package com.rtbasia.rtbsdk.data.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {
    public static void closeGracefully(Closeable closeable) {
        if (null == closeable) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
