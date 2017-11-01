package com.basekit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Spirit on 2017/4/12 9:47.
 */

public class PropertiesUtils {

    private static Properties prop = new Properties();

    public static String getProperty(String fileName, String key) {
        String value = "";
        InputStream in = null;
        FileInputStream fis = null;
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fis = new FileInputStream(fileName);
            //in = PropertiesUtils.class.getResourceAsStream(fileName);
            prop.load(fis);
            value = prop.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    //in.close();
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public static void setProperty(String fileName, String key, String value) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            prop.setProperty(key, value);
            prop.store(fos, "Update key:" + key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
