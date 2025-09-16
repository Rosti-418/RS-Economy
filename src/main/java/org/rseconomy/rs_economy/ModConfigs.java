package org.rseconomy.rs_economy;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;

public class ModConfigs {
    public static final ModConfigSpec COMMON_CONFIG;
    public static final ModConfigSpec.BooleanValue FEATURE_X;
    public static final ModConfigSpec.IntValue MAX_Y;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Allgemeine Einstellungen").push("general");

        FEATURE_X = builder
                .comment("Soll Feature X aktiviert sein?")
                .define("featureX", true);

        MAX_Y = builder
                .comment("Maximale Anzahl von Y")
                .defineInRange("maxY", 10, 1, 100);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    public static void loadConfig(ModConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(com.electronwill.nightconfig.core.file.WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }
}