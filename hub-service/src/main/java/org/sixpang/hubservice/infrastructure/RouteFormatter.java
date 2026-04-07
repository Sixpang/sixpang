package org.sixpang.hubservice.infrastructure;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RouteFormatter {
    private RouteFormatter() {}

    public static String formatDistance(BigDecimal distance) {
        if (distance == null) return null;
        return distance.setScale(2, RoundingMode.HALF_UP) + " km";
    }

    public static String formatDuration(Long durationMs) {
        if (durationMs == null) return null;

        long totalMinutes = durationMs / 1000 / 60;
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours == 0) {
            return minutes + " min";
        }

        return hours + " h " + minutes + " min";
    }
}
