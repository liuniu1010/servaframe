package org.neo.servaframe;


import java.io.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.neo.servaframe.util.*;

/**
 * Unit test for simple App.
 */
public class IOUtilTest 
    extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public IOUtilTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( IOUtilTest.class );
    }

    public void testBase64() throws Exception {
        try (InputStream in = new FileInputStream("/tmp/dogandcat.png")) {
            String base64 = IOUtil.inputStreamToRawBase64(in);
            System.out.println("base64 = " + base64);
        }
    }

    public void testBytesToFile() throws Exception {
        String filePath = "/tmp/dogandcat.png";
        byte[] bytes = IOUtil.fileToBytes(filePath);
        String newFilePath = "/tmp/temp.png";
        IOUtil.bytesToFile(bytes, newFilePath);
        byte[] bytes2 = IOUtil.fileToBytes(newFilePath);
        assertEquals(bytes.length, bytes2.length);
        for(int i = 0;i < bytes.length;i++) {
            assertEquals(bytes[i], bytes2[i]);
        }
    }
}
