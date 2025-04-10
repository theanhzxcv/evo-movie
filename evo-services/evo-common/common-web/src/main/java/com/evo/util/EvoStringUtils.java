package com.evo.util;

public class EvoStringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String escapeSqlLike(String input) {
        if (input == null) {
            return null;
        }

        return input.replace("\\", "\\\\")
                .replace("_", "\\_")
                .replace("%", "\\%");
    }

    public static String sqlStringSearch(String str, boolean isLike) {
        if (str == null) {
            return isLike ? "%" : "";
        }
        String processed = str.toLowerCase().trim();
        String escaped = escapeSqlLike(processed);
        return isLike ? "%" + escaped + "%" : escaped;
    }
}
