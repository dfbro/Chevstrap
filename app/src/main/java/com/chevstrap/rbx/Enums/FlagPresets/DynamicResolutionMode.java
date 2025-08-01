package com.chevstrap.rbx.Enums.FlagPresets;

public enum DynamicResolutionMode {
    Default("Default"),
    A240P("240p"),
    A360P("360p"),
    A480P("480p"),
    A720P("720p"),
    A1080P("1080p"),
    A1440P("1440p"),
    A2160P("2160p"); // 4K

    private final String displayName;

    DynamicResolutionMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
