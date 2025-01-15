/*
 * Copyright (c) 2025 Rosti Studios. All rights reserved.
 * Licensed under the Rosti Studios Minecraft Mod License (RSMML).
 * For more information, see the LICENSE file in the project root
 * or contact us via Discord: https://dsc.gg/rosti-studios
 */

package org.rseconomy.rs_economy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

public class CommandManager {
    private final BalanceManager balanceManager;
    private final RewardManager rewardManager;

    public CommandManager(UserDataManager userDataManager, BalanceManager balanceManager, RewardManager rewardManager) {
        this.balanceManager = balanceManager;
        this.rewardManager = rewardManager;
    }

    /**
     * Registers all commands used by the economy system.
     * Commands include balance checks, payments, rewards, and admin actions.
     */
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Localization.get("command.pay"))
                .then(Commands.argument(Localization.get("sugg.player"), EntityArgument.player())
                        .then(Commands.argument(Localization.get("sugg.amount"), DoubleArgumentType.doubleArg(0))
                                .executes(context -> {
                                    ServerPlayer sender = context.getSource().getPlayerOrException();
                                    ServerPlayer receiver = EntityArgument.getPlayer(context, Localization.get("sugg.player"));
                                    double amount = DoubleArgumentType.getDouble(context, Localization.get("sugg.amount"));
                                    return handlePay(sender, receiver, amount);
                                }))));

        dispatcher.register(Commands.literal(Localization.get("command.balance"))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    double balance = balanceManager.getBalance(player.getUUID());
                    player.sendSystemMessage(Component.literal(Localization.get("balance.info", balance, BalanceManager.CURRENCY)));
                    return 1;
                })
                .then(Commands.argument(Localization.get("sugg.player"), EntityArgument.player())
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            ServerPlayer admin = context.getSource().getPlayerOrException();
                            ServerPlayer target = EntityArgument.getPlayer(context, Localization.get("sugg.player"));
                            double targetBalance = balanceManager.getBalance(target.getUUID());
                            admin.sendSystemMessage(Component.literal(Localization.get("balance.admin.info", target.getName().getString(), targetBalance, BalanceManager.CURRENCY)));
                            return 1;
                        })));

        dispatcher.register(Commands.literal(Localization.get("command.dailyreward"))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    return rewardManager.claimDailyReward(player);
                }));

        dispatcher.register(Commands.literal(Localization.get("command.rseco"))
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal(Localization.get("sugg.set"))
                        .then(Commands.argument(Localization.get("sugg.player"), EntityArgument.player())
                                .then(Commands.argument(Localization.get("sugg.amount"), DoubleArgumentType.doubleArg(0))
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, Localization.get("sugg.player"));
                                            double amount = DoubleArgumentType.getDouble(context, Localization.get("sugg.amount"));
                                            balanceManager.setBalance(player.getUUID(), amount);
                                            context.getSource().sendSuccess(() -> Component.literal(Localization.get("admin.balance.set", player.getName().getString(), amount, BalanceManager.CURRENCY)), true);
                                            return 1;
                                        }))))
                .then(Commands.literal(Localization.get("sugg.add"))
                        .then(Commands.argument(Localization.get("sugg.player"), EntityArgument.player())
                                .then(Commands.argument(Localization.get("sugg.amount"), DoubleArgumentType.doubleArg(0))
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, Localization.get("sugg.player"));
                                            double amount = DoubleArgumentType.getDouble(context, Localization.get("sugg.amount"));
                                            balanceManager.addBalance(player.getUUID(), amount);
                                            context.getSource().sendSuccess(() -> Component.literal(Localization.get("admin.balance.add", amount, BalanceManager.CURRENCY, player.getName().getString())), true);
                                            return 1;
                                        }))))
                .then(Commands.literal(Localization.get("sugg.remove"))
                        .then(Commands.argument(Localization.get("sugg.player"), EntityArgument.player())
                                .then(Commands.argument(Localization.get("sugg.amount"), DoubleArgumentType.doubleArg(0))
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, Localization.get("sugg.player"));
                                            double amount = DoubleArgumentType.getDouble(context, Localization.get("sugg.amount"));
                                            if (!balanceManager.subtractBalance(player.getUUID(), amount)) {
                                                context.getSource().sendFailure(Component.literal(Localization.get("admin.balance.remove.insufficient")));
                                                return 0;
                                            }
                                            context.getSource().sendSuccess(() -> Component.literal(Localization.get("admin.balance.remove", amount, BalanceManager.CURRENCY, player.getName().getString())), true);
                                            return 1;
                                        }))))
                .then(Commands.literal(Localization.get("sugg.rename"))
                        .then(Commands.argument(Localization.get("sugg.currencyname"), StringArgumentType.string())
                                .executes(context -> {
                                    String newCurrencyName = StringArgumentType.getString(context, Localization.get("sugg.currencyname"));
                                    ServerDataManager.setCurrency(newCurrencyName);
                                    balanceManager.loadBalance();
                                    context.getSource().sendSuccess(() -> Component.literal(Localization.get("admin.rename", BalanceManager.CURRENCY)), true);
                                    return 1;
                                })))
                .then(Commands.literal(Localization.get("sugg.dailyreward.set"))
                        .then(Commands.argument(Localization.get("sugg.dailyreward.min"), IntegerArgumentType.integer())
                            .then(Commands.argument(Localization.get("sugg.dailyreward.max"), IntegerArgumentType.integer())
                                    .executes(context -> {
                                        ServerDataManager.setDailyReward(IntegerArgumentType.getInteger(context, Localization.get("sugg.dailyreward.min")), IntegerArgumentType.getInteger(context, Localization.get("sugg.dailyreward.max")));
                                        context.getSource().sendSuccess(() -> Component.literal(Localization.get("admin.dailyreward", IntegerArgumentType.getInteger(context, Localization.get("sugg.dailyreward.min")), IntegerArgumentType.getInteger(context, Localization.get("sugg.dailyreward.max")))), true);
                                        return 1;
                                    }))))
                .then(Commands.literal(Localization.get("sugg.language"))
                        .then(Commands.argument(Localization.get("sugg.language.locale"), StringArgumentType.string())
                                .executes(context -> {
                                    String localeArgument = StringArgumentType.getString(context, Localization.get("sugg.language.locale"));
                                    Locale newLocale = Locale.forLanguageTag(localeArgument);
                                    //ServerDataManager.LOGGER.debug("Locale: " + newLocale.toString());
                                    context.getSource().sendSuccess(() -> Component.literal(Localization.get("admin.language", Localization.setLocale(newLocale))), true);
                                    return 1;
                                })))
                .then(Commands.literal(Localization.get("sugg.reload"))
                        .executes(context -> {
                            RSEconomy.getInstance().reload();
                            context.getSource().sendSuccess(() -> Component.literal(Localization.get("admin.reload")), true);
                            return 1;
                        })));
    }

    private int handlePay(ServerPlayer sender, ServerPlayer receiver, double amount) {
        if (amount <= 0) {
            sender.sendSystemMessage(Component.literal(Localization.get("pay.invalid")));
            return 0;
        }
        if (!balanceManager.subtractBalance(sender.getUUID(), amount)) {
            sender.sendSystemMessage(Component.literal(Localization.get("pay.insufficient")));
            return 0;
        }
        balanceManager.addBalance(receiver.getUUID(), amount);
        sender.sendSystemMessage(Component.literal(Localization.get("pay.send", amount, BalanceManager.CURRENCY, receiver.getName().getString())));
        receiver.sendSystemMessage(Component.literal(Localization.get("pay.receive", amount, BalanceManager.CURRENCY, sender.getName().getString())));
        return 1;
    }
}