package net.WorldsBot;

import net.dv8tion.jda.api.entities.Guild;
import org.simpleyaml.configuration.file.YamlFile;

/**
 * -- Configuration File Getter
 * Obtains and applies the main configuration, as well as the server
 * configurations for individual servers.
 */

public class GetConfig {

    public static YamlFile main() {
        YamlFile main = new YamlFile("config.yml");
        try {
            if (!main.exists()) main.createNewFile(true);
            main.load();
            return main;
        } catch (Exception e) {
            e.printStackTrace();
            return new YamlFile();
        }
    }

    public static YamlFile getServerConf(Guild guild) {
        try {
            YamlFile yaml = new YamlFile("servers/" + guild.getId() + ".yml");
            if (!yaml.exists()) yaml.createNewFile(true);
            yaml.load();
            yaml.addDefault("prefix", "!");
            return yaml;
        } catch (Exception e) {
            e.printStackTrace();
            return new YamlFile();
        }
    }



}
