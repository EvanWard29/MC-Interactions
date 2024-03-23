package uk.co.evanward.twitchinteractions.twitch.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.config.ModConfig;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

import java.util.Random;

public class RaidEvent implements TwitchEvent.TwitchEventInterface
{
    private enum RaidEntity
    {
        ZOMBIE, SKELETON, CREEPER;

        public Entity getEntity(ServerPlayerEntity player)
        {
            switch (this) {
                case ZOMBIE -> {
                    ZombieEntity zombie = new ZombieEntity(EntityType.ZOMBIE, player.getWorld());
                    zombie.setTarget(player);
                    zombie.setBaby((new Random()).nextInt(100) < 40); // 40% chance to be a baby
                    zombie.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));

                    return zombie;
                }
                case SKELETON -> {
                    SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, player.getWorld());
                    skeleton.setTarget(player);
                    skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));

                    // Add bow
                    skeleton.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BOW));

                    return skeleton;
                }
                case CREEPER -> {
                    CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, player.getWorld());
                    creeper.setTarget(player);

                    // 30% chance the creeper is charged
                    if ((new Random()).nextInt(100) <= 30) {
                        NbtCompound nbt = new NbtCompound();
                        nbt.putBoolean("powered", true);

                        creeper.writeCustomDataToNbt(nbt);
                    }

                    return creeper;
                }
                default -> throw new RuntimeException("Unsupported RaidEntity `" + this + "`");
            }
        }
    }

    @Override
    public TwitchEvent.Type getType()
    {
        return TwitchEvent.Type.RAID;
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
            .put("to_broadcaster_user_id", ModConfig.BROADCASTER_ID);
    }

    /**
     * Summon mobs based on the raid party size
     */
    @Override
    public void trigger(JSONObject payload)
    {
        JSONObject event = payload.getJSONObject("event");
        int raiders =  Math.min(event.getInt("viewers"), 1000); // Restrict raiders to 1000 to avoid performance issues

        AnnouncementHelper.playAnnouncement(event.getString("from_broadcaster_user_name"), "Raided With " + raiders + " Raiders!");

        ServerPlayerEntity player = ServerHelper.getConnectedPlayer();

        Random random = new Random();

        // Summon a mob for every raider (max 1000)
        for (int i = 0; i < raiders; i++) {
            // Generate a random number between 1 & 100
            int j = random.nextInt(100);

            Entity entity;
            if (j <= 50) {
                // 50% chance the mob will be a Zombie
                entity = RaidEntity.ZOMBIE.getEntity(player);
            } else if (j <= 75) {
                // 35% chance the mob will be a Skeleton
                entity = RaidEntity.SKELETON.getEntity(player);
            } else {
                // 15% chance the mob will be a Creeper
                entity = RaidEntity.CREEPER.getEntity(player);
            }

            ServerHelper.spawnEntity(entity);
        }

        // Summon an Ender Dragon for 100+ raiders
        if (raiders >= 100) {
            EnderDragonEntity enderDragon = new EnderDragonEntity(EntityType.ENDER_DRAGON, player.getWorld());
            enderDragon.setTarget(player);

            ServerHelper.spawnEntity(enderDragon);
        }

        // Summon a wither for every 100th viewer past 500 up until 1000 (max 5 Withers)
        if (raiders >= 500) {
            for (int i = 0; i < Math.ceil((double) ((raiders - 400)) / 100); i++) {
                WitherEntity wither = new WitherEntity(EntityType.WITHER, player.getWorld());
                wither.setTarget(player);

                ServerHelper.spawnEntity(wither);
            }
        }
    }
}
