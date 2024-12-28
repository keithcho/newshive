package cloud.newshive.mini_project.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

@Component
public class DateTimeUtils {
    
    /**
     * Converts ISO 8601 datetime string to "x time ago" format
     * @param isoDateTime ISO 8601 datetime string
     * @return Relative time string (e.g. "2 hours ago")
     */
    public String getTimeAgo(String isoDateTime) {
        Instant instant = Instant.parse(isoDateTime);
        Instant now = Instant.now();
        
        long minutes = ChronoUnit.MINUTES.between(instant, now);
        long hours = ChronoUnit.HOURS.between(instant, now);
        long days = ChronoUnit.DAYS.between(instant, now);
        
        if (minutes < 60) {
            return minutes + " minute" + (minutes != 1 ? "s" : "") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours != 1 ? "s" : "") + " ago";
        } else {
            return days + " day" + (days != 1 ? "s" : "") + " ago";
        }
    }
    
    /**
     * Formats ISO 8601 datetime string to a custom pattern
     * @param isoDateTime ISO 8601 datetime string
     * @param pattern DateTimeFormatter pattern
     * @return Formatted datetime string
     */
    public String formatDateTime(String isoDateTime, String pattern) {
        Instant instant = Instant.parse(isoDateTime);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
}