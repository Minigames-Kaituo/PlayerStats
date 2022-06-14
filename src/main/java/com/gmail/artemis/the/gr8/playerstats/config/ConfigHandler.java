package com.gmail.artemis.the.gr8.playerstats.config;

import com.gmail.artemis.the.gr8.playerstats.Main;
import com.gmail.artemis.the.gr8.playerstats.enums.Query;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ConfigHandler {

    private File configFile;
    private FileConfiguration config;
    private final Main plugin;
    private final int configVersion;

    public ConfigHandler(Main p) {
        plugin = p;

        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(configFile);
        configVersion = 3;

        checkConfigVersion();
    }

    /** Checks the number that "config-version" returns, to see if the config needs updating.
     <p></p>
     <p>PlayerStats 1.1: "config-version" doesn't exist.</p>
     <p>PlayerStats 1.2: "config-version" is 2.</p>
     <p>PlayerStats 1.3: "config-version" is 3. </P>*/
    private void checkConfigVersion() {
        if (!config.contains("config-version") || config.getInt("config-version") != configVersion) {
            new ConfigUpdateHandler(plugin, configFile, configVersion);
        }
    }

    /** Reloads the config from file, or creates a new file with default values if there is none. */
    public boolean reloadConfig() {
        try {
            if (!configFile.exists()) {
                saveDefaultConfig();
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            return true;
        }
        catch (Exception e) {
            plugin.getLogger().warning(e.toString());
            return false;
        }
    }

    /** Returns the config setting for include-whitelist-only, or the default value "false". */
    public boolean whitelistOnly() {
        return config.getBoolean("include-whitelist-only", false);
    }

    /** Returns the config setting for exclude-banned-players, or the default value "false". */
    public boolean excludeBanned() {
        return config.getBoolean("exclude-banned-players", false);
    }

    /** Returns the number of maximum days since a player has last been online, or the default value of 0 to not use this constraint. */
    public int lastPlayedLimit() {
        return config.getInt("number-of-days-since-last-joined", 0);
    }

    /** Whether to use festive formatting, such as pride colors - true by default */
    public boolean useFestiveFormatting() {
        return config.getBoolean("enable-festive-formatting", true);
    }

    /** Gets a String representation of an integer (with or without "!" in front of it) that can determine rainbow phase in Adventure. */
    public String getRainbowPhase() {
        return config.getString("rainbow-phase", "");
    }

    /** Whether or not to use HoverComponents in the usage explanation, returns true by default*/
    public boolean useHoverText() {
        return config.getBoolean("enable-hover-text", true);
    }

    /** Returns the config setting for use-dots, or the default value "true" if no value can be retrieved. */
    public boolean useDots() {
        return config.getBoolean("use-dots", true);
    }

    /** Returns the config setting for top-list-max-size, or the default value of 10 if no value can be retrieved. */
    public int getTopListMaxSize() {
        return config.getInt("top-list-max-size", 10);
    }

    public String getServerTitle() {
        return config.getString("total-server-stat-title", "Total on");
    }

    /** Returns the specified server name, or "this server" if no value can be retrieved. */
    public String getServerName() {
        return config.getString("your-server-name", "this server");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "green" or "gold" for Color (for top or individual color). */
    public String getPlayerNameFormatting(Query selection, boolean isStyle) {
        String def;
        if (selection == Query.TOP) {
            def = "green";
        }
        else {
            def = "gold";
        }
        return getStringFromConfig(selection, isStyle, def, "player-names");
    }

    /** Returns true if playerNames style is bold, false if it is not (and false by default). */
    public boolean playerNameIsBold() {
        ConfigurationSection style = getRelevantSection(Query.PLAYER);

        if (style != null) {
            String styleString = style.getString("player-names");
            return styleString != null && styleString.equalsIgnoreCase("bold");
        }
        return false;
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "yellow" for Color. */
    public String getStatNameFormatting(Query selection, boolean isStyle) {
        return getStringFromConfig(selection, isStyle, "yellow", "stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "#FFD52B" for Color. */
    public String getSubStatNameFormatting(Query selection, boolean isStyle) {
        return getStringFromConfig(selection, isStyle, "#FFD52B", "sub-stat-names");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "#55AAFF" or "#ADE7FF" for Color (for the top or individual/server color). */
    public String getStatNumberFormatting(Query selection, boolean isStyle) {
        String def;
        if (selection == Query.TOP) {
            def = "#55AAFF";
        }
        else {
            def = "#ADE7FF";
        }
        return getStringFromConfig(selection, isStyle, def,"stat-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "yellow" or "gold" for Color (for top/server). */
    public String getTitleFormatting(Query selection, boolean isStyle) {
        String def;
        if (selection == Query.TOP) {
            def = "yellow";
        }
        else {
            def = "gold";
        }
        return getStringFromConfig(selection, isStyle, def, "title");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "gold" for Color. */
    public String getTitleNumberFormatting(boolean isStyle) {
        return getStringFromConfig(Query.TOP, isStyle, "gold", "title-number");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "#FFB80E" for Color. */
    public String getServerNameFormatting(boolean isStyle) {
        return getStringFromConfig(Query.SERVER, isStyle, "#FFB80E", "server-name");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "gold" for Color. */
    public String getRankNumberFormatting(boolean isStyle) {
        return getStringFromConfig(Query.TOP, isStyle, "gold", "rank-numbers");
    }

    /** Returns a String that represents either a Chat Color, hex color code, or Style. Default values are "none" for Style,
     and "dark_gray" for Color. */
    public String getDotsFormatting(boolean isStyle) {
        return getStringFromConfig(Query.TOP, isStyle, "dark_gray", "dots");
    }

    /** Returns the config value for a color or style option in string-format, the supplied default value, or null if no configSection was found. */
    private @Nullable String getStringFromConfig(Query selection, boolean isStyle, String def, String pathName){
        String path = isStyle ? pathName + "-style" : pathName;
        String defaultValue = isStyle ? "none" : def;

        ConfigurationSection section = getRelevantSection(selection);
        return section != null ? section.getString(path, defaultValue) : null;
    }

    /** Returns the config section that contains the relevant color or style option. */
    private @Nullable ConfigurationSection getRelevantSection(Query selection) {
        switch (selection) {
            case TOP -> {
                return config.getConfigurationSection("top-list");
            }
            case PLAYER -> {
                return config.getConfigurationSection("individual-statistics");
            }
            case SERVER -> {
                return config.getConfigurationSection("total-server");
            }
            default -> {
                return null;
            }
        }
    }

    /** Create a config file if none exists yet (from the config.yml in the plugin's resources). */
    private void saveDefaultConfig() {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
    }
}