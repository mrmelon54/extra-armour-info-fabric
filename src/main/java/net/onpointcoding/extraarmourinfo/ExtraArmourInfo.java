package net.onpointcoding.extraarmourinfo;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.onpointcoding.extraarmourinfo.config.ConfigStructure;
import net.onpointcoding.extraarmourinfo.config.statusbars.KnockbackConfig;
import net.onpointcoding.extraarmourinfo.config.statusbars.ToughnessConfig;

public class ExtraArmourInfo implements ModInitializer {
    private static ExtraArmourInfo instance;
    private ConfigStructure config;

    public static ExtraArmourInfo getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        instance = this;

        System.out.println("Loading extra armour info");
        AutoConfig.register(ConfigStructure.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ConfigStructure.class).getConfig();
    }

    public ConfigStructure getConfig() {
        return config;
    }

    public KnockbackConfig getKnockbackResistanceConfig() {
        return getConfig().knockback;
    }

    public ToughnessConfig getToughnessConfig() {
        return getConfig().toughness;
    }
}
