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
package dev.dejvokep.clickspersecond.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
import dev.dejvokep.clickspersecond.ClicksPerSecond;
import dev.dejvokep.clickspersecond.handler.sampler.Sampler;
import dev.dejvokep.clickspersecond.utils.player.UUIDFactory;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static dev.dejvokep.clickspersecond.utils.messaging.Messenger.*;

/**
 * Handler for the <code>/cps delete</code> command.
 */
public class DeleteCommand {

    // Plugin
    private final ClicksPerSecond plugin;

    /**
     * Registers the command to the given manager.
     *
     * @param plugin  the plugin
     * @param manager the manager
     */
    public DeleteCommand(@NotNull ClicksPerSecond plugin, @NotNull CommandManager<CommandSender> manager) {
        this.plugin = plugin;

        // Register
        manager.command(manager.commandBuilder("cps", "clickspersecond").literal("delete").permission("cps.delete")
                .argument(StringArgument.single("name|uuid|all"))
                .meta(CommandMeta.DESCRIPTION, "Deletes all or individual player data (identified by name or UUID) from the currently used data source.")
                .meta(CommandConfirmationManager.META_CONFIRMATION_REQUIRED, true).handler(context -> {
                    // The target
                    String target = context.get("name|uuid|all");

                    // Delete all
                    if (target.equals("*") || target.equals("all")) {
                        plugin.getMessenger().send(context, MESSAGE_REQUEST_SENT);
                        plugin.getClickHandler().getSamplers().values().forEach(Sampler::wipeData);
                        return;
                    }

                    // Parse UUID
                    UUID uuid = UUIDFactory.fromArgument(target);
                    if (uuid == null) {
                        plugin.getMessenger().send(context, MESSAGE_INVALID_NAME, message -> message.replace("{name}", target));
                        return;
                    }

                    // Delete
                    Sampler sampler = plugin.getClickHandler().getSampler(uuid);
                    if (sampler != null)
                        sampler.wipeData();
                }).build());
    }

}