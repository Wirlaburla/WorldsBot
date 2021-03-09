package net.WorldsBot.worlds;

import net.WorldsBot.GetConfig;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * -- User Profile Management
 * Class that handles information handling and organization. Is called on
 * any bot command that requests Worlds information.
 */

public class UserProfile {

    private static final String baseURL = GetConfig.main().getString("Pages.Base");
    private static final String infoURL = GetConfig.main().getString("Pages.Profile");
    private static final String vipURL = GetConfig.main().getString("Pages.VIP");

    // Stolen from the tables.dat
    public static final List<String> employees = Arrays.asList(
            "trinnity", "fkane", "fkane2", "tessaruni", "matt", "capybara", "thom", "sirgemini",
            "dingoatemybaby", "anon", "jeremy", "mouse", "thumper", "bluebettle", "dotcommie", "treehugger",
            "gwydion", "lwts", "tmanwarren", "do", "ayres", "sjci", "schock", "shane123", "technozeus"
    );

    // Used to return actual data for message
    public static List<Info> getInfo(String username) {
        System.out.println("Getting info of Worlds user " + username + ".");
        List<Info> profileList = new ArrayList<>();
        // Define variables before the try/catch statement so I can use them afterwards.
        // Shouldn't cause a NullPointerException because it they aren't assigned in the try/catch, the catch should return null and that is handled elsewhere.
        URL url;
        String[] pText;
        try {
            url = new URL(baseURL + infoURL + username); // Should output correct URL (ex: http://www-dynamic.us.worlds.net/cgi-bin/profile?Thom)
            pText = new Scanner(url.openStream()).useDelimiter("\\A").next().split("\n"); // Splitting into multiple lines. It's just text anyway.
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Checking to see if there is only one line. No profile has one line unless it doesn't exist.
        // Could also just check and see if line one matches a certain set of words.
        if (pText.length <= 1) {
            return null;
        } else {
            // Looping through the lines.
            for (int a = 0; a < pText.length; a++) {
                // Splitting group lines with :. This is done because the number is also displayed in there and we require that for grabbing.
                // The split group name is defined in `complete`, and the number in `count`. Content is defined for convenience sake with the if statement.
                String[] complete = pText[a].split(":");
                int count = Integer.parseInt(complete[1]);
                String content = "";
                // Checking to see if the count is any larger than 1. This is done as to parse multi-line content. Newer profiles don't do this.
                // If it isn't larger, it just simply grabs the next line from the group and advances the count (a).
                if (count > 1) {
                    // Using StringBuilder because less warnings is good, right?
                    StringBuilder contB = new StringBuilder();
                    for (int b = 0; b < count; b++) {
                        contB.append(pText[(a+1) + b]).append("\n");
                    }
                    content = contB.toString();
                    a += count; // Advancing `a` so it skips over the lines we just parsed for the group.
                } else if (count == 1) {
                    // Only grabbing one line this time, so we can skip the for loop entirely.
                    content = pText[a+1];
                    a++;
                }
                // Add all of the combined and parsed data to the object for returning.
                profileList.add(new Info(complete[0], content));
            }
        }
        return profileList;
    }

    public static boolean getVIP(String username) {
        // Defines these so we can use them as a return.
        URL url;
        String cont;
        try {
            // Grabbing URL
            url = new URL(baseURL + vipURL + username);
            // Scanning the contents of the URL so we have the full page.
            Scanner scanner = new Scanner(url.openConnection().getInputStream());
            cont = scanner.useDelimiter("\\Z").next(); // String-ified
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // And this simple text is all that is needed to determine if the user has VIP or not.
        // TODO: Make this not obtain the entire file. It takes it's time grabbing every user apart of a profile when using Discord.
        return cont.contains("You're already a VIP!");
    }

    public static boolean exists(String username) {
        URL url;
        String text;
        try {
            // Typical grabbing URL and then making it a String or Object.
            url = new URL(baseURL + infoURL + username);
            text = new Scanner(url.openStream()).useDelimiter("\\A").next();
        } catch (IOException e) {
            return false;
        }

        return !text.startsWith("No user named");
    }
}
