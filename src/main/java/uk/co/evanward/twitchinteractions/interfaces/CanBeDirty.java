package uk.co.evanward.twitchinteractions.interfaces;

public interface CanBeDirty
{
    /**
     * Check if the NBT has been changed
     */
    default boolean isDirty() {
        return false;
    }

    /**
     * Mark the NBT as dirty
     */
    default void markDirty() {}

    /**
     * Mark the NBT as clean
     */
    default void clean() {};
}
