package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.helpers.AnnouncementHelper;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.helpers.TwitchHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

import java.util.Random;

public class GambleRedemption implements ChannelPoint.ChannelPointInterface
{
    private enum GambleActions implements Action
    {
        CHARGED_CREEPERS(10), WATER_BUCKET_CHALLENGE(10), HALF_HEALTH(10), ANGRY_MOB(10),
        FOOD_EXPLOSION(10), NOTHING(50);

        private final int weight;

        GambleActions(int weight)
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
            switch(this) {
                case WATER_BUCKET_CHALLENGE -> {
                    // Teleport the player to the world limit of the dimension they're in
                    player.teleport(player.getX(), player.getEntityWorld().getHeight(), player.getZ());

                    // get the item in the player's main hand
                    ItemStack itemInHand = player.getStackInHand(Hand.MAIN_HAND);

                    if (!itemInHand.isEmpty()) {
                        // Get an available slot in the player's current inventory (hotbar/main)
                        int slot = player.getInventory().getEmptySlot();

                        ItemStack waterBucket = new ItemStack(Items.WATER_BUCKET);
                        if (slot != -1) {
                            // Move the item in the player's hand to another available slot
                            player.getInventory().insertStack(slot, itemInHand);
                            player.getInventory().removeStack(player.getInventory().selectedSlot);

                            // Place the water bucket in the player's main hand
                            player.setStackInHand(Hand.MAIN_HAND, waterBucket);
                        } else {
                            // Drop the water bucket
                            player.dropItem(waterBucket, false);
                        }
                    } else {
                        // Put a water bucket in the player's main hand if it isn't empty
                        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET));
                    }
                }
                case ANGRY_MOB -> {
                    enum AngryMob
                    {
                        BEE, WOLF, ZOMBIE_PIGLIN, IRON_GOLEM;

                        public Entity getEntity()
                        {
                            Angerable angryEntity;
                            switch(this) {
                                case BEE -> {
                                    angryEntity = new BeeEntity(EntityType.BEE, player.getEntityWorld());
                                }
                                case WOLF -> {
                                    angryEntity = new WolfEntity(EntityType.WOLF, player.getEntityWorld());
                                }
                                case ZOMBIE_PIGLIN -> {
                                    angryEntity = new ZombifiedPiglinEntity(EntityType.ZOMBIFIED_PIGLIN, player.getEntityWorld());
                                }
                                case IRON_GOLEM -> {
                                    angryEntity = new IronGolemEntity(EntityType.IRON_GOLEM, player.getEntityWorld());
                                }
                                default -> throw new IllegalArgumentException("Unsupported AngryMob enum `" + this + "`");
                            }

                            angryEntity.setTarget(player);
                            angryEntity.setAngerTime(60000);
                            angryEntity.setAngryAt(player.getUuid());

                            return (Entity) angryEntity;
                        }
                    }

                    AngryMob angryEntity = AngryMob.values()[(new Random()).nextInt(AngryMob.values().length)];
                    for (int i = 0; i < 15; i++) {
                        Entity entity = angryEntity.getEntity();

                        entity.setCustomName(Text.literal(username));
                        entity.setCustomNameVisible(true);

                        // Set a random variant for Wolf
                        if (angryEntity == AngryMob.WOLF) {
                            WolfEntity wolf = (WolfEntity) entity;

                            RegistryKey<WolfVariant> variantKey = player.getEntityWorld().getRegistryManager()
                                .get(RegistryKeys.WOLF_VARIANT)
                                .getRandom(player.getRandom())
                                .get()
                                .registryKey();

                            // Set the wolf to this variant
                            player.getRegistryManager()
                                .get(RegistryKeys.WOLF_VARIANT)
                                .getEntry(variantKey)
                                .ifPresent(wolf::setVariant);

                        }

                        // Spawn a new entity
                        ServerHelper.spawnEntity(entity);

                        // Only spawn one Iron Golem
                        if (angryEntity == AngryMob.IRON_GOLEM) {
                            break;
                        }
                    }
                }
                case HALF_HEALTH -> {
                    player.setHealth(10);
                    player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10);
                }
                case FOOD_EXPLOSION -> {
                    for (int i = 0; i < 5; i++) {
                        PigEntity pig = new PigEntity(EntityType.PIG, player.getEntityWorld());
                        setAttributes(pig);
                        ServerHelper.spawnEntity(pig);

                        CowEntity cow = new CowEntity(EntityType.COW, player.getEntityWorld());
                        setAttributes(cow);
                        ServerHelper.spawnEntity(cow);

                        ChickenEntity chicken = new ChickenEntity(EntityType.CHICKEN, player.getEntityWorld());
                        setAttributes(chicken);
                        ServerHelper.spawnEntity(chicken);

                        SheepEntity sheep = new SheepEntity(EntityType.SHEEP, player.getEntityWorld());
                        setAttributes(sheep);
                        ServerHelper.spawnEntity(sheep);

                        // Summon rockets on each of the entities
                        summonRocket(pig);
                        summonRocket(cow);
                        summonRocket(chicken);
                        summonRocket(sheep);
                    }
                }
                case CHARGED_CREEPERS -> {
                    CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, player.getEntityWorld());
                    creeper.setTarget(player);

                    // Set the Creeper to charged if Creepers are now charged
                    if (!chargedCreepers) {
                        NbtCompound powered = new NbtCompound();
                        powered.putBoolean("powered", true);

                        creeper.readCustomDataFromNbt(powered);
                    }

                    ServerHelper.spawnEntity(creeper);

                    // Set spawning Creepers to charged
                    chargedCreepers = !chargedCreepers;
                }
                case NOTHING -> {
                    player.sendMessage(Text.literal("You got lucky this time, but unlucky ")
                        .append(Text.literal(username).formatted(Formatting.AQUA)));
                }
            }
        }
    }

    // TODO: Save the last state of this
    public static boolean chargedCreepers;

    private static String username;
    private static ServerPlayerEntity player;

    /**
     * Perform one of the following:
     * <ul>
     *     <li>Charged Creepers - 10%</li>
     *     <li>Water Bucket Challenge - 10%</li>
     *     <li>Half Health - 10%</li>
     *     <li>Angry Mob - 10%</li>
     *     <li>Food Explosion - 10%</li>
     *     <li>Nothing - 50%</li>
     * </ul>
     */
    @Override
    public void trigger(JSONObject event)
    {
        username = event.getString("user_name");
        player = ServerHelper.getConnectedPlayer();

        AnnouncementHelper.playAnnouncement(username, "Is Feeling Lucky!");

        TwitchHelper.getRandomAction(GambleActions.values()).execute();
    }

    /**
     * Set the given passive entity's health low, and on fire, with levitation
     */
    private static void setAttributes(PassiveEntity entity)
    {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20, 40));
        entity.setHealth(3);
        entity.setOnFireFor(3);
    }

    /**
     * Summon an explosive rocket on the given passive entity
     */
    private static void summonRocket(PassiveEntity entity)
    {
        NbtList explosions = new NbtList();

        // Set the explosions colours
        NbtCompound explosion = new NbtCompound();
        explosion.putByte("Type", (byte) 3);
        explosion.putByte("Flicker", (byte) 1);
        explosion.putIntArray("Colors", new int[] {12743679, 9320703, 16735854});
        explosion.putIntArray("FadeColors", new int[] {16739979, 16774930});

        explosions.add(explosion);

        NbtCompound firework = new NbtCompound();
        firework.putInt("Flight", 2);
        firework.put("Explosions", explosions);

        // Create the firework item
        ItemStack fireworkItem = new ItemStack(Items.FIREWORK_ROCKET);
        NbtCompound fireworkItemTag = new NbtCompound();
        fireworkItemTag.put("Fireworks", firework);

        NbtCompound fireworkEntityTag = new NbtCompound();
        fireworkEntityTag.putInt("Lifetime", 0);
        fireworkEntityTag.putInt("Life", 0);

        // Create the firework entity and apply nbt
        FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(entity.getEntityWorld(), entity.getX(), entity.getY(), entity.getZ(), fireworkItem);
        fireworkRocket.writeCustomDataToNbt(fireworkEntityTag);

        entity.getEntityWorld().spawnEntity(fireworkRocket);
    }
}
