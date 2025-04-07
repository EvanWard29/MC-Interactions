package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

import java.util.Random;

public class RandomItemRedemption implements ChannelPoint.ChannelPointInterface
{
    @Override
    public void trigger(JSONObject event)
    {
        AnnouncementHelper.playAnnouncement(event.getString("user_name"), "Gave An Item!");

        ItemStack item;
        if ((new Random()).nextInt(100) <= 5) {
            // 5% chance to give a super tool
            Item[] tools = {Items.NETHERITE_AXE, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL};

            item = new ItemStack(RegistryEntry.of(tools[(new Random()).nextInt(tools.length)]));

            // Add OP enchants
            item.addEnchantment(ServerHelper.getServer().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(ServerHelper.getServer().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).get(Enchantments.EFFICIENCY)), 100);
            item.addEnchantment(ServerHelper.getServer().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(ServerHelper.getServer().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).get(Enchantments.VANISHING_CURSE)), 100);

            // Set unbreakable
            item.set(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true));

            // Set custom name
            item.set(DataComponentTypes.ITEM_NAME, Text.literal("Super " + item.getName().getString()));
        } else {
            // 95% chance to give a completely random item
            item = new ItemStack(
                ServerHelper.getServer()
                    .getRegistryManager()
                    .getOrThrow(RegistryKeys.ITEM)
                    .getRandom(ServerHelper.getConnectedPlayer().getRandom())
                    .get()
                    .value()
            );

            // Return a stack of the random item
            item.setCount(item.getItem().getMaxCount());
        }

        ServerHelper.giveItem(item);
    }
}
