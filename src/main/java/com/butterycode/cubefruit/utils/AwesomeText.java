package com.butterycode.cubefruit.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AwesomeText {

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&' , string);
    }

    public static String decolorize(String string) {
        return ChatColor.stripColor(string);
    }

    public static String colorizeHex(String string) {
        Pattern pattern = Pattern.compile("&#[0-9A-F]{6}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);

        pattern.matcher(string).matches();
        List<String> strout = Arrays.asList(pattern.split(string));

        while (matcher.find()) {
            String strColor = matcher.group();
            String strHex = strColor.replaceFirst("&#", "#");
            ChatColor color = ChatColor.of(strHex);

            List<String> rest = strout.stream().skip(1).collect(Collectors.toList());
            rest.set(0, strout.get(0) + color + rest.get(0)); // FIXME: index out of bounds for length 0 error

            strout = rest;
        }

        return colorize(String.join("", strout));
    }

    public static String importPlaceholders(String message, Player player) {
        String newmessage = message;

        newmessage = PluginSupport.getPlaceholders(player, message);
        newmessage = replacePlaceholder(newmessage, "player_name", player.getName());
        newmessage = replacePlaceholder(newmessage, "player_displayname", player.getDisplayName());
        newmessage = replacePlaceholder(newmessage, "player_list_name", player.getPlayerListName());
        newmessage = replacePlaceholder(newmessage, "player_uuid", player.getUniqueId().toString());

        return newmessage;
    }

    public static String replacePlaceholder(String message, String stringName, String placeholder) {
        return message.replaceAll(Pattern.quote("%" + stringName + "%"), Matcher.quoteReplacement(placeholder));
    }

    /**
     * Prettify a message with multiple formatting functions
     * @param message Input message
     * @return Prettified message
     */
    public static String prettifyMessage(String message) {
        String newmessage = message;

        // TODO: add compacted numbers
        newmessage = AwesomeText.colorizeHex(newmessage); // Colorize string

        return newmessage;
    }

    /**
     * Prettify a message with multiple formatting functions and player imports
     * @param message Input message
     * @param player Player to parse imports against
     * @return Prettified message
     */
    public static String prettifyMessage(String message, Player player) {
        String newmessage = message;

        // TODO: add compacted numbers
        newmessage = AwesomeText.importPlaceholders(newmessage, player);
        newmessage = prettifyMessage(newmessage);

        return newmessage;
    }

    public static String compactNumber(double number) { // TODO: retest
        double notNum = number;
        String[] strTiers = new String[] {"K", "M", "B", "T", "Q", "QT"}; // TODO: Add more abbreviations
        int intTiers = 0;
        while (number >= 1000) {
            number = number / 1000;
            intTiers = intTiers + 1;
        }
        int subInt = (int) Math.floor((number - Math.floor(number)) * 10);

        if (intTiers - 1 < 0) { // Return uncompacted
            if (subInt != 0) {
                return Math.floor(number) + subInt + "";
            } else {
                return (int) Math.floor(number) + "";
            }
        } else if (intTiers > strTiers.length) { // Return Scientific notation
            int notTiers = 0;
            while (notNum >= 10) {
                notNum = notNum / 10;
                notTiers = notTiers + 1;
            }

            int subNot = (int) Math.floor((notNum - Math.floor(notNum)) * 10);
            if (subInt != 0) {
                return (int) Math.floor(notNum) + "." + subNot + "e" + notTiers;
            } else {
                return (int) Math.floor(notNum) + "e" + notTiers;
            }
        } else { // Return compaction
            if (subInt != 0) {
                return (int) Math.floor(number) + "." + subInt + strTiers[intTiers - 1];
            } else {
                return (int) Math.floor(number) + strTiers[intTiers - 1];
            }
        }
    }

    public static String romanNumeral(int number) {
        List<Object[]> rules = new ArrayList<>();

        rules.add(new Object[] {"m", 1000000});
        rules.add(new Object[] {"cm", 900000});
        rules.add(new Object[] {"c", 100000});
        rules.add(new Object[] {"xc", 90000});
        rules.add(new Object[] {"x", 10000});
        rules.add(new Object[] {"Mx", 9000});
        rules.add(new Object[] {"v", 5000});
        rules.add(new Object[] {"Mv", 4000});
        rules.add(new Object[] {"M", 1000});
        rules.add(new Object[] {"CM", 900});
        rules.add(new Object[] {"D", 500});
        rules.add(new Object[] {"CD", 400});
        rules.add(new Object[] {"C", 100});
        rules.add(new Object[] {"XC", 90});
        rules.add(new Object[] {"L", 50});
        rules.add(new Object[] {"XL", 40});
        rules.add(new Object[] {"XXX", 30});
        rules.add(new Object[] {"XX", 20});
        rules.add(new Object[] {"X", 10});
        rules.add(new Object[] {"IX", 9});
        rules.add(new Object[] {"V", 5});
        rules.add(new Object[] {"IV", 4});
        rules.add(new Object[] {"I", 1});

        String roman = "";
        for (Object[] rule : rules) {
            String key = (String) rule[0];
            int value = (int) rule[1];

            while (number >= value) {
                number -= value;
                roman += key;
            }
        }

        return roman;
    }

    public static String capitalize(String message) {
        return message.substring(0, 1).toUpperCase() + message.substring(1);
    }

    public static String screamingSnakeCase(String string) {
        string = string.toLowerCase();
        String newString = "";

        for (String sub : string.split("_")) {
            if (newString != "") newString += " ";
            newString += capitalize(sub);
        }

        return newString;
    }

    public static String commaAndSeparatedList(ArrayList<String> list) {
        if (list.size() == 2) {
            return list.get(0) + " and " + list.get(1);
        }

        String joined = "";
        String lastItem = null;

        if (list.size() >= 3) {
            lastItem = list.get(list.size() - 1);
            list.remove(list.size() - 1);
        }

        joined = list.stream().reduce((str1, str2) -> str1 + ", " + str2).orElse("");

        if (lastItem != null) {
            joined += ", and " + lastItem;
        }

        return joined;
    }

    public static String commaOrSeparatedList(ArrayList<String> list) {
        if (list.size() == 2) {
            return list.get(0) + " or " + list.get(1);
        }

        String joined = "";
        String lastItem = null;

        if (list.size() >= 3) {
            lastItem = list.get(list.size() - 1);
            list.remove(list.size() - 1);
        }

        joined = list.stream().reduce((str1, str2) -> str1 + ", " + str2).orElse("");

        if (lastItem != null) {
            joined += ", or " + lastItem;
        }

        return joined;
    }

    public static String addDelimiters(double number) {
        String string = String.valueOf(number);

        String[] decimal = string.split("\\.");
        String strnumber = decimal[0];
        String strdecimal = "";
        if (decimal.length > 0)
            strdecimal = decimal[1];

        String[] strcut = reverseString(strnumber).split("(?<=\\G.{3})");
        String newstring = reverseString(String.join(",", strcut));
        if (strdecimal != "")
            newstring += "." + strdecimal;

        return newstring;
    }

    public static String addDelimiters(float number) {
        String string = String.valueOf(number);

        String[] decimal = string.split("\\.");
        String strnumber = decimal[0];
        String strdecimal = "";
        if (decimal.length > 0)
            strdecimal = decimal[1];

        String[] strcut = reverseString(strnumber).split("(?<=\\G.{3})");
        String newstring = reverseString(String.join(",", strcut));
        if (strdecimal != "")
            newstring += "." + strdecimal;

        return newstring;
    }

    public static String addDelimiters(int number) {
        String string = String.valueOf(number);

        String[] strcut = reverseString(string).split("(?<=\\G.{3})");
        String newstring = reverseString(String.join(",", strcut));

        return newstring;
    }

    public static String reverseString(String string) {
        StringBuilder builder = new StringBuilder();
        builder.append(string);
        builder.reverse();
        return builder.toString();
    }

    public static String parseTime(double seconds) {
        String strout = "";

        if (seconds >= 315360000) {
            int amount = (int) Math.floor(seconds / 315360000);
            if (!strout.equals("")) strout += " ";
            strout += amount + " decade";
            if (amount > 1) strout += "s";
            seconds -= amount * 315360000;
        }
        if (seconds >= 31536000) {
            int amount = (int) Math.floor(seconds / 31536000);
            if (!strout.equals("")) strout += " ";
            strout += amount + " year";
            if (amount > 1) strout += "s";
            seconds -= amount * 31536000;
        }
        if (seconds >= 2628000) {
            int amount = (int) Math.floor(seconds / 2628000);
            if (!strout.equals("")) strout += " ";
            strout += amount + " month";
            if (amount > 1) strout += "s";
            seconds -= amount * 2628000;
        }
        if (seconds >= 604800) {
            int amount = (int) Math.floor(seconds / 604800);
            if (!strout.equals("")) strout += " ";
            strout += amount + " week";
            if (amount > 1) strout += "s";
            seconds -= amount * 604800;
        }
        if (seconds >= 86400) {
            int amount = (int) Math.floor(seconds / 86400);
            if (!strout.equals("")) strout += " ";
            strout += amount + " day";
            if (amount > 1) strout += "s";
            seconds -= amount * 86400;
        }
        if (seconds >= 3600) {
            int amount = (int) Math.floor(seconds / 3600);
            if (!strout.equals("")) strout += " ";
            strout += amount + " hour";
            if (amount > 1) strout += "s";
            seconds -= amount * 3600;
        }
        if (seconds >= 60) {
            int amount = (int) Math.floor(seconds / 60);
            if (!strout.equals("")) strout += " ";
            strout += amount + " minute";
            if (amount > 1) strout += "s";
            seconds -= amount * 60;
        }
        if (seconds >= 0) {
            if (!strout.equals("")) strout += " ";

            double decimal = Caboodle.round(seconds % 1, 3);
            int integer = (int) seconds;
            String time = decimal > 0 ? String.valueOf(integer + decimal) : String.valueOf(integer);

            strout += time + " seconds";
        }

        return strout;
    }

    public static String removePrefix(String string, String prefix) {
        if (string.startsWith(prefix)) {
            return string.substring(prefix.length());
        }
        return string;
    }

    public static String removeSuffix(String string, String suffix) {
        if (string.endsWith(suffix)) {
            return string.substring(0, string.length() - suffix.length());
        }
        return string;
    }

}
