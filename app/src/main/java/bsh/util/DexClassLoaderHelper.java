package bsh.util;

import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.command.dexer.DxContext;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.file.DexFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DexClassLoaderHelper {
    private final DexOptions DEX_OPTIONS = new DexOptions();
    private final DxContext DX_CONTEXT = new DxContext();
    private final CfOptions CF_OPTIONS = new CfOptions();

    /**
     * 将 Java 字节码 (.class) 转换为 Android DEX 字节码
     */
    public byte[] convertClassToDex(String name, byte[] classCode) throws IOException {
        String classFilePath = String.format("%s.class", name.replace('.', '/'));
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            DirectClassFile directClassFile = new DirectClassFile(classCode, classFilePath, true);
            directClassFile.setAttributeFactory(StdAttributeFactory.THE_ONE);
            DexFile dexFile = new DexFile(DEX_OPTIONS);
            dexFile.add(CfTranslator.translate(DX_CONTEXT, directClassFile, classCode, CF_OPTIONS, DEX_OPTIONS, dexFile));
            dexFile.writeTo(byteStream, null, true);
            return byteStream.toByteArray();
        }
    }
}