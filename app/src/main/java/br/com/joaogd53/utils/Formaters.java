package br.com.joaogd53.utils;

import java.text.DecimalFormat;

public class Formaters {

    public static String formatDouble(double value) {
        DecimalFormat format = new DecimalFormat("#.0000");
        return format.format(value);
    }
}
