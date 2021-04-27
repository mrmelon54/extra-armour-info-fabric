package net.onpointcoding.extraarmourinfo.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import net.onpointcoding.extraarmourinfo.enums.PositionDisplayOption;
import net.onpointcoding.extraarmourinfo.enums.SideDisplayOption;

public interface SubCategoryConfig extends ConfigData {
    boolean isEnabled();

    PositionDisplayOption getPosition();

    SideDisplayOption getSide();

    int getTweakX();

    int getTweakY();
}
