package com.gmail.artemis.the.gr8.playerstats;

import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StatManager {

    private final Main plugin;
    private final EnumHandler enumHandler;
    private final OfflinePlayerHandler offlinePlayerHandler;
    private final List<String> statNames;
    private final List<String> entityStatNames;
    private final List<String> subStatEntryNames;

    public StatManager(EnumHandler e, Main p) {
        plugin = p;
        enumHandler = e;
        offlinePlayerHandler = OfflinePlayerHandler.getInstance();

        statNames = Arrays.stream(Statistic.values()).map(
                Statistic::toString).map(String::toLowerCase).toList();
        entityStatNames = Arrays.stream(Statistic.values()).filter(statistic ->
                statistic.getType().equals(Statistic.Type.ENTITY)).map(
                Statistic::toString).map(String::toLowerCase).collect(Collectors.toList());

        subStatEntryNames = new ArrayList<>();
        subStatEntryNames.addAll(enumHandler.getBlockNames());
        subStatEntryNames.addAll(enumHandler.getEntityTypeNames());
        subStatEntryNames.addAll(enumHandler.getItemNames());
    }

    public int getStatistic(String statName, String playerName) throws IllegalArgumentException, NullPointerException {
        return getStatistic(statName, null, playerName);
    }

    //returns the integer associated with a certain statistic for a player
    public int getStatistic(String statName, String subStatEntryName, String playerName) throws IllegalArgumentException, NullPointerException {
        long time = System.currentTimeMillis();

        OfflinePlayer player = offlinePlayerHandler.getOfflinePlayer(playerName);

        plugin.getLogger().info("StatManager 51: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        if (player == null) throw new NullPointerException("No player called " + playerName + " was found!");

        Statistic stat = getStatistic(statName);
        plugin.getLogger().info("StatManager 56: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();

            if (stat != null) {
                switch (stat.getType()) {
                    case UNTYPED -> {
                        plugin.getLogger().info("StatManager 62: " + (System.currentTimeMillis() - time));
                        time = System.currentTimeMillis();
                        return player.getStatistic(stat);
                    }
                    case BLOCK -> {
                        plugin.getLogger().info("StatManager 67: " + (System.currentTimeMillis() - time));
                        time = System.currentTimeMillis();
                        Material block = enumHandler.getBlock(subStatEntryName);
                        if (block == null) throw new NullPointerException(subStatEntryName + " is not a valid block name!");
                        return player.getStatistic(stat, block);
                    }
                    case ENTITY -> {
                        plugin.getLogger().info("StatManager 74: " + (System.currentTimeMillis() - time));
                        time = System.currentTimeMillis();
                        EntityType entity = enumHandler.getEntityType(subStatEntryName);
                        if (entity == null) throw new NullPointerException(subStatEntryName + " is not a valid entity name!");
                        return player.getStatistic(stat, entity);
                    }
                    case ITEM -> {
                        plugin.getLogger().info("StatManager 81: " + (System.currentTimeMillis() - time));
                        time = System.currentTimeMillis();
                        Material item = enumHandler.getItem(subStatEntryName);
                        if (item == null) throw new NullPointerException(subStatEntryName + " is not a valid item name!");
                        return player.getStatistic(stat, item);
                    }
                }
            }
        throw new NullPointerException(statName + " is not a valid statistic name!");
    }

    //returns the statistic enum constant, or null if non-existent (param: statName, not case sensitive)
    private Statistic getStatistic(String statName) {
        try {
            return Statistic.valueOf(statName.toUpperCase());
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            plugin.logStatRelatedExceptions(exception);
            return null;
        }
    }

    //gets the type of the statistic from the string, otherwise returns null (param: statName, not case sensitive)
    public Statistic.Type getStatType(String statName) {
        try {
            return Statistic.valueOf(statName.toUpperCase()).getType();
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            plugin.logStatRelatedExceptions(exception);
            return null;
        }
    }

    //checks if string is a valid statistic (param: statName, not case sensitive)
    public boolean isStatistic(String statName) {
        return statNames.contains(statName.toLowerCase());
    }

    //checks if string is a valid substatistic dealing with entities (param: statName, not case sensitive)
    public boolean isStatEntityType(String statName) {
        return entityStatNames.contains(statName.toLowerCase());
    }

    //checks in the most general sense if this statistic is a substatistic (param: statName, not case sensitive)
    public boolean isSubStatEntry(String statName) {
        return subStatEntryNames.contains(statName.toLowerCase());
    }

    //checks whether a subStatEntry is of the type that the statistic requires
    public boolean isMatchingSubStatEntry(String statName, String subStatEntry) {
        Statistic.Type type = getStatType(statName);
        if (type != null && subStatEntry != null) {
            switch (type) {
                case ENTITY -> {
                    return enumHandler.isEntityType(subStatEntry);
                }
                case ITEM -> {
                    return enumHandler.isItem(subStatEntry);
                }
                case BLOCK -> {
                    return enumHandler.isBlock(subStatEntry);
                }
                case UNTYPED -> {
                    return false;
                }
            }
        }
        return false;
    }

    //returns the names of all general statistics in lowercase
    public List<String> getStatNames() {
        return statNames;
    }

    //returns all statistics that have type entities, in lowercase
    public List<String> getEntityTypeNames() {
        return entityStatNames;
    }

    //returns all substatnames in lowercase
    public List<String> getSubStatEntryNames() {
        return subStatEntryNames;
    }
}
