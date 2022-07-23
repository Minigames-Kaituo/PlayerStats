package com.gmail.artemis.the.gr8.playerstats.msg;

import com.gmail.artemis.the.gr8.playerstats.ShareManager;
import com.gmail.artemis.the.gr8.playerstats.api.StatFormatter;
import com.gmail.artemis.the.gr8.playerstats.config.ConfigHandler;
import com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage;
import com.gmail.artemis.the.gr8.playerstats.models.StatRequest;
import com.gmail.artemis.the.gr8.playerstats.msg.components.BukkitConsoleComponentFactory;
import com.gmail.artemis.the.gr8.playerstats.msg.components.PrideComponentFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.Month;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.jetbrains.annotations.ApiStatus.Internal;
import static com.gmail.artemis.the.gr8.playerstats.enums.StandardMessage.*;

/** This class manages all PlayerStats output. It is the only place where messages are sent.
 It gets the messages from a {@link MessageBuilder}, which is different for a Console as for Players
 (mainly to deal with the lack of hover-text, and for Bukkit consoles to make up for the lack of hex-colors).*/
public final class OutputManager implements StatFormatter {

    private static BukkitAudiences adventure;
    private static ShareManager shareManager;
    private static MessageBuilder writer;
    private static MessageBuilder consoleWriter;

    private static EnumMap<StandardMessage, Function<MessageBuilder, TextComponent>> standardMessages;

    public OutputManager(BukkitAudiences adventure, ConfigHandler config, ShareManager shareManager) {
        OutputManager.adventure = adventure;
        OutputManager.shareManager = shareManager;

        getMessageWriters(config);
        prepareFunctions();
    }

    public void updateMessageWriters(ConfigHandler config) {
        getMessageWriters(config);
    }

    @Internal
    @Override
    public boolean saveOutputForSharing() {
        return true;
    }

    @Override
    public TextComponent formatPlayerStat(@NotNull StatRequest request, int playerStat) {
        CommandSender sender = request.getCommandSender();
        BiFunction<UUID, CommandSender, TextComponent> playerStatFunction =
                getWriter(sender).formattedPlayerStatFunction(playerStat, request);

        return processBuildFunction(sender, playerStatFunction);
    }

    @Override
    public TextComponent formatServerStat(@NotNull StatRequest request, long serverStat) {
        CommandSender sender = request.getCommandSender();
        BiFunction<UUID, CommandSender, TextComponent> serverStatFunction =
                getWriter(sender).formattedServerStatFunction(serverStat, request);

        return processBuildFunction(sender, serverStatFunction);
    }

    @Override
    public TextComponent formatTopStat(@NotNull StatRequest request, LinkedHashMap<String, Integer> topStats) {
        CommandSender sender = request.getCommandSender();
        BiFunction<UUID, CommandSender, TextComponent> topStatFunction =
                getWriter(sender).formattedTopStatFunction(topStats, request);

        return processBuildFunction(sender, topStatFunction);
    }

    public void sendFeedbackMsg(CommandSender sender, StandardMessage message) {
        if (message != null) {
            adventure.sender(sender).sendMessage(standardMessages.get(message)
                    .apply(getWriter(sender)));
        }
    }

    public void sendFeedbackMsgWaitAMoment(CommandSender sender, boolean longWait) {
        adventure.sender(sender).sendMessage(getWriter(sender)
                .waitAMoment(longWait));
    }

    public void sendFeedbackMsgMissingSubStat(CommandSender sender, Statistic.Type statType) {
        adventure.sender(sender).sendMessage(getWriter(sender)
                .missingSubStatName(statType));
    }

    public void sendFeedbackMsgWrongSubStat(CommandSender sender, Statistic.Type statType, String subStatName) {
        if (subStatName == null) {
            sendFeedbackMsgMissingSubStat(sender, statType);
        } else {
            adventure.sender(sender).sendMessage(getWriter(sender)
                    .wrongSubStatType(statType, subStatName));
        }
    }

    public void sendExamples(CommandSender sender) {
        adventure.sender(sender).sendMessage(getWriter(sender)
                .usageExamples());
    }

    public void sendHelp(CommandSender sender) {
        adventure.sender(sender).sendMessage(getWriter(sender)
                .helpMsg(sender instanceof ConsoleCommandSender));
    }

    public void sendToAllPlayers(@NotNull TextComponent component) {
        adventure.players().sendMessage(component);
    }

    public void sendToCommandSender(CommandSender sender, TextComponent component) {
        adventure.sender(sender).sendMessage(component);
    }

    private TextComponent processBuildFunction(@Nullable CommandSender sender, BiFunction<UUID, CommandSender, TextComponent> buildFunction) {
        boolean saveOutput = saveOutputForSharing() &&
                sender != null &&
                ShareManager.isEnabled() &&
                shareManager.senderHasPermission(sender);

        if (saveOutput) {
            UUID shareCode =
                    shareManager.saveStatResult(sender.getName(), buildFunction.apply(null, sender));
            return buildFunction.apply(shareCode, null);
        }
        else {
            return buildFunction.apply(null, null);
        }
    }

    private MessageBuilder getWriter(CommandSender sender) {
        return sender instanceof ConsoleCommandSender ? consoleWriter : writer;
    }

    private void getMessageWriters(ConfigHandler config) {
        boolean isBukkit = Bukkit.getName().equalsIgnoreCase("CraftBukkit");
        if (config.useRainbowMode() ||
                (config.useFestiveFormatting() && LocalDate.now().getMonth().equals(Month.JUNE))) {
            writer = MessageBuilder.fromComponentFactory(config, new PrideComponentFactory(config));
        }
        else {
            writer = MessageBuilder.defaultBuilder(config);
        }

        if (!isBukkit) {
            consoleWriter = writer;
        } else {
            consoleWriter = MessageBuilder.fromComponentFactory(config, new BukkitConsoleComponentFactory(config));
        }
    }

    private void prepareFunctions() {
        standardMessages = new EnumMap<>(StandardMessage.class);

        standardMessages.put(RELOADED_CONFIG, (MessageBuilder::reloadedConfig));
        standardMessages.put(STILL_RELOADING, (MessageBuilder::stillReloading));
        standardMessages.put(MISSING_STAT_NAME, (MessageBuilder::missingStatName));
        standardMessages.put(MISSING_PLAYER_NAME, (MessageBuilder::missingPlayerName));
        standardMessages.put(REQUEST_ALREADY_RUNNING, (MessageBuilder::requestAlreadyRunning));
        standardMessages.put(STILL_ON_SHARE_COOLDOWN, (MessageBuilder::stillOnShareCoolDown));
        standardMessages.put(RESULTS_ALREADY_SHARED, (MessageBuilder::resultsAlreadyShared));
        standardMessages.put(STAT_RESULTS_TOO_OLD, (MessageBuilder::statResultsTooOld));
        standardMessages.put(UNKNOWN_ERROR, (MessageBuilder::unknownError));
    }
}