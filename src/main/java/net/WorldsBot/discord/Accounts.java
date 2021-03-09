package net.WorldsBot.discord;

import net.WorldsBot.Message;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * -- Account Configuration Saving/Loading
 * Main Accounts class that handles loading linked accounts and saving newly linked accounts.
 * Configuration files are stored per-user in a folder named "accounts". The filename is the
 * id of the user linked. Worlds accounts are stored by username in the file.
 */

public class Accounts {

    Map<String, List<String>> logins;

    public void register() {
        logins = new HashMap<>();
        File mainFolder = new File("accounts/");
        assert mainFolder.exists() || mainFolder.mkdir();

        System.out.println("Loading Usernames");
        if (mainFolder.isDirectory()) {
            File[] files = mainFolder.listFiles();
            for (File file : files) {
                String fileName = file.getName().replace(".yml", "").replace(".yaml", "");
                YamlFile yaml = new YamlFile(file.getPath());
                try {
                    yaml.load();
                } catch (InvalidConfigurationException | IOException e) {
                    e.printStackTrace();
                }
                logins.put(fileName, yaml.getStringList("worlds"));
                System.out.println(Message.format("Found {0} account(s) linked to ID {1}.", new String[] {String.valueOf(yaml.getStringList("worlds").size()), fileName }));
            }
        }
        if (logins.size() < 1) System.out.println("No accounts found.");
    }

    public void link(String id, String username) {
        YamlFile linked = new YamlFile("accounts/" + id + ".yml");
        List<String> accounts;
        if (!linked.exists()) {
            try {
                linked.createNewFile(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            linked.load();
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }

        /*
        Map<String, List<String>> tempLogins = logins;
        // Recaching Map
        if (tempLogins.containsKey(id)) {
            tempLogins.get(id).add(username);
        } else {
            tempLogins.put(username, Collections.singletonList(username));
        }
        logins = tempLogins;
         */

        accounts = linked.getStringList("worlds");
        accounts.add(username.toLowerCase());
        linked.set("worlds", accounts);
        try {
            linked.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Replacement for now...
        // Not sure if keeping or not...
        register();

    }

    public void unlink(String id, String username) {
        YamlFile linked = new YamlFile("accounts/" + id + ".yml");
        List<String> accounts;
        if (linked.exists()) {
            try {
                linked.load();
            } catch (InvalidConfigurationException | IOException e) {
                e.printStackTrace();
            }

            accounts = linked.getStringList("worlds");
            accounts.remove(username);
            linked.set("worlds", accounts);
            try {
                linked.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            register();
        } else {
            System.out.println("Account has no configuration file. Aborting.");
        }
    }

    public String getId(String username) {
        for (Map.Entry<String, List<String>> entry : logins.entrySet()) {
            if (entry.getValue().size() > 1) for (String login : entry.getValue()) if (login.equalsIgnoreCase(username)) return entry.getKey();
            else if (entry.getValue().get(0).equalsIgnoreCase(username)) return entry.getKey();
        }
        return null;
    }

    public List<String> getUsernames(String id) {
        return logins.get(id);
    }

    public String getUsername(String id, int index) {
        List<String> ins = logins.get(id);

        return ins.get(index);
    }

}
