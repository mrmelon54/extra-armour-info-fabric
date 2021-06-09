package net.onpointcoding.extraarmourinfo.config;

import me.shedaniel.autoconfig.ConfigData;
import net.onpointcoding.extraarmourinfo.enums.PositionDisplayOption;
import net.onpointcoding.extraarmourinfo.enums.SideDisplayOption;

public interface SubCategoryConfig extends ConfigData {
    boolean isEnabled();

    PositionDisplayOption getPosition();

    SideDisplayOption getSide();

    int getTweakX();

    int getTweakY();
}
