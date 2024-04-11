package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.DyeColor;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

import java.util.Random;

public class WorldRandomiseRedemption implements ChannelPoint.ChannelPointInterface
{
    /**
     * Change the following of the world:
     * <ul>
     *     <li>Item laid by chickens</li>
     *     <li>Natural mob spawns</li>
     *     <li>Amount outputted by a crafting recipe</li>
     *     <li>Dropped loot</li>
     *     <li>Double or half the max item stack size</li>
     *     <li>Replace sound with another</li>
     *     <li>Villager currency</li>
     *     <li>Length of day</li>
     *     <li>Sheep colour</li>
     *     <li>Item use speed</li>
     *     <li>Loot double or half</li>
     *     <li>Recipe output double or half</li>
     *     <li>Spawn egg chance</li>
     *     <li>Item despawn time</li>
     *     <li>Damage modifier - Change the damage value of a random damage source</li>
     * </ul>
     */
    @Override
    public void trigger(JSONObject event)
    {
        // Set the item Chickens lay to a random item
        TwitchInteractions.worldChanges.CHICKEN_EGG = ServerHelper.getServer()
            .getRegistryManager()
            .get(RegistryKeys.ITEM)
            .getRandom(ServerHelper.getConnectedPlayer().getRandom())
            .get()
            .value();

        // Double or half the length of day
        int newDayLength = (new Random()).nextBoolean()
            ? TwitchInteractions.worldChanges.DAY_LENGTH * 2
            : TwitchInteractions.worldChanges.DAY_LENGTH / 2;

        // Reset to 24000 ticks (20:00) if day is less than 1500 ticks (1:15)
        TwitchInteractions.worldChanges.DAY_LENGTH = newDayLength < 1500
            ? 24000
            : newDayLength;

        // Change the default Sheep colour
        TwitchInteractions.worldChanges.SHEEP_COLOUR = DyeColor.values()[new Random().nextInt(DyeColor.values().length)];

        // Double or half the item despawn time
        int despawnTime = (new Random()).nextBoolean()
            ? TwitchInteractions.worldChanges.ITEM_DESPAWN * 2
            : TwitchInteractions.worldChanges.ITEM_DESPAWN / 2;

        // 10% chance to reset the timer
        if ((new Random()).nextInt(100) <= 10) {
            despawnTime = 6000;
        }

        // Set the despawn time to 6000 (5:00) if less than 200 (00:05)
        TwitchInteractions.worldChanges.ITEM_DESPAWN = despawnTime < 600
            ? 6000
            : despawnTime;

        // Get a random damage type
        DamageType damageType = ServerHelper.getConnectedPlayer()
            .getDamageSources()
            .registry
            .getRandom(ServerHelper.getConnectedPlayer().getRandom())
            .get()
            .value();

        // Get the existing modifier of the random damage type (if set already)
        float damageModifier = TwitchInteractions.worldChanges.DAMAGE_MODIFIERS.getFloat(damageType.toString());

        // Set the modifier to unmodified if not already modified
        if (!(damageModifier > 0.0f)) {
            damageModifier = 1.0f;
        }

        // Double or half the damage modifier
        TwitchInteractions.worldChanges.DAMAGE_MODIFIERS.putFloat(
            damageType.msgId(),
            (new Random()).nextBoolean()
                ? damageModifier * 2
                : damageModifier / 2
        );

        TwitchInteractions.worldChanges.setDirty(true);
    }
}
