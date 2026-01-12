package me.realized.tokenmanager.util;

import me.realized.tokenmanager.util.compat.CompatUtil;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile(
            "(?i)(?:&#|#|<#|\\{#)([0-9a-f]{6})(?:>|\\})?"
    );

    private StringUtil() {
    }

    public static String fromList(final List<?> list) {
        StringBuilder builder = new StringBuilder();

        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i).toString()).append(i + 1 != list.size() ? "\n" : "");
            }
        }

        return builder.toString();
    }

    public static String color(final String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String colored = ChatColor.translateAlternateColorCodes('&', input);

        if (CompatUtil.isPre1_16()) {
            return colored;
        }

        final Matcher matcher = HEX_PATTERN.matcher(colored);
        final StringBuffer buffer = new StringBuffer(colored.length());

        while (matcher.find()) {
            final String hex = matcher.group(1);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(toChatColorHex(hex)));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String toChatColorHex(final String hex) {
        final StringBuilder builder = new StringBuilder(14);
        builder.append('§').append('x');

        for (int i = 0; i < hex.length(); i++) {
            builder.append('§').append(hex.charAt(i));
        }

        return builder.toString();
    }

    public static List<String> color(final List<String> input) {
        return color(input, null);
    }

    public static List<String> color(final List<String> input, final Function<String, String> extra) {
        input.replaceAll(s -> s = color(extra != null ? extra.apply(s) : s));
        return input;
    }

    public static String format(long seconds) {
        if (seconds <= 0) {
            return "updating...";
        }

        long years = seconds / 31556952;
        seconds -= years * 31556952;
        long months = seconds / 2592000;
        seconds -= months * 2592000;
        long weeks = seconds / 604800;
        seconds -= weeks * 604800;
        long days = seconds / 86400;
        seconds -= days * 86400;
        long hours = seconds / 3600;
        seconds -= hours * 3600;
        long minutes = seconds / 60;
        seconds -= minutes * 60;

        StringBuilder sb = new StringBuilder();

        if (years > 0) {
            sb.append(years).append("yr");
        }

        if (months > 0) {
            sb.append(months).append("mo");
        }

        if (weeks > 0) {
            sb.append(weeks).append("w");
        }

        if (days > 0) {
            sb.append(days).append("d");
        }

        if (hours > 0) {
            sb.append(hours).append("h");
        }

        if (minutes > 0) {
            sb.append(minutes).append("m");
        }

        if (seconds > 0) {
            sb.append(seconds).append("s");
        }

        return sb.toString();
    }
}
