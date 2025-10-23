package me.yxp.qfun.utils.qq;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import me.yxp.qfun.utils.data.DataUtils;
import me.yxp.qfun.utils.error.ErrorOutput;
import me.yxp.qfun.utils.reflect.ClassUtils;
import me.yxp.qfun.utils.reflect.FieldUtils;
import me.yxp.qfun.utils.reflect.MethodUtils;

public class AudioConverterUtil {
    private static final String TAG = "AudioConverterUtil";
    private static final ExecutorService sExecutor = Executors.newSingleThreadExecutor();

    private static Class<?> sSilkCodecWrapper;
    private static Class<?> sPttBufferImpl;
    private static Method sInitSilkCodecWrapper;
    private static Method sReadMethod;
    private static Method sReleaseMethod;
    private static Method sCreateBufferTask;
    private static Method sAppendBuffer;
    private static Method sFlushMethod;

    static {
        try {
            initMethod();
        } catch (Throwable th) {
            ErrorOutput.Error("AudioConverterUtil", th);
        }
    }

    public static void convertMp3ApiToSilk(String apiUrl, String outputPath, ConversionCallback callback) {
        sExecutor.execute(() -> {
            try {
                callback.onProgress(0, "开始下载MP3数据");
                byte[] mp3Data = fetchMp3FromApi(apiUrl);

                if (mp3Data == null || mp3Data.length == 0) {
                    callback.onError(new IOException("获取MP3数据失败"));
                    return;
                }

                callback.onProgress(30, "MP3数据下载完成，开始转换为PCM");
                byte[] pcmData = convertMp3ToPcm(mp3Data);

                if (pcmData == null || pcmData.length == 0) {
                    callback.onError(new IOException("MP3转PCM失败"));
                    return;
                }

                callback.onProgress(70, "PCM转换完成，开始转换为SILK");
                convertAndWriteSilkFile(pcmData, outputPath);
                callback.onProgress(100, "转换完成");
                callback.onSuccess(outputPath);

            } catch (Throwable th) {
                callback.onError(th);
            }
        });
    }

    private static byte[] fetchMp3FromApi(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            Log.e(TAG, "获取MP3数据失败", e);
        }
        return null;
    }

    private static byte[] convertMp3ToPcm(byte[] mp3Data) {
        java.io.File tempFile = null;
        MediaExtractor extractor = null;
        MediaCodec decoder = null;

        try {
            tempFile = DataUtils.createFile("temp", "temp.mp3");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(mp3Data);
            }

            extractor = new MediaExtractor();
            extractor.setDataSource(tempFile.getAbsolutePath());

            int audioTrackIndex = findAudioTrack(extractor);
            if (audioTrackIndex == -1) {
                Log.e(TAG, "未找到音频轨道");
                return null;
            }

            extractor.selectTrack(audioTrackIndex);
            MediaFormat format = extractor.getTrackFormat(audioTrackIndex);

            String mime = format.getString(MediaFormat.KEY_MIME);
            decoder = MediaCodec.createDecoderByType(mime);
            decoder.configure(format, null, null, 0);
            decoder.start();

            return decodeAudioData(extractor, decoder);

        } catch (Exception e) {
            Log.e(TAG, "MP3转PCM失败", e);
            return null;
        } finally {
            cleanupResources(decoder, extractor, tempFile);
        }
    }

    private static int findAudioTrack(MediaExtractor extractor) {
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                return i;
            }
        }
        return -1;
    }

    private static byte[] decodeAudioData(MediaExtractor extractor, MediaCodec decoder) throws IOException {
        ByteBuffer[] inputBuffers = decoder.getInputBuffers();
        ByteBuffer[] outputBuffers = decoder.getOutputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        ByteArrayOutputStream pcmStream = new ByteArrayOutputStream();

        while (!sawOutputEOS) {
            if (!sawInputEOS) {
                int inputBufferIndex = decoder.dequeueInputBuffer(10000);
                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                    int sampleSize = extractor.readSampleData(inputBuffer, 0);

                    if (sampleSize < 0) {
                        sawInputEOS = true;
                        decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        long presentationTimeUs = extractor.getSampleTime();
                        decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0);
                        extractor.advance();
                    }
                }
            }

            int outputBufferIndex = decoder.dequeueOutputBuffer(info, 10000);
            if (outputBufferIndex >= 0) {
                if (info.size > 0) {
                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                    outputBuffer.position(info.offset);
                    outputBuffer.limit(info.offset + info.size);

                    byte[] chunk = new byte[info.size];
                    outputBuffer.get(chunk);
                    pcmStream.write(chunk);
                }

                decoder.releaseOutputBuffer(outputBufferIndex, false);

                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    sawOutputEOS = true;
                }
            }
        }

        return pcmStream.toByteArray();
    }

    private static void cleanupResources(MediaCodec decoder, MediaExtractor extractor, java.io.File tempFile) {
        if (decoder != null) {
            decoder.stop();
            decoder.release();
        }
        if (extractor != null) {
            extractor.release();
        }
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    private static void convertAndWriteSilkFile(byte[] pcmData, String path) throws Throwable {
        Object silkCodecWrapper = ClassUtils.makeDefaultObject(sSilkCodecWrapper, HostInfo.getHostContext());
        Object pttBufferImpl = ClassUtils.makeDefaultObject(sPttBufferImpl);

        sInitSilkCodecWrapper.invoke(silkCodecWrapper, 16000, 16000, 1);
        sCreateBufferTask.invoke(pttBufferImpl, path);

        byte[] headBuffer = new byte[]{2, 35, 33, 83, 73, 76, 75, 95, 86, 51};
        sAppendBuffer.invoke(pttBufferImpl, path, headBuffer, 10);

        processPcmData(pcmData, silkCodecWrapper, pttBufferImpl, path);
        sFlushMethod.invoke(pttBufferImpl, path);
        sReleaseMethod.invoke(silkCodecWrapper);
    }

    private static void processPcmData(byte[] pcmData, Object silkCodecWrapper, Object pttBufferImpl, String path) throws Exception {
        final int total = pcmData.length;
        final int blockSize = 640;
        int times = total / blockSize;
        int remain = total % blockSize;

        for (int i = 0; i < times; i++) {
            byte[] inputBlock = Arrays.copyOfRange(pcmData, i * blockSize, (i + 1) * blockSize);
            processDataBlock(silkCodecWrapper, pttBufferImpl, path, inputBlock);
        }

        if (remain > 0) {
            byte[] inputBlock = Arrays.copyOfRange(pcmData, times * blockSize, total);
            processDataBlock(silkCodecWrapper, pttBufferImpl, path, inputBlock);
        }
    }

    private static void processDataBlock(Object silkCodecWrapper, Object pttBufferImpl, String path, byte[] inputBlock) throws Exception {
        Object output = sReadMethod.invoke(silkCodecWrapper, inputBlock, 0, inputBlock.length);
        byte[] outputBlock = (byte[]) FieldUtils.create(output).ofType(byte[].class).getValue();

        int length = getOutputLength(output);
        if (length > 0) {
            sAppendBuffer.invoke(pttBufferImpl, path, outputBlock, length);
        }
    }

    private static int getOutputLength(Object output) throws Exception {
        for (Field field : output.getClass().getDeclaredFields()) {
            if (field.getType() == int.class) {
                field.setAccessible(true);
                int value = (int) field.get(output);
                if (value != 0) return value;
            }
        }
        return 0;
    }

    private static void initMethod() throws Throwable {
        sSilkCodecWrapper = ClassUtils.load("com.tencent.mobileqq.utils.SilkCodecWrapper");
        sPttBufferImpl = ClassUtils.load("com.tencent.mobileqq.pttlogic.api.impl.PttBufferImpl");

        sInitSilkCodecWrapper = MethodUtils.create(sSilkCodecWrapper)
                .withReturnType(void.class)
                .withParamTypes(int.class, int.class, int.class)
                .findOne();

        sReadMethod = MethodUtils.create(sSilkCodecWrapper)
                .withParamTypes(byte[].class, int.class, int.class)
                .findAll()
                .stream()
                .filter(m -> m.getReturnType() != int.class)
                .collect(Collectors.toList())
                .get(0);

        sReleaseMethod = MethodUtils.create(sSilkCodecWrapper)
                .withMethodName("release")
                .findOne();

        sCreateBufferTask = MethodUtils.create(sPttBufferImpl)
                .withMethodName("createBufferTask")
                .findOne();

        sAppendBuffer = MethodUtils.create(sPttBufferImpl)
                .withMethodName("appendBuffer")
                .findOne();

        sFlushMethod = MethodUtils.create(sPttBufferImpl)
                .withMethodName("flush")
                .findOne();
    }

    public interface ConversionCallback {
        void onProgress(int progress, String message);

        void onSuccess(String outputPath);

        void onError(Throwable th);
    }
}