/*
 * Copyright 2022 https://dejvokep.dev/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.dejvokep.clickspersecond.utils.messaging;

import cloud.commandframework.context.CommandContext;
import dev.dejvokep.clickspersecond.ClicksPerSecond;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Messenger class used to send command messages.
 */
public class Messenger {

    /**
     * Message path prefix.
     */
    public static final String MESSAGE_PREFIX = "messages.";
    /**
     * "Players only" message path.
     */
    public static final String MESSAGE_PLAYERS_ONLY = MESSAGE_PREFIX + "players-only";
    /**
     * "Invalid name" message path.
     */
    public static final String MESSAGE_INVALID_NAME = MESSAGE_PREFIX + "invalid-name";
    /**
     * "Confirmation required" message path.
     */
    public static final String MESSAGE_CONFIRM_REQUIRED = MESSAGE_PREFIX + "confirm.required";
    /**
     * "Confirmation not pending" message path.
     */
    public static final String MESSAGE_CONFIRM_NO_PENDING = MESSAGE_PREFIX + "confirm.not-pending";

    /**
     * "Data request sent" message path.
     */
    public static final String MESSAGE_REQUEST_SENT = MESSAGE_PREFIX + "data-request.sent";

    private final ClicksPerSecond plugin;

    /**
     * Initializes the messenger.
     *
     * @param plugin the plugin
     */
    public Messenger(@NotNull ClicksPerSecond plugin) {
        this.plugin = plugin;
    }

    /**
     * Sends message to the sender of the given context.
     *
     * @param context   the command context
     * @param messageId ID of the message to send
     */
    public void send(@NotNull CommandContext<CommandSender> context, @NotNull String messageId) {
        send(context, messageId, null);
    }

    /**
     * Sends message to the given sender.
     *
     * @param sender    the sender to send to
     * @param messageId ID of the message to send
     */
    public void send(@NotNull CommandSender sender, @NotNull String messageId) {
        send(sender, messageId, null);
    }

    /**
     * Sends message to the sender of the given context. If provided, applies the given replacer to the message.
     *
     * @param context   the command context
     * @param messageId ID of the message to send
     * @param replacer  replacer to apply to the message
     */
    public void send(@NotNull CommandContext<CommandSender> context, @NotNull String messageId, @Nullable Function<String, String> replacer) {
        send(context.getSender(), messageId, replacer);
    }

    /**
     * Sends message to the given sender. If provided, applies the given replacer to the message.
     *
     * @param sender    the sender to send to
     * @param messageId ID of the message to send
     * @param replacer  replacer to apply to the message
     */
    public void send(@NotNull CommandSender sender, @NotNull String messageId, @Nullable Function<String, String> replacer) {
        // Not online
        if (sender instanceof Player && !((Player) sender).isOnline())
            return;

        // Message
        String message = plugin.getConfiguration().getString(messageId);
        if (message == null || message.isEmpty())
            return;

        // Send
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', replacer == null ? message : replacer.apply(message)));
    }

}