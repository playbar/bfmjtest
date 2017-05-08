package com.baofeng.mojing.sdk.glhelper;

/**
 * Created by lixianke on 2017/4/26.
 */

import java.io.IOException;
import java.io.InputStream;

public class KtxLoader {
    public KtxLoader() {
    }

    public int LoadTextureFromStream(InputStream is, Texture texture) {
        boolean length = false;

        byte[] buffer;
        try {
            int length1 = is.available();
            buffer = new byte[length1];
            is.read(buffer);
        } catch (IOException var6) {
            var6.printStackTrace();
            return 8;
        }

        return this.LoadTextureFromMemory(buffer, texture);
    }

    public native int LoadTextureFromMemory(byte[] var1, Texture var2);

    public native int LoadTextureFromFile(String var1, Texture var2);

    public native int SaveTextureToFile(String var1, Texture var2);

    static {
        System.loadLibrary("glHelper");
    }
}
