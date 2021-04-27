package net.onpointcoding.extraarmourinfo.enums;

public enum PositionDisplayOption {
    HUD("HUD"),
    TOP("Top"),
    MIDDLE("Middle"),
    BOTTOM("Bottom");

    private final String name;

    PositionDisplayOption(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}