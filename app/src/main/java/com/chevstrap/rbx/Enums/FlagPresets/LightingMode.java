package com.chevstrap.rbx.Enums.FlagPresets;

public enum LightingMode {
    Default("Default"),
    Voxel("Voxel"),
    ShadowMap("Shadow Map"),
    Future("Future"),
    Unified("Unified");

    private final String displayName;

    LightingMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
