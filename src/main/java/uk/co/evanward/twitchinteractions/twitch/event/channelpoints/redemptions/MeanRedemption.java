package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

import java.util.Random;

public class MeanRedemption implements ChannelPoint.ChannelPointInterface
{
    private enum Actions implements Action
    {
        PIGLIN_BRUTE(5), POLAR_BEAR(5), UNDEAD_HORSES(5), EVOKER(5), PUFFERFISH(5),
        WITCH(5), BLAZE(5), WITHER_SKELETON(10), SILVERFISH(10), VEX(10),
        CREEPER(10), LIGHTNING(10), OBSIDIAN_BOX(10);

        private final int weight;

        Actions(int weight)
        {
            this.weight = weight;
        }

        @Override
        public void execute()
        {
            switch(this) {
                case PIGLIN_BRUTE -> {
                    PiglinBruteEntity piglinBrute = new PiglinBruteEntity(EntityType.PIGLIN_BRUTE, player.getWorld());
                    piglinBrute.setAttacking(player);
                    piglinBrute.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.GOLDEN_AXE));

                    ServerHelper.spawnEntity(piglinBrute);
                }
                case WITHER_SKELETON -> {
                    WitherSkeletonEntity witherSkeleton = new WitherSkeletonEntity(EntityType.WITHER_SKELETON, player.getWorld());
                    witherSkeleton.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.STONE_SWORD));
                    witherSkeleton.setTarget(player);

                    ServerHelper.spawnEntity(witherSkeleton);
                }
                case SILVERFISH -> {
                    for (int i = 0; i < 20; i++) {
                        SilverfishEntity silverfish = new SilverfishEntity(EntityType.SILVERFISH, player.getWorld());
                        silverfish.setTarget(player);

                        ServerHelper.spawnEntity(silverfish);
                    }
                }
                case PUFFERFISH -> {
                    for (int i = 0; i < 3; i++) {
                        PufferfishEntity pufferfish = new PufferfishEntity(EntityType.PUFFERFISH, player.getWorld());
                        pufferfish.setTarget(player);
                        pufferfish.setPosition(player.getPos());

                        // Summon pufferfish directly on the player
                        player.getServerWorld().spawnEntity(pufferfish);
                    }
                }
                case UNDEAD_HORSES -> {
                    // Summon 5 Zombie Horses with Zombie riders
                    for (int i = 0; i < 5; i++) {
                        ZombieHorseEntity zombieHorse = new ZombieHorseEntity(EntityType.ZOMBIE_HORSE, player.getWorld());

                        ZombieEntity zombie = new ZombieEntity(EntityType.ZOMBIE, player.getWorld());
                        zombie.setTarget(player);
                        equipArmour(zombie);

                        zombie.startRiding(zombieHorse);

                        zombieHorse.setCustomName(Text.literal(username));
                        zombieHorse.setCustomNameVisible(true);

                        ServerHelper.spawnEntity(zombieHorse);
                    }

                    // Summon 5 Skeleton Horses with Skeleton riders
                    for (int i = 0; i < 5; i++) {
                        SkeletonHorseEntity skeletonHorse = new SkeletonHorseEntity(EntityType.SKELETON_HORSE, player.getWorld());

                        SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, player.getWorld());
                        skeleton.setTarget(player);
                        equipArmour(skeleton);

                        // Add a bow to the Skeleton
                        skeleton.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BOW));

                        skeletonHorse.setCustomName(Text.literal(username));
                        skeletonHorse.setCustomNameVisible(true);

                        skeleton.startRiding(skeletonHorse);

                        ServerHelper.spawnEntity(skeletonHorse);
                    }
                }
                case LIGHTNING -> {
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, player.getWorld());
                    lightning.setPosition(player.getPos());

                    // Manually spawn at the player's location, rather than around
                    player.getServerWorld().spawnEntity(lightning);
                }
                case CREEPER -> {
                    CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, player.getWorld());
                    creeper.setTarget(player);

                    // 5% chance to charge the creeper
                    if ((new Random()).nextInt(100) <= 5) {
                        NbtCompound nbt = new NbtCompound();
                        nbt.putBoolean("powered", true);

                        creeper.readCustomDataFromNbt(nbt);
                    }

                    ServerHelper.spawnEntity(creeper);
                }
                case EVOKER -> {
                    EvokerEntity evoker = new EvokerEntity(EntityType.EVOKER, player.getWorld());
                    evoker.setTarget(player);

                    ServerHelper.spawnEntity(evoker);
                }
                case BLAZE -> {
                    BlazeEntity blaze = new BlazeEntity(EntityType.BLAZE, player.getWorld());
                    blaze.setTarget(player);

                    ServerHelper.spawnEntity(blaze);
                }
                case VEX -> {
                    for (int i = 0; i < 10; i++) {
                        VexEntity vex = new VexEntity(EntityType.VEX, player.getWorld());
                        vex.setTarget(player);

                        ServerHelper.spawnEntity(vex);
                    }
                }
                case OBSIDIAN_BOX -> {
                    // Place obsidian at the players feet
                    player.getServerWorld().setBlockState(player.getSteppingPos(), Blocks.OBSIDIAN.getDefaultState());

                    // Surround the player in box, 4 blocks in height, from their feet
                    for (int i = -1; i < 3; i++) {
                        surroundObsidian(player.getBlockPos().up(i));
                    }

                    // Place lid
                    player.getServerWorld().setBlockState(player.getSteppingPos().up(3), Blocks.OBSIDIAN.getDefaultState());
                }
                case POLAR_BEAR -> {
                    PolarBearEntity polarBear = new PolarBearEntity(EntityType.POLAR_BEAR, player.getWorld());
                    polarBear.setTarget(player);

                    ServerHelper.spawnEntity(polarBear);
                }
                case WITCH -> {
                    WitchEntity witch = new WitchEntity(EntityType.WITCH, player.getWorld());
                    witch.setTarget(player);

                    ServerHelper.spawnEntity(witch);
                }
                default -> throw new IllegalArgumentException("Unsupported Action enum `" + this + "`");
            }
        }

        public static int getTotalWeight()
        {
            int totalWeight = 0;

            for (Actions action : Actions.values()) {
                totalWeight += action.weight;
            }

            return totalWeight;
        }
    }

    private static String username;
    private static ServerPlayerEntity player;

    /**
     * Perform a mean action:
     * <ul>
     *     <li>Spawn Piglin Brute - 5%</li>
     *     <li>Spawn Polar Bear - 5%</li>
     *     <li>Spawn Zombie & Skeleton Horses - 5%</li>
     *     <li>Spawn Evoker - 5%</li>
     *     <li>Spawn Puffer Fish - 5%</li>
     *     <li>Spawn Witch - 5%</li>
     *     <li>Spawn Blaze - 5%</li>
     *     <li>Spawn Wither Skeleton - 10%</li>
     *     <li>Spawn Silverfish - 10%</li>
     *     <li>Spawn Vex - 10%</li>
     *     <li>Spawn Creeper - 10%</li>
     *     <li>Spawn Lightning - 10%</li>
     *     <li>Obsidian Box - 10%</li>
     * </ul>
     */
    @Override
    public void trigger(JSONObject event)
    {
        username = event.getString("user_name");
        player = ServerHelper.getConnectedPlayer();

        AnnouncementHelper.playAnnouncement(username, "Is Feeling Mean!");

        Action action = random();
        action.execute();

        TwitchInteractions.logger.info(action.toString());
    }

    private Action random()
    {
        int totalWeight = Actions.getTotalWeight();

        // Get a random number between 1 and the total weight
        int random = (new Random()).nextInt(totalWeight);

        int cursor = 0;
        for (int i = 0; i < Actions.values().length; i++) {
            cursor += Actions.values()[i].weight;
            if (cursor >= random) {
                return Actions.values()[i];
            }
        }

        throw new RuntimeException("Error getting random action");
    }

    /**
     * Equip the given hostile entity with Golden Armour
     */
    private static void equipArmour(HostileEntity entity)
    {
        entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
        entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
        entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
        entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
    }

    /**
     * Surround the given position with Obsidian
     */
    private static void surroundObsidian(BlockPos blockPos)
    {
        player.getServerWorld().setBlockState(blockPos.north(), Blocks.OBSIDIAN.getDefaultState());
        player.getServerWorld().setBlockState(blockPos.east(), Blocks.OBSIDIAN.getDefaultState());
        player.getServerWorld().setBlockState(blockPos.south(), Blocks.OBSIDIAN.getDefaultState());
        player.getServerWorld().setBlockState(blockPos.west(), Blocks.OBSIDIAN.getDefaultState());
    }
}
