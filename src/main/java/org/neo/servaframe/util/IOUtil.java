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
import java.util.Base64;

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
        try(FileInputStream fin = new FileInputStream(filePath)) {
            return inputStreamToString(fin);
        }
    }

    public static void resourceFileToFile(String fileName, String filePath) throws IOException, FileNotFoundException {
        ClassLoader classLoader = IOUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        IOUtil.inputStreamToFile(inputStream, filePath);
    }

    public static String resourceFileToString(String fileName) throws IOException, FileNotFoundException {
        ClassLoader classLoader = IOUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        return IOUtil.inputStreamToString(inputStream);
    }

    public static void stringToFile(String content, String filePath) throws IOException {
        try(FileOutputStream fout = new FileOutputStream(filePath)) {
            stringToOutputStream(content, fout);
            fout.flush();
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

    public static byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    public static String inputStreamToRawBase64(InputStream inputStream) throws IOException {
        return Base64.getEncoder().encodeToString(inputStreamToBytes(inputStream));
    }

    public static byte[] rawBase64ToBytes(String rawBase64) {
        return Base64.getDecoder().decode(rawBase64);
    }

    public String bytesToRawBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void rawBase64ToFile(String rawBase64, String filePath) throws IOException {
        bytesToFile(rawBase64ToBytes(rawBase64), filePath); 
    }

    public static String resourceFileToRawBase64(String fileName) throws IOException {
        ClassLoader classLoader = IOUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        return inputStreamToRawBase64(inputStream);
    }

    public static String fileToRawBase64(String filePath) throws IOException {
        try(FileInputStream fin = new FileInputStream(filePath)) {
            return inputStreamToRawBase64(fin);
        }
    }

    public static void inputStreamToFile(InputStream inputStream, String filePath) throws IOException {
        try(FileOutputStream fout = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fout.write(buffer, 0, bytesRead);
            }
            fout.flush();
        }
    }

    public static void bytesToFile(byte[] bytes, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
        }
    }

    public static byte[] fileToBytes(String filePath) throws Exception {
        try (FileInputStream fin = new FileInputStream(filePath)) {
            return inputStreamToBytes(fin); 
        } 
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
