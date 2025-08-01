package com.chevstrap.rbx.Enums.FlagPresets;

public enum TextureMode {
    Automatic("Automatic"),
    x1("1x"),
    x2("2x"),
    x3("3x");

    private final String displayName;

    TextureMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
