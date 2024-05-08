package uk.co.evanward.twitchinteractions.twitch.event.channelpoints;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.exceptions.UnsupportedChannelPointException;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions.*;

public class ChannelPoint
{
    public interface ChannelPointInterface
    {
        void trigger(JSONObject event);
    }

    enum Redemption
    {
        FRIEND_CREATOR("95659d42-8fbb-4cd3-afe4-953198a401b7"),
        TELEPORT_HOME("54aad035-c47a-425a-a1f8-50245ba1b7f5"),
        SET_DAY("469a5355-eafd-43da-9432-d8357b8a7be1"),
        SET_NIGHT("99877225-b426-4537-bc79-ef5b6f2845ae"),
        MEAN("b5134ef3-cd2e-40c9-afcd-4bee4e7acc88"),
        GAMBLE("5b80bca8-edb5-40bc-af5a-c8db5960f857"),
        EXTREME_GAMBLE("9af2dd6f-b95c-4d46-ba96-8a5d89cff6ef"),
        RANDOM_EFFECT("7153472f-291f-4b54-b5c1-8221f4286996"),
        RANDOM_ITEM("9968d4a0-e7ef-44c5-af54-9afad23f3734"),
        WORLD_RANDOMISE("6a3fdbc0-5145-4658-8e6e-6f7f038c3c28");

        private final String rewardId;

        Redemption(String rewardId)
        {
            this.rewardId = rewardId;
        }

        public static Redemption from(String rewardId) throws UnsupportedChannelPointException
        {
            for (Redemption redemption : Redemption.values()) {
                if (redemption.rewardId.equalsIgnoreCase(rewardId)) {
                    return redemption;
                }
            }

            throw new UnsupportedChannelPointException("No enum with reward id `" + rewardId + "` found");
        }
    }

    private final Redemption redemption;

    public ChannelPoint(String rewardId) throws UnsupportedChannelPointException
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
            case MEAN -> {
                return new MeanRedemption();
            }
            case GAMBLE -> {
                return new GambleRedemption();
            }
            case EXTREME_GAMBLE -> {
                return new ExtremeGambleRedemption();
            }
            case RANDOM_EFFECT -> {
                return new RandomEffectRedemption();
            }
            case RANDOM_ITEM -> {
                return new RandomItemRedemption();
            }
            case WORLD_RANDOMISE -> {
                return new WorldRandomiseRedemption();
            }
            default -> throw new IllegalArgumentException("Unsupported redemption `" + redemption + "`");
        }
    }
}
