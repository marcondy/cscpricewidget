package com.cryptospective.marco.cscpricewidget;

public class ColorHelper {
    public static String checkColor(String color) {
        int countZero;
        countZero = 8 - color.length();
        String result = "";
        for(int i = 0; i < countZero; i++){
            result = result + "0";
        }
        return "#" + result + color;
    }


}
