package com.company.common.util;

import java.time.Instant;

public final class DateUtil {
    private DateUtil() {
    }

    public static String nowIso() {
        return Instant.now().toString();
    }
}
