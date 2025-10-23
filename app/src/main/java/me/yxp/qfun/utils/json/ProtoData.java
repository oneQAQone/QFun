package me.yxp.qfun.utils.json;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProtoData {
    private static final int WIRETYPE_VARINT = 0;
    private static final int WIRETYPE_FIXED64 = 1;
    private static final int WIRETYPE_LENGTH_DELIMITED = 2;
    private static final int WIRETYPE_FIXED32 = 5;

    private final HashMap<Integer, List<Object>> mValues = new HashMap<>();

    public void fromJSON(JSONObject json) {
        try {
            Iterator<String> keyIterator = json.keys();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                int fieldNumber = Integer.parseInt(key);
                Object value = json.get(key);
                processJsonValue(fieldNumber, value);
            }
        } catch (Exception ignored) {
            // 忽略解析异常
        }
    }

    private void processJsonValue(int fieldNumber, Object value) {
        if (value instanceof JSONObject) {
            ProtoData newProto = new ProtoData();
            newProto.fromJSON((JSONObject) value);
            putValue(fieldNumber, newProto);
        } else if (value instanceof JSONArray) {
            processJsonArray(fieldNumber, (JSONArray) value);
        } else {
            putValue(fieldNumber, value);
        }
    }

    private void processJsonArray(int fieldNumber, JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                Object arrayObj = array.get(i);
                if (arrayObj instanceof JSONObject) {
                    ProtoData newProto = new ProtoData();
                    newProto.fromJSON((JSONObject) arrayObj);
                    putValue(fieldNumber, newProto);
                } else {
                    putValue(fieldNumber, arrayObj);
                }
            } catch (Exception e) {
                // 忽略数组元素解析异常
            }
        }
    }

    private void putValue(int key, Object value) {
        List<Object> list = mValues.computeIfAbsent(key, k -> new ArrayList<>());
        list.add(value);
    }

    public void fromBytes(byte[] data) throws IOException {
        byte[] processedData = getUnpPackage(data);
        CodedInputStream input = CodedInputStream.newInstance(processedData);

        while (input.getBytesUntilLimit() > 0) {
            int tag = input.readTag();
            int fieldNumber = tag >>> 3;
            int wireType = tag & 7;

            processWireType(fieldNumber, wireType, input);
        }
    }

    private void processWireType(int fieldNumber, int wireType, CodedInputStream input) throws IOException {
        switch (wireType) {
            case WIRETYPE_VARINT:
                putValue(fieldNumber, input.readInt64());
                break;
            case WIRETYPE_FIXED64:
                putValue(fieldNumber, input.readRawVarint64());
                break;
            case WIRETYPE_LENGTH_DELIMITED:
                processLengthDelimited(fieldNumber, input);
                break;
            case WIRETYPE_FIXED32:
                putValue(fieldNumber, input.readFixed32());
                break;
            default:
                putValue(fieldNumber, "Unknown wireType: " + wireType);
                break;
        }
    }

    private void processLengthDelimited(int fieldNumber, CodedInputStream input) throws IOException {
        byte[] subBytes = input.readByteArray();
        try {
            ProtoData subData = new ProtoData();
            subData.fromBytes(subBytes);
            putValue(fieldNumber, subData);
        } catch (Exception e) {
            putValue(fieldNumber, new String(subBytes));
        }
    }

    public JSONObject toJSON() throws Exception {
        JSONObject result = new JSONObject();
        for (Integer fieldNumber : mValues.keySet()) {
            List<?> valueList = mValues.get(fieldNumber);
            if (valueList.size() > 1) {
                JSONArray array = new JSONArray();
                for (Object value : valueList) {
                    array.put(valueToJsonObject(value));
                }
                result.put(String.valueOf(fieldNumber), array);
            } else {
                Object value = valueList.get(0);
                result.put(String.valueOf(fieldNumber), valueToJsonObject(value));
            }
        }
        return result;
    }

    private Object valueToJsonObject(Object value) throws Exception {
        if (value instanceof ProtoData) {
            return ((ProtoData) value).toJSON();
        } else {
            return value;
        }
    }

    public byte[] toBytes() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(byteStream);

        try {
            for (Integer fieldNumber : mValues.keySet()) {
                List<?> valueList = mValues.get(fieldNumber);
                for (Object value : valueList) {
                    encodeValue(fieldNumber, value, output);
                }
            }
            output.flush();
            return byteStream.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private void encodeValue(int fieldNumber, Object value, CodedOutputStream output) throws IOException {
        if (value instanceof Long) {
            output.writeInt64(fieldNumber, (Long) value);
        } else if (value instanceof String) {
            output.writeByteArray(fieldNumber, ((String) value).getBytes());
        } else if (value instanceof ProtoData) {
            byte[] subBytes = ((ProtoData) value).toBytes();
            output.writeByteArray(fieldNumber, subBytes);
        } else if (value instanceof Integer) {
            output.writeInt32(fieldNumber, (Integer) value);
        }
        // 其他类型暂时忽略
    }

    private byte[] getUnpPackage(byte[] data) {
        if (data == null || data.length < 4) {
            return data;
        }

        if (data[0] == 0) {
            return Arrays.copyOfRange(data, 4, data.length);
        } else {
            return data;
        }
    }
}
