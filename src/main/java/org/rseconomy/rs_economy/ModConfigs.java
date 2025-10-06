/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */
package org.rseconomy.rs_economy;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Defines configuration settings for the RSEconomy mod.
 */
public class ModConfigs {
    public static final ModConfigSpec COMMON_CONFIG;
    public static final ModConfigSpec.ConfigValue<String> CURRENCY;
    public static final ModConfigSpec.ConfigValue<String> LOCALE;
    public static final ModConfigSpec.ConfigValue<Integer> DAILY_REWARD_MIN;
    public static final ModConfigSpec.ConfigValue<Integer> DAILY_REWARD_MAX;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("RSEconomy General Configuration").push("general");

        CURRENCY = builder
                .comment("The name of the currency used in the economy system")
                .define("currency", "Coins", s -> s instanceof String && !((String) s).isEmpty());

        LOCALE = builder
                .comment("Locale for the mod (e.g., en_US, de_DE)")
                .define("locale", "en_US", s -> s instanceof String && ((String) s).matches("[a-z]{2}_[A-Z]{2}"));

        DAILY_REWARD_MIN = builder
                .comment("Minimum amount for daily rewards")
                .define("daily.reward.min", 100, i -> i instanceof Integer && (Integer) i >= 0);

        DAILY_REWARD_MAX = builder
                .comment("Maximum amount for daily rewards")
                .define("daily.reward.max", 500, i -> i instanceof Integer && (Integer) i >= 0);

        builder.pop();
        COMMON_CONFIG = builder.build();
    }
}