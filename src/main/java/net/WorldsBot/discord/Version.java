package net.WorldsBot.discord;

import net.WorldsBot.Main;

import java.io.IOException;
import java.util.Properties;

/**
 * -- Version
 * Small version class that outputs the version of the project obtained
 * from the pom file.
 */

public class Version {

	public static String getVersion(){
		try {
			final Properties properties = new Properties();
			properties.load(Main.class.getClassLoader().getResourceAsStream("project.properties"));
			return properties.getProperty("version");
		} catch (IOException e) {
			return "*Unknown*";
		}
	}
}
