package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

import java.util.Random;

public class RandomEffectRedemption implements ChannelPoint.ChannelPointInterface
{
    @Override
    public void trigger(JSONObject event)
    {
        AnnouncementHelper.playAnnouncement(event.getString("user_name"), "Splashed You With A Potion!");

        // Get a random potion effect that isn't instant
        StatusEffect effect;
        do {
            effect = ServerHelper.getServer()
                .getRegistryManager()
                .getOrThrow(RegistryKeys.STATUS_EFFECT)
                .getRandom(ServerHelper.getConnectedPlayer().getRandom())
                .get()
                .value();
        } while (effect.isInstant());

        // Apply the effect for 30-60 seconds
        int duration = (new Random()).nextInt(30, 60 + 1) * 20;

        ServerHelper.getConnectedPlayer().addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(effect), duration));
    }
}
