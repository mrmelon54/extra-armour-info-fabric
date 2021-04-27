package net.onpointcoding.extraarmourinfo.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
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

