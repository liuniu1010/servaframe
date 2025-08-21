package org.neo.servaframe;

import java.io.InputStream;
import java.io.FileInputStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.neo.servaframe.util.IOUtil;

class IOUtilTest {

    @Test
    void testBase64() throws Exception {
        try (InputStream in = new FileInputStream("/tmp/dogandcat.png")) {
            String base64 = IOUtil.inputStreamToRawBase64(in);
            System.out.println("base64 = " + base64);
        }
    }

    @Test
    void testBytesToFile() throws Exception {
        String filePath = "/tmp/dogandcat.png";
        byte[] bytes = IOUtil.fileToBytes(filePath);
        String newFilePath = "/tmp/temp.png";
        IOUtil.bytesToFile(bytes, newFilePath);
        byte[] bytes2 = IOUtil.fileToBytes(newFilePath);
        assertEquals(bytes.length, bytes2.length);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], bytes2[i]);
        }
    }
}

