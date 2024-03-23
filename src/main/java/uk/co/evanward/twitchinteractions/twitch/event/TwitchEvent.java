package uk.co.evanward.twitchinteractions.twitch.event;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.twitch.event.subscribe.GifSubscriptionEvent;
import uk.co.evanward.twitchinteractions.twitch.event.subscribe.SubscribeEvent;

import java.util.Objects;

public class TwitchEvent
{
    public interface TwitchEventInterface
    {
        Type getType();
        String getVersion();
        JSONObject getCondition();

        void trigger(JSONObject payload);
    }

    public enum Type {
        FOLLOW("channel.follow"),
        SUBSCRIBE("channel.subscribe"),
        RE_SUBSCRIBE("channel.subscription.message"),
        GIFT("channel.subscription.gift"),
        BITS("channel.cheer"),
        RAID("channel.raid"),
        CHANNEL_POINTS_REDEMPTION("channel.channel_points_custom_reward_redemption.add"),
        HYPE_TRAIN_START("channel.hype_train.begin"),
        HYPE_TRAIN_PROGRESS("channel.hype_train.progress"),
        HYPE_TRAIN_END("channel.hype_train.end");

        private final String type;

        Type(String type)
        {
            this.type = type;
        }

        public String getString()
        {
            return this.type;
        }

        public static Type from(String type)
        {
            for (Type twitchEventType : Type.values()) {
                if (Objects.equals(twitchEventType.type, type)) {
                    return twitchEventType;
                }
            }

            throw new RuntimeException("Unrecognised `Type` enum `" + type + "`");
        }
    }

    private final Type type;

    public TwitchEvent(Type type)
    {
        this.type = type;
    }

    public TwitchEventInterface getEvent()
    {
        switch(this.type) {
            case FOLLOW -> {
                return new FollowEvent();
            }
            case SUBSCRIBE, RE_SUBSCRIBE -> {
                return new SubscribeEvent();
            }
            case GIFT -> {
                return new GifSubscriptionEvent();
            }
            case BITS -> {
                return new CheerEvent();
            }
            case RAID -> {
                return new RaidEvent();
            }
            default -> throw new RuntimeException("Unsupported Twitch event `" + type + "`");
        }
    }
}
