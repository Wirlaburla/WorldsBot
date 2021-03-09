package net.WorldsBot;

import org.simpleyaml.configuration.file.YamlFile;

/**
 * -- Message Controller
 * Handles messages configured with the configuration file. Messages set here
 * are output through both Discord and Worlds.
 */

public class MessageControl {

	static YamlFile main = GetConfig.main();

	// Config file messages
	public static String InvalidArguments = main.getString("Message.InvalidArguments");
	public static String Linked = main.getString("Message.Linked");
	public static String Unlinked = main.getString("Message.Unlinked");
	public static String SetPrefix = main.getString("Message.SetPrefix");

}
