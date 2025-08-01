package com.chevstrap.rbx.Enums.FlagPresets;

public enum MSAAMode {
    Automatic("Automatic"),
    x1("1x"),
    x2("2x"),
    x4("4x");

    private final String displayName;

    MSAAMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
