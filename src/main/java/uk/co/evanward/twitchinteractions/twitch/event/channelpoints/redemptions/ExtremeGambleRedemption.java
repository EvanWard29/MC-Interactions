package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.helpers.TwitchHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

import java.util.Random;

public class ExtremeGambleRedemption implements ChannelPoint.ChannelPointInterface
{
    private enum ExtremeGambleAction implements Action
    {
        WARDEN(1), CLEAR_INVENTORY(1), RAVAGERS(10), TELEPORT_NETHER(10), SLIME(10), TNT(10),
        DOUBLE_HEALTH(10), GHAST(15), CHEST_LOOT(15), NOTHING(18);

        private final int weight;

        ExtremeGambleAction(int weight)
        {
            this.weight = weight;
        }

        @Override
        public int getWeight()
        {
            return this.weight;
        }

        @Override
        public void execute()
        {
            switch (this) {
                case WARDEN -> {
                    WardenEntity warden = EntityType.WARDEN.create(player.getServerWorld(), null,
                        player.getSteppingPos().up().mutableCopy(), SpawnReason.TRIGGERED, false, false);

                    warden.setCustomName(Text.literal(username));
                    warden.setCustomNameVisible(true);
                    warden.setTarget(player);

                    ServerHelper.spawnEntity(warden);
                }
                case RAVAGERS -> {
                    for (int i = 0; i < 5; i++) {
                        RavagerEntity ravager = new RavagerEntity(EntityType.RAVAGER, player.getEntityWorld());
                        ravager.setTarget(player);
                        ravager.setCustomName(Text.literal(username));
                        ravager.setCustomNameVisible(true);

                        ServerHelper.spawnEntity(ravager);
                    }
                }
                case TELEPORT_NETHER -> {
                    // If the player is in the Overworld or End, teleport to Nether, otherwise teleport to Overworld
                    ServerWorld world = (player.getEntityWorld().getRegistryKey().equals(World.OVERWORLD) || player.getEntityWorld().getRegistryKey().equals(World.END))
                        ? ServerHelper.getServer().getWorld(World.NETHER) : ServerHelper.getServer().getWorld(World.OVERWORLD);

                    BlockPos safeSpawn = world.getSpawnPos();

                    int i = Math.max(0, ServerHelper.getServer().getSpawnRadius(world));
                    int j = MathHelper.floor(world.getWorldBorder().getDistanceInsideBorder(player.getX(), player.getZ()));

                    if (j < i) {
                        i = j;
                    }

                    if (j <= 1) {
                        i = 1;
                    }

                    long l = i * 2L + 1;
                    long m = l * l;
                    int k = m > 2147483647L ? Integer.MAX_VALUE : (int)m;
                    int n = k <= 16 ? k - 1 : 17;
                    int o = (new Random()).nextInt(k);

                    for (int p = 0; p < k; p++) {
                        int q = (o + n * p) % k;
                        int r = q % (i * 2 + 1);
                        int s = q / (i * 2 + 1);

                        safeSpawn = findSafeSpawn(world, player.getBlockX() + r - i, player.getBlockZ() + s - i);
                        if (safeSpawn != null) {
                            break;
                        }
                    }

                    player.teleport(world, safeSpawn.getX(), safeSpawn.getY(), safeSpawn.getZ(), player.getYaw(), player.getPitch());
                }
                case TNT -> {
                    for (int i = 0; i < 5; i++) {
                        TntEntity tnt = new TntEntity(EntityType.TNT, player.getEntityWorld());
                        tnt.setFuse(30);

                        ServerHelper.spawnEntity(tnt);
                    }
                }
                case GHAST -> {
                    GhastEntity ghast = new GhastEntity(EntityType.GHAST, player.getEntityWorld());
                    ghast.setTarget(player);

                    ghast.setCustomName(Text.literal(username));
                    ghast.setCustomNameVisible(true);

                    // Set the explosion power
                    NbtCompound explosionPower = new NbtCompound();
                    explosionPower.putByte("ExplosionPower", (byte) 15);

                    ghast.readCustomDataFromNbt(explosionPower);

                    ServerHelper.spawnEntity(ghast);
                }
                case SLIME -> {
                    SlimeEntity slime = new SlimeEntity(EntityType.SLIME, player.getEntityWorld());
                    slime.setSize(8, true);
                    slime.setTarget(player);

                    ServerHelper.spawnEntity(slime);
                }
                case DOUBLE_HEALTH -> {
                    EntityAttributeInstance maxHealth = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                    if (maxHealth.getBaseValue() >= 1280) {
                        // Reset player's health
                        maxHealth.setBaseValue(20);
                        player.setHealth(20);
                        player.sendMessage(Text.literal("You're too lucky! Set max health to 20"));
                    } else {
                        // Double player's max health
                        maxHealth.setBaseValue(maxHealth.getBaseValue() * 2);

                        // Double player's current health
                        player.setHealth(player.getHealth() * 2);
                        player.sendMessage(Text.literal("Set max health to " + maxHealth.getBaseValue()));

                        // Give temporary regen as a boost
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 255));
                    }
                }
                case CHEST_LOOT -> {
                    ChestMinecartEntity chestMinecart = new ChestMinecartEntity(EntityType.CHEST_MINECART, player.getEntityWorld());
                    chestMinecart.setCustomName(Text.literal(username));
                    chestMinecart.setCustomNameVisible(true);

                    String lootTable = (new Random()).nextBoolean() ? "chests/end_city_treasure" : "chests/ancient_city";

                    chestMinecart.setLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, new Identifier(lootTable)));

                    ServerHelper.spawnEntity(chestMinecart);
                }
                case CLEAR_INVENTORY -> {
                    ServerHelper.getConnectedPlayer().getInventory().clear();
                }
                case NOTHING -> {
                    player.sendMessage(Text.literal("You got lucky this time, but unlucky ")
                        .append(Text.literal(username).formatted(Formatting.AQUA)));
                }
            }
        }
    }

    private static ServerPlayerEntity player;
    private static String username;

    /**
     * Perform one of the following:
     * <ul>
     *     <li>Warden - 1%</li>
     *     <li>Clear Inventory - 1%</li>
     *     <li>Ravagers - 10%</li>
     *     <li>Teleport Nether - 10%</li>
     *     <li>Ghast - 10%</li>
     *     <li>Tnt - 10%</li>
     *     <li>DoubleHealth - 10% </li>
     *     <li>Slime - 15%</li>
     *     <li>Chest Loot - 15%</li>
     *     <li>Nothing - 19%</li>
     * </ul>
     */
    @Override
    public void trigger(JSONObject event)
    {
        username = event.getString("user_name");
        player = ServerHelper.getConnectedPlayer();

        AnnouncementHelper.playAnnouncement(username, "Is Feeling Extremely Lucky!");

        TwitchHelper.getRandomAction(ExtremeGambleAction.values()).execute();
    }

    /**
     * Find a safe spawn in the Nether to teleport to
     */
    private static BlockPos findSafeSpawn(ServerWorld world, int x, int z)
    {
        boolean bl = world.getDimension().hasCeiling();
        WorldChunk worldChunk = world.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z));
        int i = bl ? world.getChunkManager().getChunkGenerator().getSpawnHeight(world) : worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 15, z & 15);
        if (i < world.getBottomY()) {
            return null;
        } else {
            int j = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 15, z & 15);
            if (j <= i && j > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 15, z & 15)) {
                return null;
            } else {
                BlockPos.Mutable mutable = new BlockPos.Mutable();

                for(int k = i + 1; k >= world.getBottomY(); --k) {
                    mutable.set(x, k, z);
                    BlockState blockState = world.getBlockState(mutable);
                    if (!blockState.getFluidState().isEmpty()) {
                        break;
                    }

                    if (Block.isFaceFullSquare(blockState.getCollisionShape(world, mutable), Direction.UP)) {
                        return mutable.up().toImmutable();
                    }
                }

                return null;
            }
        }
    }
}
