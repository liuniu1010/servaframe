package org.neo.servaframe.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

/***
 * utility class to provide methods about IO related functions
 *
 */
public class IOUtil {
    public static byte[] objToBytes(Object obj) throws IOException {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream out = null;
        try {
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            return bos.toByteArray();
        }
        finally {
            closeOutputStream(out);
        }
    }

    public static Object bytesToObj(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = null;
        ObjectInputStream in = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(bis);
            return in.readObject();
        }
        finally {
            closeInputStream(in);
        }
    }

    public static Object clone(Object origObject) throws IOException, ClassNotFoundException {
        return bytesToObj(objToBytes(origObject));
    }

    public static String fileToString(String filePath) throws IOException, FileNotFoundException {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(filePath);
            return inputStreamToString(fin);
        }
        finally {
            closeInputStream(fin);
        }
    }

    public static void stringToFile(String content, String filePath) throws IOException {
        FileOutputStream fout = null;
        try {
            File file = new File(filePath);
            fout = new FileOutputStream(file);
            stringToOutputStream(content, fout);
            fout.flush();
        }
        finally {
            closeOutputStream(fout);
        }
    }

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder sb = new StringBuilder();
        InputStreamReader in = null;
        in = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        while(true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            sb.append(buffer, 0, rsz);
        }
        return sb.toString();        
    }

    public static void stringToOutputStream(String content, OutputStream outputStream) throws IOException {
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
    }

    public static void closeInputStream(InputStream in) {
        if(in == null)
            return;
        try {
            in.close();
        }
        catch(IOException ioe) {
        }
    }

    public static void closeOutputStream(OutputStream out) {
        if(out == null)
            return;
        try {
            out.close();
        }
        catch(IOException ioe) {
        }
    }

    public static void closeBufferedReader(BufferedReader br) {
        if(br == null)
            return;
        try {
            br.close();
        }
        catch(IOException ioe) {
        }
    }
}
