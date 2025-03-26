package org.jeecg.modules.learntime.utils;

public class StringUtil {
    public static String isNotEmptyThenReplace(String str){
        if (str != null) {
            return str.replace("*","");
        }
        return "";
    }
}
