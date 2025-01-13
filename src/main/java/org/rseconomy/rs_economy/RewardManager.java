package org.rseconomy.rs_economy;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RewardManager {
    private final BalanceManager balanceManager;
    private final Map<UUID, LocalDate> lastClaimedRewards = new HashMap<>();
    private final Random random = new Random();

    public RewardManager(BalanceManager balanceManager) {
        this.balanceManager = balanceManager;
    }

    /**
     * Allows players to claim their daily rewards.
     * Rewards are only claimable once per day per player.
     *
     * @param player The player claiming the reward.
     * @return 1 if reward is successfully claimed, 0 otherwise.
     */
    public int claimDailyReward(ServerPlayer player) {
        UUID playerId = player.getUUID();
        LocalDate today = LocalDate.now();
        if (lastClaimedRewards.containsKey(playerId) && lastClaimedRewards.get(playerId).equals(today)) {
            player.sendSystemMessage(Component.literal(Localization.get("reward.daily.alreadyclaimed")));
            return 0;
        }

        lastClaimedRewards.put(playerId, today);
        double rewardAmount = 100 + random.nextInt(400);
        balanceManager.addBalance(playerId, rewardAmount);
        player.sendSystemMessage(Component.literal(Localization.get("reward.daily.redeem", rewardAmount, BalanceManager.CURRENCY)));
        return 1;
    }

    public Map<UUID, LocalDate> getLastClaimedRewards() {
        return new HashMap<>(lastClaimedRewards);
    }

    public void loadDailyRewards(Map<UUID, LocalDate> rewards) {
        lastClaimedRewards.putAll(rewards);
    }
}