package net.WorldsBot;

/**
 * -- Message Class
 * Handles String Message manipulation.
 */

public class Message {

    // Simple format function that replaces certain strings of characters to a string in an array.
    public static String format(String message, String[] formats) {
        String newMsg = message;
        for (int s = 0; s < formats.length; s++) {
            newMsg = newMsg.replace("{*}".replace("*", String.valueOf(s)), formats[s]);
        }
        return newMsg;
    }
}
