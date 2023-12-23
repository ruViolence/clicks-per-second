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
package dev.dejvokep.clickspersecond.utils.placeholders;

import dev.dejvokep.clickspersecond.ClicksPerSecond;
import dev.dejvokep.clickspersecond.utils.player.PlayerInfo;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * Expansion for PlaceholderAPI.
 */
public class StatsExpansion extends PlaceholderExpansion {

    private final ClicksPerSecond plugin;
    private final PlaceholderReplacer replacer;

    /**
     * Initializes (but does not register) the statistics expansion.
     *
     * @param plugin the plugin
     */
    public StatsExpansion(@NotNull ClicksPerSecond plugin) {
        this.plugin = plugin;
        this.replacer = plugin.getPlaceholderReplacer();
    }

    @Override
    public @Nullable
    String onRequest(OfflinePlayer player, @NotNull String params) {
        // To lower case
        params = params.toLowerCase();

        // Requesting current CPS
        if (params.equals("now") && player != null)
            return player instanceof Player ? convertToUnknown(plugin.getClickHandler().getCPS((Player) player), -1) : replacer.getUnknownValue();

        // Requesting best CPS
        if (params.startsWith("best") && player != null) {
            PlayerInfo info = plugin.getClickHandler().getInfo(player.getUniqueId());
            if (info == null || info.isLoading())
                return replacer.getUnknownValue();

            if (params.equals("best_cps"))
                return String.valueOf(info.getCPS());
            if (info.isEmpty())
                return replacer.getUnknownValue();

            if (params.equals("best_date_millis"))
                return String.valueOf(info.getTime());
            if (params.equals("best_date") || params.equals("best_date_formatted"))
                return replacer.getDateFormat().format(new Date(info.getTime()));
            return replacer.getUnknownValue();
        }

        // Unknown request
        return replacer.getUnknownValue();
    }

    /**
     * Converts the given value to {@link PlaceholderReplacer#getUnknownValue() unknown}, if it equals the condition or
     * is <code>null</code>.
     *
     * @param value     value to convert, if needed
     * @param condition condition
     * @param <T>       type of the value and condition
     * @return the string representation of the value, or {@link PlaceholderReplacer#getUnknownValue() unknown}
     */
    private <T> String convertToUnknown(@Nullable T value, @NotNull T condition) {
        return value == null || value.equals(condition) ? replacer.getUnknownValue() : value.toString();
    }

    @Override
    public @NotNull
    String getIdentifier() {
        return "cps";
    }

    @Override
    public @NotNull
    String getAuthor() {
        return "dejvokep";
    }

    @Override
    public @NotNull
    String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }
}