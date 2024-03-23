package uk.co.evanward.twitchinteractions.twitch.event.hypetrain;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.*;
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

        public List<Entity> getEntities(ServerPlayerEntity player)
        {
            Random random = new Random();

            // Make a list of entities to spawn based on the level
            List<Entity> entities = new ArrayList<>();

            switch (this) {
                case ONE -> {
                    // Level 1 summons Spiders
                    if (random.nextInt(100) <= 20) {
                        // 20% chance to summon a Cave Spider
                        CaveSpiderEntity caveSpider = new CaveSpiderEntity(EntityType.CAVE_SPIDER, player.getWorld());
                        caveSpider.setTarget(player);

                        entities.add(caveSpider);
                    } else {
                        SpiderEntity spider = new SpiderEntity(EntityType.SPIDER, player.getWorld());
                        spider.setTarget(player);

                        entities.add(spider);
                    }
                }
                case TWO -> {
                    // Level 2 summons buffed Spiders and Zombies
                    ZombieEntity zombie = new ZombieEntity(EntityType.ZOMBIE, player.getWorld());
                    zombie.setTarget(player);

                    // 20% chance the Zombie is a baby
                    if (random.nextInt(100) <=20) {
                        zombie.setBaby(true);
                    }

                    entities.add(zombie);
                    entities.addAll(ONE.getEntities(player));
                }
                case THREE -> {
                    // Level 3 summons buffed Spiders, buffed Zombies, and Skeletons
                    SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, player.getWorld());
                    skeleton.setTarget(player);

                    entities.add(skeleton);
                    entities.addAll(TWO.getEntities(player));
                }
                case FOUR -> {
                    // Level 4 summons buffed Spiders, buffed Zombies, buffed Skeletons, and Creepers
                    CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, player.getWorld());
                    creeper.setTarget(player);

                    // 20% chance the creeper is charged
                    if (random.nextInt(100) <= 20) {
                        NbtCompound nbt = new NbtCompound();
                        nbt.putBoolean("powered", true);

                        creeper.readCustomDataFromNbt(nbt);
                    }

                    entities.add(creeper);
                    entities.addAll(THREE.getEntities(player));
                }
                case FIVE -> {
                    // Level 5 summons buffed Spiders, super buffed Zombies, super buffed Skeletons, and creepers
                    entities.addAll(FOUR.getEntities(player));
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
}
