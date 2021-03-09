package net.WorldsBot;

import net.WorldsBot.discord.DiscordListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

/**
 * -- Main
 * Main class for running directly and initializing JDA, the library used for this Discord Bot.
 */

public class Main {

    public static void main(String[] args) {
        JDABuilder builder = JDABuilder.createDefault(GetConfig.main().getString("token"));
        builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.ZLIB);

        builder.addEventListeners(new DiscordListener());

        try {
            builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        System.out.println("https://discord.com/api/oauth2/authorize?client_id=619330060981108768&scope=bot");
    }
}
