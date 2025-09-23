package org.rseconomy.rs_economy;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;

public class ModConfigs {
    public static final ModConfigSpec COMMON_CONFIG;
    public static final ModConfigSpec.ConfigValue<String> CURRENCY;
    public static final ModConfigSpec.ConfigValue<String> LOCALE;
    public static final ModConfigSpec.ConfigValue<Integer> DAILY_REWARD_MIN;
    public static final ModConfigSpec.ConfigValue<Integer> DAILY_REWARD_MAX;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("RS-ECONOMY GENERAL CONFIG").push("general");

        CURRENCY = builder
                .comment("Currency name used in the economy system")
                .define("currency", "Coins", s -> s instanceof String && !((String) s).isEmpty());

        LOCALE = builder
                .comment("Locale for the mod (e.g., en_US, de_DE)")
                .define("locale", "en_US", s -> s instanceof String && ((String) s).matches("[a-z]{2}_[A-Z]{2}"));

        DAILY_REWARD_MIN = builder
                .comment("Minimum daily reward amount")
                .define("daily.reward.min", 100, i -> i instanceof Integer);

        DAILY_REWARD_MAX = builder
                .comment("Maximum daily reward amount")
                .define("daily.reward.max", 500, i -> i instanceof Integer);

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