package gui;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Small helpers to show money, dates and text the same way on every screen
final class Format {
    private static final NumberFormat MONEY = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private Format() {
    }

    static String money(double value) {
        return MONEY.format(value);
    }

    static String date(String databaseDate) {
        if (databaseDate == null) return "-";
        return LocalDateTime.parse(databaseDate.replace(' ', 'T')).format(DATE);
    }

    // Escapes user text before putting it inside an HTML label
    static String html(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    static String shorten(String text, int maxLength) {
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 1) + "…";
    }
}
