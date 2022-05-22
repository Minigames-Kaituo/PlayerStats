package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.commands.ReloadCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.StatCommand;
import com.gmail.artemis.the.gr8.playerstats.commands.TabCompleter;
import com.gmail.artemis.the.gr8.playerstats.filehandlers.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.listeners.JoinListener;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OutputFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();

        //check if Spigot ChatColors can be used, and prepare accordingly
        boolean enableHexColors = false;
        try {
            Class.forName("net.md_5.bungee.api.ChatColor");
            enableHexColors = true;
            this.getLogger().info("Hex Color support enabled!");
        }
        catch (ClassNotFoundException e) {
            this.getLogger().info("Hex Colors are not supported for this server type, proceeding with default Chat Colors...");
        }

        //get instances of the classes that should be initialized
        ConfigHandler config = new ConfigHandler(this);
        EnumHandler enumHandler = new EnumHandler();
        OutputFormatter outputFormatter = new OutputFormatter(config, enableHexColors);

        OfflinePlayerHandler offlinePlayerHandler = new OfflinePlayerHandler(config);
        getLogger().info("Amount of offline players: " + offlinePlayerHandler.getOfflinePlayerCount());

        //register the commands
        PluginCommand statcmd = this.getCommand("statistic");
        if (statcmd != null) {
            statcmd.setExecutor(new StatCommand(config, enumHandler, offlinePlayerHandler, outputFormatter, this));
            statcmd.setTabCompleter(new TabCompleter(enumHandler));
        }
        PluginCommand reloadcmd = this.getCommand("statisticreload");
        if (reloadcmd != null) reloadcmd.setExecutor(new ReloadCommand(config, offlinePlayerHandler, outputFormatter));

        //register the listener
        Bukkit.getPluginManager().registerEvents(new JoinListener(offlinePlayerHandler), this);
        logTimeTaken("Time", "taken", time);
        this.getLogger().info("Enabled PlayerStats!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabled PlayerStats!");
    }

    public long logTimeTaken(String className, String methodName, long previousTime) {
        getLogger().info(className + " " + methodName + " " + ": " + (System.currentTimeMillis() - previousTime));
        return System.currentTimeMillis();
    }
}
