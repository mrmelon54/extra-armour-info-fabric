package net.onpointcoding.extraarmourinfo.config.statusbars;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.onpointcoding.extraarmourinfo.config.SubCategoryConfig;
import net.onpointcoding.extraarmourinfo.enums.PositionDisplayOption;
import net.onpointcoding.extraarmourinfo.enums.SideDisplayOption;

public class HotIconConfig implements SubCategoryConfig {
    public boolean enabled = true;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public PositionDisplayOption position = PositionDisplayOption.HUD;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public SideDisplayOption side = SideDisplayOption.LEFT;

    @ConfigEntry.Gui.Tooltip()
    public int tweakX = 0;

    @ConfigEntry.Gui.Tooltip()
    public int tweakY = 0;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public PositionDisplayOption getPosition() {
        return position;
    }

    @Override
    public SideDisplayOption getSide() {
        return side;
    }

    @Override
    public int getTweakX() {
        return tweakX;
    }

    @Override
    public int getTweakY() {
        return tweakY;
    }
}
