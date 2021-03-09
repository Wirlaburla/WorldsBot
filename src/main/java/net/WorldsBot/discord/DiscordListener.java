package net.WorldsBot.discord;

import net.WorldsBot.GetConfig;
import net.WorldsBot.Message;
import net.WorldsBot.MessageControl;
import net.WorldsBot.worlds.Info;
import net.WorldsBot.worlds.UserProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.simpleyaml.configuration.file.YamlFile;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * -- Discord Event Listener
 * Discord event listener that handles and listens to events from discord servers.
 * Currently only listens to MessageReceived but has plans to listen to more if
 * necessary.
 */

public class DiscordListener extends ListenerAdapter {

    public static Accounts acc = new Accounts();

    String prefix;

    private boolean checkPermission(Member member, Permission permission) {
        return member.hasPermission(permission);
    }

    public DiscordListener() {
        acc.register();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor() != event.getJDA().getSelfUser())
        System.out.println(Message.format("{0} {1} >> {2}", new String[] {
                LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE), event.getAuthor().getAsMention(), event.getMessage().getContentRaw()
        }));

        String[] message = event.getMessage().getContentRaw().split(" ");
        Guild curGuild = event.getGuild();
        prefix = GetConfig.getServerConf(curGuild).getString("prefix");

        // Detect if bot was mentioned without being triggered on @everyone or @here.
        // Will respond with the current server prefix. Useful for when the admins screw around a bunch.
        if (event.getMessage().getMentionedMembers().contains(curGuild.getMember(event.getJDA().getSelfUser())) && !event.getMessage().mentionsEveryone()) {
            event.getChannel().sendMessage(Message.format("My prefix is '{0}'. Use `{0}help` to get a list of commands.", new String[]{prefix})).queue();
        }

        // Checking if the message starts with the prefix.
        if (message[0].toLowerCase().startsWith(prefix)) {
            EmbedBuilder msgEmbed = new EmbedBuilder();
            String user;
            switch (message[0].substring(prefix.length())) {
                case "h":
                case "help":
                    msgEmbed.setTitle("WorldsBot Help")
                            .setColor(Color.green)
                            .setDescription("Commands for WorldsBot. `<OPTION>` is required, `[OPTION]` is optional.")
                            .addField(Message.format("`{0}help`", new String[]{prefix}), "Show this message", false)
                            //.addField(Message.format("`{0}link <WORLDS USER>`", new String[]{prefix}), "Link yourself with your Worlds account.", false)
                            //.addField(Message.format("`{0}unlink <WORLDS USER>`", new String[]{prefix}), "Unlink your Worlds account.", false)
                            //.addField(Message.format("`{0}profile [USER]`", new String[]{prefix}), "View the profile of a user.", false)
                            .addField(Message.format("`{0}info <USER>`", new String[]{prefix}), "View the Personal Information of a user.", false)
                            .addField(Message.format("`{0}vip <USER>`", new String[]{prefix}), "Check if the user is VIP.", false)
                            .addField(Message.format("`{0}about`", new String[]{prefix}), "About the bot.", false);
                    if (event.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)) {
                        msgEmbed.addField(Message.format(":tools: `{0}admin <TOOL> [OPTIONS]`", new String[]{prefix}), "Staff Configuration tools.", false);
                    }
                    event.getChannel().sendMessage(msgEmbed.build()).queue();
                    break;
                case "a":
                case "tools":
                case "admin":
                    if (!event.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)) {
                        msgEmbed.setTitle("Error 403: Forbidden")
                                .setDescription("You don't have the proper permissions to do that.")
                                .setColor(Color.red);
                    } else {
                        String firstArg;
                        try {
                            firstArg = message[1];
                        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                            firstArg = "help";
                        }
                        switch (firstArg) {
                            case "help":
                            default:
                                msgEmbed.setTitle(":tools: WorldsBot Admin Help")
                                    .setColor(Color.cyan)
                                    .setDescription("Admin Commands for WorldsBot. `<OPTION>` is required, `[OPTION]` is optional.")
                                    .addField(Message.format("`{0}admin help`", new String[]{prefix}), "Show this message", false)
                                    //.addField(Message.format("`{0}admin channel <CHANNEL ID>`", new String[]{prefix}), "Sets a channel in the server to relay GroundZero#Reception chat.", false
                                    .addField(Message.format("`{0}admin prefix <NEW PREFIX>`", new String[]{prefix}), "Sets server-wide prefix.", false);
                                break;
                            case "prefix":
                                try {
                                    if (message.length >= 3 && !message[2].isEmpty()) {
                                        YamlFile curServ = GetConfig.getServerConf(event.getGuild());
                                        curServ.set("prefix", message[2]);
                                        curServ.save();
                                        msgEmbed.setTitle("Successfully set Command prefix")
                                                .setDescription("Command prefix for your server '" + event.getGuild().getName() + "' is now `" + message[2] + "`.")
                                                .setColor(Color.green);
                                    } else {
                                        event.getChannel().sendMessage(Message.format(MessageControl.InvalidArguments, new String[]{ prefix, "admin prefix", "<CHARACTER>" }));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    msgEmbed.setTitle("Error 500: Unknown Error")
                                            .setDescription("An unknown error occured. Please contact your server staff or administrator or report a bug.")
                                            .setColor(Color.yellow);
                                }
                                break;
                        }
                    }
                    event.getChannel().sendMessage(msgEmbed.build()).queue();
                    break;
                case "info":
                case "i":
                    if (message.length < 2) {
                        event.getChannel().sendMessage(Message.format(MessageControl.InvalidArguments, new String[]{ prefix, "info", "<USER>" }));
                    } else {
                        user = MarkdownSanitizer.escape(message[1]);
                        msgEmbed.setTitle(user + "'s Information");

                        String idesc = userDesc(message[1].toLowerCase());
                        if (idesc != null) msgEmbed.setDescription(idesc);

                        List<Info> userInfo = UserProfile.getInfo(message[1]);
                        if (UserProfile.exists(message[1])) {
                            for (Info curfo : userInfo) {
                                if (!curfo.value.isEmpty()) msgEmbed.addField(curfo.title, curfo.value, false);
                            }
                        } else {
                            msgEmbed = error404(user);
                        }
                        event.getChannel().sendMessage(msgEmbed.build()).queue();
                    }
                    break;
                case "vip":
                    if (message.length < 2) {
                        event.getChannel().sendMessage(Message.format(MessageControl.InvalidArguments, new String[]{ prefix, "vip", "[USER]" }));
                    } else {
                        user = MarkdownSanitizer.escape(message[1]);
                        msgEmbed.setTitle(user + "'s VIP Status");

                        String vdesc = userDesc(message[1]);
                        if (vdesc != null) msgEmbed.setDescription(vdesc);

                        if (UserProfile.exists(user)) {
                            msgEmbed.addField("VIP", UserProfile.getVIP(message[1]) ? "Yes" : "No", false);
                        } else {
                            msgEmbed = error404(user);
                        }

                        event.getChannel().sendMessage(msgEmbed.build()).queue();
                    }
                    break;
                case "about":
                    String creator = "Wirlaburla";
                    event.getChannel().sendMessage(
                            msgEmbed.setTitle("About WorldsBot")
                                    .setDescription("Integration for Discord and Worlds.")
                                    .setColor(Color.magenta)
                                    .addField("Creator",creator,false)
                                    .addField("Version", Version.getVersion(),false)
                                    .addField("Changelog","<https://wirlaburla.site/WorldsBot/>",false)
                            .build()
                    ).queue();
                    break;
            }
            msgEmbed = null;
        }
    }

    String userDesc(String username) {
        if (UserProfile.employees.contains(username.toLowerCase())) { // If the account requests is an employee
            return ":blue_circle: This user is an Employee.";
        } else if (username.startsWith("host-")) {
            return ":yellow_circle: This user is a Host.";
        } else if (username.startsWith("guest-")) {
            return ":purple_circle: This user is a Special Guest.";
        } else {
            return null;
        }
    }

    // Error 404: User Not Found
    EmbedBuilder error404(String username) {
        EmbedBuilder embedo = new EmbedBuilder()
                .setTitle("Error 404: User not found!")
                .setDescription("No user named '" + username + "' can be found.")
                .setColor(Color.orange);
        return embedo;
    }
}
