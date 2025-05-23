package uk.co.evanward.twitchinteractions.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

import java.util.*;
import java.util.stream.Stream;

public class ServerHelper
{
    /**
     * Get the player connected to Twitch
     */
    public static ServerPlayerEntity getConnectedPlayer()
    {
        return getServer().getPlayerManager().getPlayer(TwitchInteractions.socketClient.getPlayerId());
    }

    public static List<ServerPlayerEntity> getPlayers()
    {
        List<ServerPlayerEntity> players = new ArrayList<>();
        getServer().getWorlds().forEach(serverWorld -> players.addAll(serverWorld.getPlayers()));

        return players;
    }

    /**
     * Get the minecraft server
     */
    public static IntegratedServer getServer()
    {
        return MinecraftClient.getInstance().getServer();
    }

    /**
     * Give a player the given item
     */
    public static void giveItem(ItemStack item, ServerPlayerEntity player)
    {
        // Attempt to insert egg into player's inventory
        boolean inserted = player.getInventory().insertStack(item);

        // Drop the egg on the floor if the player's inventory is full
        ItemEntity itemEntity;
        if (!inserted) {
            itemEntity = player.dropItem(item, false);
            itemEntity.resetPickupDelay();
            itemEntity.setOwner(player.getUuid());
        }

        // Play item pickup sound
        player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
        player.currentScreenHandler.sendContentUpdates();
    }

    /**
     * Give an item to the connected player
     */
    public static void giveItem(ItemStack item)
    {
        giveItem(item, getConnectedPlayer());
    }

    /**
     * Summon an entity on the given player
     */
    public static void spawnEntity(Entity entity, ServerPlayerEntity player)
    {
        Random random = new Random();

        // Get a random position within a 10 block square of the player that is safe for the mob to spawn on
        int maxAttempts = 1000;
        int attempts = 0;

        boolean safe;
        do {
            // Set the position to the centre of the block
            double x = (random.nextBoolean() ? player.getBlockX() + random.nextInt(10) : player.getBlockX() - random.nextInt(10)) + 0.5;
            double z = (random.nextBoolean() ? player.getBlockZ() + random.nextInt(10) : player.getBlockZ() - random.nextInt(10)) + 0.5;
            int y = player.getBlockY();

            Vec3d pos = new Vec3d(x, y, z);
            entity.setPosition(pos);

            boolean collides = entity.collidesWithStateAtPos(entity.getBlockPos(), player.getServerWorld().getBlockState(entity.getBlockPos()));
            boolean isAir = player.getServerWorld().getBlockState(entity.getBlockPos().down()).isAir();

            safe = !collides && !isAir;

            attempts++;
        } while (
            !safe && attempts < maxAttempts
        );

        if (attempts == maxAttempts) {
            throw new RuntimeException("Could not find safe spawn");
        }

        player.getServerWorld().spawnEntityAndPassengers(entity);
    }

    /**
     * Summon an entity on the connected player
     */
    public static void spawnEntity(Entity entity)
    {
        spawnEntity(entity, getConnectedPlayer());
    }

    /**
     * Get a random living mob type to replace
     */
    public static EntityType<?> randomMobType()
    {
        return Stream.generate(() -> Registries.ENTITY_TYPE.getRandom(getConnectedPlayer().getRandom()))
            .flatMap(Optional::stream)
            .filter(entityTypeReference -> EnumSet.of(SpawnGroup.CREATURE, SpawnGroup.MONSTER, SpawnGroup.AXOLOTLS, SpawnGroup.AMBIENT)
                .contains(entityTypeReference.value().getSpawnGroup()))

            // Mobs that shouldn't be replaced
            .filter(entityTypeReference -> !entityTypeReference.matchesId(Identifier.tryParse("wandering_trader")))
            .filter(entityTypeReference -> !entityTypeReference.matchesId(Identifier.tryParse("bat")))
            .filter(entityTypeReference -> !entityTypeReference.matchesId(Identifier.tryParse("giant")))
            .filter(entityTypeReference -> !entityTypeReference.matchesId(Identifier.tryParse("zombie_horse")))
            .filter(entityTypeReference -> !entityTypeReference.matchesId(Identifier.tryParse("illusioner")))

            .findAny()
            .get()
            .value();
    }

    /**
     * Get a random living mob type replacement
     */
    public static EntityType<?> randomMobTypeReplacement()
    {
        return Stream.generate(() -> Registries.ENTITY_TYPE.getRandom(getConnectedPlayer().getRandom()))
            .flatMap(Optional::stream)
            .filter(entityTypeReference -> EnumSet.of(SpawnGroup.CREATURE, SpawnGroup.MONSTER, SpawnGroup.AXOLOTLS, SpawnGroup.AMBIENT, SpawnGroup.WATER_CREATURE, SpawnGroup.WATER_AMBIENT)
                .contains(entityTypeReference.value().getSpawnGroup()))
            .findAny()
            .get()
            .value();
    }

    /**
     * Replace the given entity
     */
    public static Entity getEntityReplacement(Entity entity)
    {
        Entity replacement = Registries.ENTITY_TYPE.get(Identifier.tryParse(
            TwitchInteractions.worldChanges.REPLACE_MOB_SPAWN.getString(entity.getType().toString())
        )).create(entity.getEntityWorld(), SpawnReason.COMMAND);

        replacement.setPosition(entity.getPos());

        if (replacement.getType() == EntityType.VEX) {
            VexEntity vex = (VexEntity) replacement;
            vex.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        }

        return replacement;
    }
}
