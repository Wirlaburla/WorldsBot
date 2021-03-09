package net.WorldsBot.worlds;

import net.dv8tion.jda.api.utils.MarkdownSanitizer;

/**
 * -- Information Type
 * Type class that stores a title and a value for the Information output of Worlds.
 * Values are combined into one multi-line string before being called here. No plans
 * to change this.
 */

public class Info {

    public String title;
    public String value;

    public Info(String title, String value) {
        this.title = MarkdownSanitizer.escape(title);
        this.value = MarkdownSanitizer.escape(value);
    }

}
