package com.lhamacorp.springocrtesseract;

public class Common {

    public static final String DEFAULT_LANG = "eng";
    public static final String DEFAULT_DIR = "./files/";

    public static String getPath(String id) {
        return DEFAULT_DIR + id;
    }

}
