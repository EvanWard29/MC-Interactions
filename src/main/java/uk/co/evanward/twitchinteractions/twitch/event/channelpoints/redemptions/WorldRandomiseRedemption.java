package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.MathHelper;
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

        TwitchInteractions.worldChanges.setDirty(true);
    }
}
