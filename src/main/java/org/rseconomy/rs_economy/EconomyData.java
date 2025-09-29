package org.rseconomy.rs_economy;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class EconomyData extends SavedData {
    private final Map<UUID, Double> balances = new HashMap<>();
    private final Map<UUID, LocalDate> dailyRewards = new HashMap<>();
    // === Factory / Loader für DimensionDataStorage#computeIfAbsent ===
// Supplier für "neu erstellen"
    public static EconomyData create() {
        return new EconomyData();
    }
    // Deserializer: wird beim Laden aus NBT aufgerufen (Hinweis: HolderLookup.Provider notwendig)
    public static EconomyData load(CompoundTag tag, HolderLookup.Provider provider) {
        EconomyData data = new EconomyData();
        if (tag.contains("balances")) {
            CompoundTag balancesTag = tag.getCompound("balances");
            for (String uuidStr : balancesTag.getAllKeys()) {
                UUID uuid = UUID.fromString(uuidStr);
                Tag playerTag = balancesTag.get(uuidStr);
                double amount;
                if (playerTag instanceof CompoundTag) {
// Old format: sum all currencies
                    amount = 0;
                    CompoundTag ct = (CompoundTag) playerTag;
                    for (String key : ct.getAllKeys()) {
                        amount += ct.getDouble(key);
                    }
                } else {
                    amount = balancesTag.getDouble(uuidStr);
                }
                data.balances.put(uuid, amount);
            }
        }
        if (tag.contains("dailyRewards")) {
            CompoundTag rewardsTag = tag.getCompound("dailyRewards");
            for (String uuidStr : rewardsTag.getAllKeys()) {
                UUID uuid = UUID.fromString(uuidStr);
                LocalDate date = LocalDate.parse(rewardsTag.getString(uuidStr));
                data.dailyRewards.put(uuid, date);
            }
        }
        return data;
    }
    // === Speichern (korrekte Signatur für NeoForge: CompoundTag + HolderLookup.Provider) ===
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag balancesTag = new CompoundTag();
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            balancesTag.putDouble(entry.getKey().toString(), entry.getValue());
        }
        tag.put("balances", balancesTag);
        CompoundTag rewardsTag = new CompoundTag();
        for (Map.Entry<UUID, LocalDate> entry : dailyRewards.entrySet()) {
            rewardsTag.putString(entry.getKey().toString(), entry.getValue().toString());
        }
        tag.put("dailyRewards", rewardsTag);
        return tag;
    }
    // === Helfer: get / set (API) ===
    public static EconomyData get(ServerLevel level) {
// Wichtig: computeIfAbsent erwartet eine SavedData.Factory (Supplier + BiFunction)
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(EconomyData::create, EconomyData::load),
                "rs_economy"
        );
    }
    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, amount);
        setDirty();
    }
    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0.0);
    }
    public void setDailyReward(UUID uuid, LocalDate date) {
        dailyRewards.put(uuid, date);
        setDirty();
    }
    public LocalDate getDailyReward(UUID uuid) {
        return dailyRewards.get(uuid);
    }
    public Map<UUID, Double> getBalances() {
        return balances;
    }
    public Map<UUID, LocalDate> getDailyRewards() {
        return dailyRewards;
    }
}