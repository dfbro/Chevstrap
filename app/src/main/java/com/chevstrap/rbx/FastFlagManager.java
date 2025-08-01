package com.chevstrap.rbx;

import android.content.Context;
import android.util.Log;

import com.chevstrap.rbx.Enums.FlagPresets.*;

import java.io.*;
import java.util.*;

public class FastFlagManager extends JsonManager<Map<String, Object>> {
    private static FastFlagManager instance;
    private final Context appContext;

    private FastFlagManager(Context context) {
        this.appContext = context.getApplicationContext();
        this.prop = new LinkedHashMap<>();
        this.originalProp = new LinkedHashMap<>();
    }

    public static synchronized FastFlagManager getInstance(Context context) {
        if (instance == null) {
            instance = new FastFlagManager(context);
        }
        return instance;
    }

    public Context getContext() {
        return appContext;
    }

    public Map<String, Object> getProp() {
        return prop;
    }

    public Map<String, Object> getOriginalProp() {
        return originalProp;
    }

    // Preset flags
    public static final Map<String, String> PresetFlags;
    static {
        Map<String, String> flags = new LinkedHashMap<>();
        flags.put("Network.Log", "FLogNetwork");

        flags.put("Rendering.Framerate", "DFIntTaskSchedulerTargetFps");
        flags.put("Rendering.ManualFullscreen", "FFlagHandleAltEnterFullscreenManually");
        flags.put("Rendering.MSAA", "FIntDebugForceMSAASamples");
        flags.put("Rendering.DisablePostFX", "FFlagDisablePostFx");
        flags.put("Rendering.ShadowIntensity", "FIntRenderShadowIntensity");

        flags.put("Rendering.Mode.Vulkan", "FFlagDebugGraphicsPreferVulkan");
        flags.put("Rendering.Mode.OpenGL", "FFlagDebugGraphicsPreferOpenGL");

        flags.put("Rendering.Lighting.Voxel", "DFFlagDebugRenderForceTechnologyVoxel");
        flags.put("Rendering.Lighting.ShadowMap", "FFlagDebugForceFutureIsBrightPhase2");
        flags.put("Rendering.Lighting.Future", "FFlagDebugForceFutureIsBrightPhase3");
        flags.put("Rendering.Lighting.Unified", "FFlagRenderUnifiedLighting13");
        flags.put("Rendering.DynamicResolution", "DFIntDebugDynamicRenderKiloPixels");

        flags.put("Rendering.TextureQuality.OverrideEnabled", "DFFlagTextureQualityOverrideEnabled");
        flags.put("Rendering.TextureQuality.Level", "DFIntTextureQualityOverride");
        flags.put("Rendering.TerrainTextureQuality", "FIntTerrainArraySliceSize");

        flags.put("UI.Hide", "DFIntCanHideGuiGroupId");
        flags.put("UI.FontSize", "FIntFontSizePadding");
        flags.put("UI.FullscreenTitlebarDelay", "FIntFullscreenTitleBarTriggerDelayMillis");
        flags.put("UI.EnableFramerateCap", "FFlagGameBasicSettingsFramerateCap5");
        flags.put("UI.NoGuiBlur", "FIntRobloxGuiBlurIntensity");
        flags.put("UI.DisplayFPS", "FFlagDebugDisplayFPS");

        PresetFlags = Collections.unmodifiableMap(flags);
    }

    public static final Map<LightingMode, String> LightingModes;
    static {
        Map<LightingMode, String> map = new EnumMap<>(LightingMode.class);
        map.put(LightingMode.Default, "None");
        map.put(LightingMode.Voxel, "Voxel");
        map.put(LightingMode.ShadowMap, "ShadowMap");
        map.put(LightingMode.Future, "Future");
        map.put(LightingMode.Unified, "Unified");
        LightingModes = Collections.unmodifiableMap(map);
    }

    public static final Map<MSAAMode, String> MSAAModes;
    static {
        Map<MSAAMode, String> map = new EnumMap<>(MSAAMode.class);
        map.put(MSAAMode.Automatic, null);
        map.put(MSAAMode.x1, "1");
        map.put(MSAAMode.x2, "2");
        map.put(MSAAMode.x4, "4");
        MSAAModes = Collections.unmodifiableMap(map);
    }

    public static final Map<TextureMode, String> TextureModes;
    static {
        Map<TextureMode, String> map = new EnumMap<>(TextureMode.class);
        map.put(TextureMode.Automatic, null);
        map.put(TextureMode.x1, "1");
        map.put(TextureMode.x2, "2");
        map.put(TextureMode.x3, "3");
        TextureModes = Collections.unmodifiableMap(map);
    }

    public static final Map<RenderingMode, String> RenderingModes;
    static {
        Map<RenderingMode, String> map = new EnumMap<>(RenderingMode.class);
        map.put(RenderingMode.Automatic, "None");
        map.put(RenderingMode.OpenGL, "OpenGL");
        map.put(RenderingMode.Vulkan, "Vulkan");
        RenderingModes = Collections.unmodifiableMap(map);
    }

    public static final Map<DynamicResolutionMode, String> DynamicResolutionModes;
    static {
        Map<DynamicResolutionMode, String> map = new EnumMap<>(DynamicResolutionMode.class);
        map.put(DynamicResolutionMode.Default, null);
        map.put(DynamicResolutionMode.A240P, "77");
        map.put(DynamicResolutionMode.A360P, "230");
        map.put(DynamicResolutionMode.A480P, "410");
        map.put(DynamicResolutionMode.A720P, "922");
        map.put(DynamicResolutionMode.A1080P, "2074");
        map.put(DynamicResolutionMode.A1440P, "3686");
        map.put(DynamicResolutionMode.A2160P, "8294");
        DynamicResolutionModes = Collections.unmodifiableMap(map);
    }

    @Override
    public String getClassName() {
        return "FastFlagManager";
    }

    @Override
    public File getFileLocation() {
        File dir = new File(Paths.getModifications(), "ClientSettings");
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                Log.w("ClientSettings", "Could not create directory: " + dir.getAbsolutePath());
            }
        }
        return new File(dir, "ClientAppSettings.json");
    }


    @Override
    @SuppressWarnings("unchecked")
    protected Class<Map<String, Object>> getGenericTypeClass() {
        return (Class<Map<String, Object>>)(Class<?>) Map.class;
    }

    public void setValue(String key, String value) {
        prop.put(key, value);
    }

    public String getValue(String key) {
        Object value = prop.get(key);
        return value != null ? value.toString() : null;
    }

    public String getPreset(String name) {
        String flag = PresetFlags.get(name);
        return flag != null ? getValue(flag) : null;
    }

    public void setPreset(String prefix, String value) {
        for (Map.Entry<String, String> entry : PresetFlags.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                setValue(entry.getValue(), value);
            }
        }
    }

    public void setPresetEnum(String prefix, String target, String value) {
        for (Map.Entry<String, String> entry : PresetFlags.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                if (entry.getKey().startsWith(prefix + "." + target)) {
                    setValue(entry.getValue(), value);
                } else {
                    setValue(entry.getValue(), null);
                }
            }
        }
    }

    public <T extends Enum<T>> T getPresetEnum(Map<T, String> mapping, String prefix, String value) {
        for (Map.Entry<T, String> entry : mapping.entrySet()) {
            String name = entry.getValue();
            if (name == null || name.equals("None")) continue;

            String preset = getPreset(prefix + "." + name);
            if (value.equals(preset)) return entry.getKey();
        }

        if (!mapping.isEmpty()) {
            return mapping.keySet().iterator().next();
        }

        return null;
    }

    @Override
    public void save() {
        // convert values to string
        Map<String, Object> updated = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : prop.entrySet()) {
            updated.put(entry.getKey(), entry.getValue().toString());
        }

        prop = updated;
        super.save();

        originalProp = new LinkedHashMap<>(prop);
    }

    @Override
    public void load(boolean alertFailure) {
        super.load(alertFailure);
        originalProp = new LinkedHashMap<>(prop);

        if (!"7".equals(getPreset("Network.Log"))) {
            setPreset("Network.Log", "7");
        }

        if (!"False".equals(getPreset("Rendering.ManualFullscreen"))) {
            setPreset("Rendering.ManualFullscreen", "False");
        }
    }
}
