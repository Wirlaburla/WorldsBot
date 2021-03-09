package net.WorldsBot.discord;

import net.WorldsBot.Message;
import org.simpleyaml.configuration.file.YamlFile;

/**
 * -- Main
 * Simple Account type class that handles user configuration files.
 */

public class Account {

    YamlFile file;

    public Account(String name) {
        file = new YamlFile(Message.format("accounts/{0}.yml", new String[] { name }));
        try {
            if (!file.exists()) file.createNewFile(true);
            file.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
