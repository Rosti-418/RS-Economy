package org.rseconomy.rs_economy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@EventBusSubscriber(modid = RSEconomy.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}