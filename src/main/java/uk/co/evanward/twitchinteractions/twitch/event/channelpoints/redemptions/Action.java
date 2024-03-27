package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

public interface Action
{
    /**
     * Get the wight of the action
     */
    int getWeight();

    /**
     * Execute the action
     */
    void execute();
}
