package net.onpointcoding.extraarmourinfo.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.onpointcoding.extraarmourinfo.config.statusbars.KnockbackConfig;
import net.onpointcoding.extraarmourinfo.config.statusbars.ToughnessConfig;

@SuppressWarnings("unused")
@Config(name = "extraarmourinfo")
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
public
class ConfigStructure implements ConfigData {
    @ConfigEntry.Category("knockback")
    @ConfigEntry.Gui.TransitiveObject
    public KnockbackConfig knockback = new KnockbackConfig();

    @ConfigEntry.Category("toughness")
    @ConfigEntry.Gui.TransitiveObject
    public ToughnessConfig toughness = new ToughnessConfig();
}

