package dev.shadowsoffire.apotheosis.garden.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class GardenREIPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Apotheosis.enableGarden) return;
        EntryStack<ItemStack> enderLead = EntryStacks.of(GardenModule.ENDER_LEAD);
        Component name = Component.translatable("item.zenith.ender_lead");
        Component line = Component.translatable("info.zenith.ender_lead");
        DefaultInformationDisplay info = DefaultInformationDisplay.createFromEntry(enderLead, name);
        info.line(line);
        registry.add(info);
    }

}
