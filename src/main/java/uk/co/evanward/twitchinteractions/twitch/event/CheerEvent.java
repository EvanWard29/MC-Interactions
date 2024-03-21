package uk.co.evanward.twitchinteractions.twitch.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

import java.util.Random;

public class CheerEvent implements TwitchEvent.TwitchEventInterface
{
    private enum CheerEntities
    {
        CHICKEN, COW, SHEEP, PIG, FROG;

        public Entity getEntity(World world)
        {
            switch (this) {
                case CHICKEN -> {
                    return new ChickenEntity(EntityType.CHICKEN, world);
                }
                case COW -> {
                    return new CowEntity(EntityType.COW, world);
                }
                case SHEEP -> {
                    return new SheepEntity(EntityType.SHEEP, world);
                }
                case PIG -> {
                    return new PigEntity(EntityType.PIG, world);
                }
                case FROG -> {
                    FrogEntity frog = new FrogEntity(EntityType.FROG, world);
                    frog.setVariant(Registries.FROG_VARIANT.get((new Random()).nextInt(Registries.FROG_VARIANT.size())));

                    return frog;
                }
                default -> throw new RuntimeException("Unrecognised CheerEntity enum " + this);
            }
        }
    }

    @Override
    public TwitchEvent.Type getType()
    {
        return TwitchEvent.Type.BITS;
    }

    @Override
    public String getVersion()
    {
        return "1";
    }

    @Override
    public JSONObject getCondition()
    {
        return new JSONObject()
            .put("broadcaster_user_id", ModConfig.BROADCASTER_ID);
    }

    /**
     * Summon a random Chicken, Cow, Sheep, Pig, or Frog for each 10th of the total number of bits
     */
    @Override
    public void trigger(JSONObject payload)
    {
        JSONObject event = payload.getJSONObject("event");

        // Set bits to the next 10th
        int bits = (int) Math.ceil((double) event.getInt("bits") / 10);

        AnnouncementHelper.playAnnouncement(event.getString("user_name"), "Just Cheered " + event.getInt("bits") + " bits!");

        ServerPlayerEntity player = ServerHelper.getConnectedPlayer();

        for (int i = 0; i < bits; i++) {
            Random random = new Random();
            Entity entity = CheerEntities.values()[random.nextInt(CheerEntities.values().length)].getEntity(player.getWorld());
            entity.setCustomName(Text.literal(!event.getBoolean("is_anonymous") ? event.getString("user_name") : "A Cool User"));
            entity.setCustomNameVisible(true);

            // Get a random position within a 10 block square of the player that is safe for the mob to spawn on
            do {
                // Set the position to the centre of the block
                double x = (random.nextBoolean() ? player.getBlockX() + random.nextInt(10) : player.getBlockX() - random.nextInt(10)) + 0.5;
                double z = (random.nextBoolean() ? player.getBlockZ() + random.nextInt(10) : player.getBlockZ() - random.nextInt(10)) + 0.5;
                int y = player.getBlockY();

                Vec3d pos = new Vec3d(x, y, z);
                entity.setPosition(pos);
            } while (entity.collidesWithStateAtPos(entity.getBlockPos(), player.getServerWorld().getBlockState(entity.getBlockPos())));

            player.getServerWorld().spawnEntity(entity);
        }
    }
}
