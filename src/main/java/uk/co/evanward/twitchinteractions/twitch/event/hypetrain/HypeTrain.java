package uk.co.evanward.twitchinteractions.twitch.event.hypetrain;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;

import java.util.*;

public class HypeTrain
{
    // TODO: Simplify `from`
    public enum Level
    {
        ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

        private static final Level[] levels = Arrays.stream(values()).sorted(Comparator.comparingInt(Level::getLevel)).toArray(Level[]::new);
        private final int level;

        Level(int level)
        {
            this.level = level;
        }

        public int getLevel()
        {
            return this.level;
        }

        public static Level from(int level)
        {
            return levels[level - 1];
        }

        /**
         * Get a list of entities to spawn based on the Hype Train level
         */
        public List<Entity> getEntities(ServerPlayerEntity player)
        {
            return getEntities(player, false);
        }

        /**
         * Get a list of buffed entities to spawn based on the Hype Train level
         */
        public List<Entity> getEntities(ServerPlayerEntity player, boolean buffed)
        {
            Random random = new Random();

            // Make a list of entities to spawn based on the level
            List<Entity> entities = new ArrayList<>();

            switch (this) {
                case ONE -> {
                    HostileEntity entity;
                    // Level 1 summons Spiders
                    if (random.nextInt(100) <= 20) {
                        // 20% chance to summon a Cave Spider
                        CaveSpiderEntity caveSpider = new CaveSpiderEntity(EntityType.CAVE_SPIDER, player.getWorld());
                        caveSpider.setTarget(player);

                        entity = caveSpider;
                    } else {
                        SpiderEntity spider = new SpiderEntity(EntityType.SPIDER, player.getWorld());
                        spider.setTarget(player);

                        entity = spider;
                    }

                    // Add strength to the mob, based on the current Hype Train level
                    if (buffed) {
                        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, -1, TwitchInteractions.hypeTrain.level.getLevel()));
                    }

                    entities.add(entity);
                }
                case TWO -> {
                    // Level 2 summons buffed Spiders and Zombies
                    ZombieEntity zombie = new ZombieEntity(EntityType.ZOMBIE, player.getWorld());
                    zombie.setTarget(player);

                    // 20% chance the Zombie is a baby
                    if (random.nextInt(100) <=20) {
                        zombie.setBaby(true);
                    }

                    // Add strength and armour to the Zombies, based on the current Hype Train level
                    if (buffed) {
                        zombie.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, -1, TwitchInteractions.hypeTrain.level.getLevel()));

                        buffEntity(zombie);
                    }

                    entities.add(zombie);
                    entities.addAll(ONE.getEntities(player, true));
                }
                case THREE -> {
                    // Level 3 summons buffed Spiders, buffed Zombies, and Skeletons
                    SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, player.getWorld());
                    skeleton.setTarget(player);

                    ItemStack bow = new ItemStack(Items.BOW);

                    // Add power to the Skeleton's bow and armour, based on the current Hype Train level
                    if (buffed) {
                        bow.addEnchantment(Enchantments.POWER, TwitchInteractions.hypeTrain.level.getLevel());

                        buffEntity(skeleton);
                    }

                    skeleton.equipStack(EquipmentSlot.MAINHAND, bow);

                    entities.add(skeleton);
                    entities.addAll(TWO.getEntities(player, true));
                }
                case FOUR -> {
                    // Level 4 summons buffed Spiders, buffed Zombies, buffed Skeletons, and Creepers
                    CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, player.getWorld());
                    creeper.setTarget(player);

                    // 20% chance the Creeper is charged or always charged if buffed
                    if (random.nextInt(100) <= 20 || buffed) {
                        NbtCompound nbt = new NbtCompound();
                        nbt.putBoolean("powered", true);

                        creeper.readCustomDataFromNbt(nbt);
                    }

                    entities.add(creeper);
                    entities.addAll(THREE.getEntities(player, true));
                }
                case FIVE -> {
                    // Level 5 summons buffed Spiders, super buffed Zombies, super buffed Skeletons, and charged Creepers
                    entities.addAll(FOUR.getEntities(player, true));
                }
            }

            return entities;
        }
    }

    private boolean active;
    private Level level;
    private int tick;

    public HypeTrain()
    {
        this.active = false;
        this.tick = 0;
        this.level = Level.ONE;
    }

    public boolean isActive()
    {
        return this.active;
    }

    public void start(Level level)
    {
        this.level = level;
        this.active = true;
    }

    public void end()
    {
        this.active = false;
        this.level = Level.ONE;
    }

    public void progress(Level level)
    {
        this.level = level;
    }

    /**
     * Summon a mob every 2-3 seconds whilst a Hype Train is active
     */
    public static void tick(MinecraftServer server)
    {
        // Tick the hype train by 1
        ++TwitchInteractions.hypeTrain.tick;

        ServerPlayerEntity player = ServerHelper.getConnectedPlayer();

        // Spawn an entity every 2-3 seconds if hype train is active
        if (TwitchInteractions.hypeTrain.isActive()) {
            if (TwitchInteractions.hypeTrain.tick % 50 == 0) {
                List<Entity> entities = TwitchInteractions.hypeTrain.level.getEntities(player);

                for (Entity entity : entities) {
                    ServerHelper.spawnEntity(entity);
                }
            }
        }
    }

    /**
     * Buff the given entity with armour based on the current level
     */
    private static void buffEntity(HostileEntity entity)
    {
        entity.equipStack(EquipmentSlot.HEAD, new ItemStack(TwitchInteractions.hypeTrain.level == Level.FIVE ? Items.DIAMOND_HELMET : Items.IRON_HELMET));
        entity.equipStack(EquipmentSlot.CHEST, new ItemStack(TwitchInteractions.hypeTrain.level == Level.FIVE ? Items.DIAMOND_CHESTPLATE : Items.IRON_CHESTPLATE));
        entity.equipStack(EquipmentSlot.LEGS, new ItemStack(TwitchInteractions.hypeTrain.level == Level.FIVE ? Items.DIAMOND_LEGGINGS : Items.IRON_LEGGINGS));
        entity.equipStack(EquipmentSlot.FEET, new ItemStack(TwitchInteractions.hypeTrain.level == Level.FIVE ? Items.DIAMOND_BOOTS : Items.IRON_BOOTS));
    }
}
