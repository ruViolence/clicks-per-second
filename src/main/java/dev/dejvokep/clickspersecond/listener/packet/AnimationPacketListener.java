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
package dev.dejvokep.clickspersecond.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import dev.dejvokep.clickspersecond.ClicksPerSecond;

import java.util.Collections;

public class AnimationPacketListener extends PacketAdapter {
    private final ClicksPerSecond plugin;

    public AnimationPacketListener(ClicksPerSecond plugin) {
        super(plugin, ListenerPriority.NORMAL, Collections.singleton(PacketType.Play.Client.ARM_ANIMATION), ListenerOptions.ASYNC);
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        plugin.getClickHandler().processClick(event.getPlayer().getUniqueId());
    }
}
