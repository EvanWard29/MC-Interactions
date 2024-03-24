package uk.co.evanward.twitchinteractions.twitch.event.channelpoints;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions.FriendCreatorRedemption;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions.SetDayRedemption;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions.SetNightRedemption;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions.TeleportHomeRedemption;

public class ChannelPoint
{
    public interface ChannelPointInterface
    {
        void trigger(JSONObject event);
    }

    enum Redemption
    {
        FRIEND_CREATOR("e11c6413-5220-4567-9261-0bf1fba7a19c"),
        TELEPORT_HOME("eb45b111-e635-4b4b-a539-7d0718943f0d"),
        SET_DAY("fc87018c-de1b-4a0e-8506-c2d01c584029"),
        SET_NIGHT("6fe53a3d-2fb3-409e-a579-d473783b4776"),
        MEAN("7a645ff5-ba7b-48a9-86c8-9fb4e410ac43"),
        GAMBLE("37dd369a-f460-45ae-87b9-bcf98febf2af"),
        EXTREME_GAMBLE("865e32f1-aec3-4950-abaa-a05ee6296e4c");

        private final String rewardId;

        Redemption(String rewardId)
        {
            this.rewardId = rewardId;
        }

        public static Redemption from(String rewardId)
        {
            for (Redemption redemption : Redemption.values()) {
                if (redemption.rewardId.equalsIgnoreCase(rewardId)) {
                    return redemption;
                }
            }

            throw new IllegalArgumentException("No enum with reward id `" + rewardId + "` found");
        }
    }

    private final Redemption redemption;

    public ChannelPoint(String rewardId)
    {
        this.redemption = Redemption.from(rewardId);
    }

    public ChannelPointInterface getRedemption()
    {
        switch (redemption) {
            case SET_DAY -> {
                return new SetDayRedemption();
            }
            case SET_NIGHT -> {
                return new SetNightRedemption();
            }
            case TELEPORT_HOME -> {
                return new TeleportHomeRedemption();
            }
            case FRIEND_CREATOR -> {
                return new FriendCreatorRedemption();
            }
            default -> throw new IllegalArgumentException("Unsupported redemption `" + redemption + "`");
        }
    }
}
