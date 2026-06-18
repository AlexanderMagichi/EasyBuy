package com.teamchallenge.easybuy.infrastructure.util;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}