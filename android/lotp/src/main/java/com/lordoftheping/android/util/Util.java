package com.lordoftheping.android.util;

public class Util {

    public static String getImageWithNewSize(String url, int size) {
        if (url.contains("sz=")) {
            String newString = url.substring(0, url.indexOf("sz=") + 3);
            return newString + size;
        } else {
            return url;
        }
    }
}
