package com.xie.framwork.audio;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioConverter {

    public static String fileToBase64(File file) {
        if (file == null) {
            throw new RuntimeException("file shoud not be null");
        }
        if (!file.exists()) {
            throw new RuntimeException("file not exsist");
        }
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        byte[] totalBytes = null;
        try {
            fis = new FileInputStream(file);
            int size = 0;
            while ((size = fis.read(buffer)) > 0) {
                baos.write(buffer);
            }
            totalBytes = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String result = Base64.encodeToString(totalBytes, Base64.DEFAULT);
        return result;
    }

    public static boolean base64ToFile(String base64Str, String filePath, String fileName) {
        if (base64Str == null) {
            return false;
        }
        byte[] bytes = Base64.decode(base64Str, Base64.DEFAULT);
        if (bytes == null || bytes.length == 0) {
            return false;
        }
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(filePath, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
