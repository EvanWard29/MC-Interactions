package uk.co.evanward.twitchinteractions.twitch.event.channelpoints.redemptions;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.helpers.ServerHelper;
import uk.co.evanward.twitchinteractions.twitch.event.channelpoints.ChannelPoint;

import java.util.Random;

public class FriendCreatorRedemption implements ChannelPoint.ChannelPointInterface
{
    private enum Friend
    {
        AXOLOTL, BEE, FOX, FROG, DONKEY, PARROT, RABBIT, LLAMA, MOOSHROOM, TRADER_LLAMA, CAMEL, TURTLE, SNIFFER, ARMADILLO;

        /**
         * Get the entity to summon
         */
        public PassiveEntity getFriend()
        {
            Random random = new Random();
            PassiveEntity entity;

            switch (this) {
                case AXOLOTL -> {
                    // Give an Axolotl in a bucket to avoid suffocation
                    AxolotlEntity axolotl = new AxolotlEntity(EntityType.AXOLOTL, player.getEntityWorld());

                    AxolotlEntity.Variant variant;

                    // 5% chance the variant will be Blue
                    if (random.nextInt(100) <= 5) {
                        variant = AxolotlEntity.Variant.BLUE;
                    } else {
                        variant = AxolotlEntity.Variant.getRandomNatural(net.minecraft.util.math.random.Random.create(random.nextLong()));
                    }

                    axolotl.setVariant(variant);

                    entity = axolotl;
                }
                case BEE -> {
                    entity = new BeeEntity(EntityType.BEE, player.getEntityWorld());
                }
                case FROG -> {
                    entity = new FrogEntity(EntityType.FROG, player.getEntityWorld());
                }
                case FOX -> {
                    FoxEntity fox = new FoxEntity(EntityType.FOX, player.getEntityWorld());

                    // 10% chance to be Snow Fox
                    if (random.nextInt(100) <= 10) {
                        fox.setVariant(FoxEntity.Type.SNOW);
                    }

                    entity = fox;
                }
                case DONKEY -> {
                    DonkeyEntity donkey = new DonkeyEntity(EntityType.DONKEY, player.getEntityWorld());
                    donkey.setOwnerUuid(player.getUuid());

                    entity = donkey;
                }
                case CAMEL -> {
                    CamelEntity camel = new CamelEntity(EntityType.CAMEL, player.getEntityWorld());
                    camel.setOwnerUuid(player.getUuid());

                    entity = camel;
                }
                case MOOSHROOM -> {
                    MooshroomEntity mooshroom = new MooshroomEntity(EntityType.MOOSHROOM, player.getEntityWorld());

                    MooshroomEntity.Type variant;

                    // 5% chance the mooshroom is brown
                    if (random.nextInt(100) <= 5) {
                        variant = MooshroomEntity.Type.BROWN;
                    } else {
                        variant = MooshroomEntity.Type.RED;
                    }

                    mooshroom.setVariant(variant);

                    entity = mooshroom;
                }
                case LLAMA -> {
                    LlamaEntity llama = new LlamaEntity(EntityType.LLAMA, player.getEntityWorld());

                    // Get a random carpet colour
                    DyeColor carpetColour = DyeColor.byId((new Random()).nextInt(DyeColor.values().length));

                    // Set the carpet colour using nbt
                    NbtCompound decorItem = new NbtCompound();
                    decorItem.putString("id", "minecraft:" + carpetColour.asString() + "_carpet");
                    decorItem.putInt("Count", 1);

                    NbtCompound nbt = new NbtCompound();
                    nbt.put("DecorItem", decorItem);

                    llama.readCustomDataFromNbt(nbt);

                    llama.setVariant(LlamaEntity.Variant.byId(random.nextInt(LlamaEntity.Variant.values().length)));
                    llama.setOwnerUuid(player.getUuid());

                    entity = llama;
                }
                case TRADER_LLAMA -> {
                    TraderLlamaEntity traderLlama = new TraderLlamaEntity(EntityType.TRADER_LLAMA, player.getEntityWorld());
                    traderLlama.setVariant(LlamaEntity.Variant.byId(random.nextInt(LlamaEntity.Variant.values().length)));
                    traderLlama.setOwnerUuid(player.getUuid());

                    entity = traderLlama;
                }
                case PARROT -> {
                    ParrotEntity parrot = new ParrotEntity(EntityType.PARROT, player.getEntityWorld());
                    parrot.setVariant(ParrotEntity.Variant.byIndex(random.nextInt(ParrotEntity.Variant.values().length)));
                    parrot.setOwner(player);

                    entity = parrot;
                }
                case RABBIT -> {
                    RabbitEntity rabbit = new RabbitEntity(EntityType.RABBIT, player.getEntityWorld());
                    rabbit.setVariant(RabbitEntity.RabbitType.byId(random.nextInt(RabbitEntity.RabbitType.values().length)));

                    entity = rabbit;
                }
                case TURTLE -> {
                    entity = new TurtleEntity(EntityType.TURTLE, player.getEntityWorld());
                }
                case SNIFFER -> {
                    entity = new SnifferEntity(EntityType.SNIFFER, player.getEntityWorld());
                }
                case ARMADILLO -> {
                    entity = new ArmadilloEntity(EntityType.ARMADILLO, player.getEntityWorld());
                }
                default -> throw new IllegalArgumentException("Unsupported Friend enum `" + this + "`");
            }

            entity.setCustomName(Text.literal(username));
            entity.setCustomNameVisible(true);

            return entity;
        }
    }

    private static String username;
    private static final ServerPlayerEntity player = ServerHelper.getConnectedPlayer();

    /**
     * Summon a random friendly mob
     */
    @Override
    public void trigger(JSONObject event)
    {
        username = event.getString("user_name");

        Friend friend = Friend.values()[(new Random()).nextInt(Friend.values().length)];
        PassiveEntity entity = friend.getFriend();

        if (friend == Friend.AXOLOTL) {
            // Give Axolotls as a bucket to avoid immediately suffocating
            AxolotlEntity axolotl = (AxolotlEntity) entity;

            // Give Axolotls as a spawn egg to avoid immediately suffocating
            SpawnEggItem axolotlEgg = SpawnEggItem.forEntity(EntityType.AXOLOTL);
            ItemStack axolotlEggItem = axolotlEgg.getDefaultStack();

            // Set the egg's name
            axolotlEggItem.set(DataComponentTypes.CUSTOM_NAME, Text.literal(username));

            // Set the entity's name
            NbtComponent.set(DataComponentTypes.ENTITY_DATA, axolotlEggItem, nbt -> {
                nbt.putString("id", "minecraft:axolotl");
                nbt.putString("CustomName", username);
                nbt.putBoolean("CustomNameVisible", true);
                nbt.putInt("Variant", axolotl.getVariant().getId());
                nbt.putBoolean("FromBucket", true);
            });

            ServerHelper.giveItem(axolotlEggItem);
        } else {
            // Summon the friend
            ServerHelper.spawnEntity(entity);
        }
    }
}
