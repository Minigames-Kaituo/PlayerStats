package com.artemis.the.gr8.playerstats.core.msg.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public final class ExcludeInfoMessage implements TextComponent {

    private final TextComponent excludeInfo;

    private ExcludeInfoMessage(ComponentFactory factory) {
        excludeInfo = buildMessage(factory);
    }

    @Contract("_ -> new")
    public static @NotNull ExcludeInfoMessage construct(ComponentFactory factory) {
        return new ExcludeInfoMessage(factory);
    }

    private @NotNull TextComponent buildMessage(@NotNull ComponentFactory factory) {
        return Component.newline()
                .append(factory.pluginPrefixAsTitle())
                .append(Component.newline())
                .append(factory.subTitle("将鼠标悬停在参数上以获取更多信息！"))
                .append(Component.newline())
                .append(text("Usage: ").color(factory.INFO_MSG)
                        .append(text("/statexclude").color(factory.INFO_MSG_ACCENT_MEDIUM)))
                .append(Component.newline())
                .append(factory.bulletPoint()).append(Component.space())
                .append(text("add ").color(factory.INFO_MSG_ACCENT_DARKEST)
                        .append(text("{player-name}").color(factory.INFO_MSG_ACCENT_MEDIUM))
                        .hoverEvent(HoverEvent.showText(
                                text("将这位玩家从 /stat 结果中排除").color(factory.INFO_MSG_ACCENT_LIGHTEST))))
                .append(Component.newline())
                .append(factory.bulletPoint()).append(Component.space())
                .append(text("remove ").color(factory.INFO_MSG_ACCENT_DARKEST)
                        .append(text("{player-name}").color(factory.INFO_MSG_ACCENT_MEDIUM))
                        .hoverEvent(HoverEvent.showText(
                                text("将这位玩家再次包含在 /stat 的结果中").color(factory.INFO_MSG_ACCENT_LIGHTEST))))
                .append(Component.newline())
                .append(factory.bulletPoint()).append(Component.space())
                .append(text("list").color(factory.INFO_MSG_ACCENT_DARKEST)
                        .hoverEvent(HoverEvent.showText(
                                text("查看当前所有被排除的玩家列表").color(factory.INFO_MSG_ACCENT_LIGHTEST))))
                .append(Component.newline())
                .append(Component.newline())
                .append(text("被排除的玩家有：")
                        .color(factory.INFO_MSG))
                .append(Component.newline())
                .append(factory.arrow()).append(Component.space())
                .append(text("不在前十名之内").color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(Component.newline())
                .append(factory.arrow()).append(Component.space())
                .append(text("不计入服务器总数").color(factory.INFO_MSG_ACCENT_MEDIUM))
                .append(Component.newline())
                .append(factory.arrow()).append(Component.space())
                .append(text("hidden").color(factory.INFO_MSG_ACCENT_LIGHTEST)
                        .hoverEvent(HoverEvent.showText(text("所有统计数据仍然由服务器存储和跟踪，")
                                .append(Component.newline())
                                .append(text("此命令不会删除任何内容！"))
                                .color(factory.INFO_MSG_ACCENT_LIGHTEST))))
                .append(text(" - 没有被删除")
                        .color(factory.INFO_MSG_ACCENT_MEDIUM));
    }

    @Override
    public @NotNull String content() {
        return excludeInfo.content();
    }

    @Override
    public @NotNull TextComponent content(@NotNull String content) {
        return excludeInfo.content(content);
    }

    @Override
    public @NotNull Builder toBuilder() {
        return excludeInfo.toBuilder();
    }

    @Override
    public @Unmodifiable @NotNull List<Component> children() {
        return excludeInfo.children();
    }

    @Override
    public @NotNull TextComponent children(@NotNull List<? extends ComponentLike> children) {
        return excludeInfo.children(children);
    }

    @Override
    public @NotNull Style style() {
        return excludeInfo.style();
    }

    @Override
    public @NotNull TextComponent style(@NotNull Style style) {
        return excludeInfo.style(style);
    }
}