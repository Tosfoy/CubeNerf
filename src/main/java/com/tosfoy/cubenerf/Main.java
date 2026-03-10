package com.tosfoy.cubenerf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Observer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Strider;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wither;
import org.bukkit.util.BoundingBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main
extends JavaPlugin
implements Listener,
CommandExecutor {
    private Set<Material> disabledBlocks = new HashSet<Material>();
    private Set<Material> disabledSlimeHoneyInteractions = new HashSet<Material>();
    private Set<Material> disabledDispenserItems = new HashSet<Material>();
    private Set<Material> disabledDispenserBlocks = new HashSet<Material>();
    private Set<Material> disabledObserverBlocks = new HashSet<Material>();
    private Set<Material> disabledCropsFromMinecart = new HashSet<Material>();
    private Set<Material> disabledCropsFromSaddle = new HashSet<Material>();
    private Set<Material> disabledCropsFromBoat = new HashSet<Material>();
    private boolean preventWitherSuffocation = false;
    private boolean preventDispenserWitherAggro = false;

    public void onEnable() {
        super.onEnable();
        this.saveDefaultConfig();
        this.loadDisabledBlocks();
        this.loadDisabledSlimeHoneyInteractions();
        this.loadDispenserConfigs();
        this.loadObserverConfigs();
        this.loadMinecartHarvestingConfig();
        this.loadSaddleHarvestingConfig();
        this.loadBoatHarvestingConfig();
        this.preventWitherSuffocation = this.getConfig().getBoolean("prevent-wither-suffocation", false);
        this.preventDispenserWitherAggro = this.getConfig().getBoolean("prevent-dispenser-wither-aggro", false);
        this.getCommand("cubenerf").setExecutor((CommandExecutor)this);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("cubenerf")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                this.reloadConfig();
                this.loadDisabledBlocks();
                this.loadDisabledSlimeHoneyInteractions();
                this.loadDispenserConfigs();
                this.loadObserverConfigs();
                this.loadMinecartHarvestingConfig();
                this.loadSaddleHarvestingConfig();
                this.loadBoatHarvestingConfig();
                this.preventWitherSuffocation = this.getConfig().getBoolean("prevent-wither-suffocation", false);
                this.preventDispenserWitherAggro = this.getConfig().getBoolean("prevent-dispenser-wither-aggro", false);
                sender.sendMessage(ChatColor.GREEN + "CubeNerf configuration reloaded.");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Usage: /cubenerf reload");
            return false;
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            completions.add("reload");
        }
        return completions;
    }

    private void loadBoatHarvestingConfig() {
        this.disabledCropsFromBoat.clear();
        if (this.getConfig().getBoolean("prevent-harvesting-from-boat")) {
            List<String> crops = this.getConfig().getStringList("disabled-crops-from-boat");
            for (String crop : crops) {
                Material mat = Material.matchMaterial(crop);
                if (mat == null) continue;
                this.disabledCropsFromBoat.add(mat);
            }
        }
    }

    private void loadSaddleHarvestingConfig() {
        this.disabledCropsFromSaddle.clear();
        if (this.getConfig().getBoolean("prevent-harvesting-from-saddle")) {
            List<String> crops = this.getConfig().getStringList("disabled-crops-from-saddle");
            for (String crop : crops) {
                Material mat = Material.matchMaterial(crop);
                if (mat == null) continue;
                this.disabledCropsFromSaddle.add(mat);
            }
        }
    }

    private void loadMinecartHarvestingConfig() {
        this.disabledCropsFromMinecart.clear();
        if (this.getConfig().getBoolean("prevent-harvesting-from-minecart")) {
            List<String> crops = this.getConfig().getStringList("disabled-crops-from-minecart");
            for (String crop : crops) {
                Material mat = Material.matchMaterial(crop);
                if (mat == null) continue;
                this.disabledCropsFromMinecart.add(mat);
            }
        }
    }

    private void loadDispenserConfigs() {
        this.disabledDispenserItems.clear();
        this.disabledDispenserBlocks.clear();
        if (this.getConfig().getBoolean("anti-dispenser-enable")) {
            List<String> items = this.getConfig().getStringList("disabled-dispenser-items");
            for (String item : items) {
                Material mat = Material.matchMaterial(item);
                if (mat == null) continue;
                this.disabledDispenserItems.add(mat);
            }
            List<String> blocks = this.getConfig().getStringList("disabled-dispenser-blocks");
            for (String block : blocks) {
                Material mat = Material.matchMaterial(block);
                if (mat == null) continue;
                this.disabledDispenserBlocks.add(mat);
            }
        }
    }

    private void loadObserverConfigs() {
        this.disabledObserverBlocks.clear();
        if (this.getConfig().getBoolean("anti-observer-enable")) {
            List<String> blocks = this.getConfig().getStringList("disabled-observer-blocks");
            for (String block : blocks) {
                Material mat = Material.matchMaterial(block);
                if (mat == null) continue;
                this.disabledObserverBlocks.add(mat);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (this.getConfig().getBoolean("prevent-harvesting-from-minecart") && this.disabledCropsFromMinecart.contains(block.getType()) && player.isInsideVehicle() && player.getVehicle() instanceof Minecart) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break crops while in a minecart!");
            return;
        }
        if (player.isInsideVehicle()) {
            Strider strider;
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof AbstractHorse) {
                AbstractHorse horse = (AbstractHorse)vehicle;
                if (horse.getInventory().getSaddle() != null) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot break crops while riding a saddled horse!");
                }
            } else if (vehicle instanceof Pig) {
                Pig pig = (Pig)vehicle;
                if (pig.hasSaddle()) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot break crops while riding a saddled pig!");
                }
            } else if (vehicle instanceof Strider && (strider = (Strider)vehicle).hasSaddle()) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot break crops while riding a saddled strider!");
            }
        }
        if (this.getConfig().getBoolean("prevent-harvesting-from-boat") && this.disabledCropsFromBoat.contains(block.getType()) && player.isInsideVehicle() && player.getVehicle() instanceof Boat) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break crops while in a boat!");
        }
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof Dispenser) {
            Dispenser dispenser = (Dispenser)block.getState();
            BlockFace facing = ((Directional)dispenser.getBlockData()).getFacing();
            Block affectedBlock = block.getRelative(facing);
            ItemStack dispensedItem = event.getItem();
            if (this.disabledDispenserItems.contains(dispensedItem.getType()) && this.disabledDispenserBlocks.contains(affectedBlock.getType())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRedstoneChange(BlockRedstoneEvent event) {
        Directional directional;
        BlockFace facing;
        Block observedBlock;
        Block block = event.getBlock();
        if (block.getType() == Material.OBSERVER && this.disabledObserverBlocks.contains((observedBlock = block.getRelative(facing = (directional = (Directional)block.getBlockData()).getFacing())).getType())) {
            event.setNewCurrent(event.getOldCurrent());
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        if (this.preventWitherSuffocation && wouldPushIntoWither(e.getBlocks(), e.getDirection())) {
            e.setCancelled(true);
            return;
        }
        this.handlePistonMovement(e.getBlocks(), e.getBlock(), e);
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e) {
        this.handlePistonMovement(e.getBlocks(), e.getBlock(), e);
    }

    @EventHandler
    public void onObserverUpdate(BlockRedstoneEvent event) {
        Observer observer;
        Block observedBlock;
        Block block = event.getBlock();
        if (block.getType() == Material.OBSERVER && this.disabledObserverBlocks.contains((observedBlock = block.getRelative((observer = (Observer)block.getBlockData()).getFacing())).getType()) && observer.isPowered()) {
            Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
                observer.setPowered(false);
                block.setBlockData((BlockData)observer);
            }, 1L);
            event.setNewCurrent(event.getOldCurrent());
        }
    }

    @EventHandler
    public void onVillagerHarvest(EntityChangeBlockEvent event) {
        Ageable ageable;
        if (!(event.getEntity() instanceof Villager)) {
            return;
        }
        if (event.getBlock().getBlockData() instanceof Ageable && (ageable = (Ageable)event.getBlock().getBlockData()).getAge() == ageable.getMaximumAge() && this.getConfig().getBoolean("prevent-villager-harvesting")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWitherDamageByDispenser(EntityDamageByEntityEvent event) {
        if (!this.preventDispenserWitherAggro) return;
        if (!(event.getEntity() instanceof Wither)) return;
        if (!(event.getDamager() instanceof Projectile)) return;
        Projectile projectile = (Projectile) event.getDamager();
        if (projectile.getShooter() instanceof BlockProjectileSource) {
            event.setCancelled(true);
        }
    }

    private void handlePistonMovement(List<Block> blocks, Block piston, Object event) {
        block7: {
            List<Material> movingMaterials;
            block4: {
                block6: {
                    boolean containsSlimeOrHoney;
                    block5: {
                        movingMaterials = blocks.stream().map(Block::getType).collect(Collectors.toList());
                        containsSlimeOrHoney = movingMaterials.contains(Material.SLIME_BLOCK) || movingMaterials.contains(Material.HONEY_BLOCK);
                        boolean allowMinecartInteractions = this.getConfig().getBoolean("allow_minecart_interactions");
                        if (allowMinecartInteractions && containsSlimeOrHoney) break block4;
                        if (!containsSlimeOrHoney || !this.getConfig().getBoolean("disable_slime_honey_interaction_by_block")) break block5;
                        if (movingMaterials.stream().anyMatch(this.disabledSlimeHoneyInteractions::contains)) break block6;
                    }
                    if (containsSlimeOrHoney || !this.getConfig().getBoolean("disable_piston_by_block")) break block7;
                    if (!movingMaterials.stream().anyMatch(this.disabledBlocks::contains)) break block7;
                }
                this.setEventCancelled(event);
                this.handlePistonBreaking(piston);
                break block7;
            }
            boolean minecartNearby = this.checkForMinecartNearby(piston.getLocation());
            if (!minecartNearby) {
                if (movingMaterials.stream().anyMatch(this.disabledSlimeHoneyInteractions::contains)) {
                    this.setEventCancelled(event);
                    this.handlePistonBreaking(piston);
                }
            }
        }
    }

    private boolean checkForMinecartNearby(Location location) {
        double radius = 2.0;
        List<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, radius, radius, radius).stream().filter(e -> e instanceof Minecart).collect(Collectors.toList());
        return !nearbyEntities.isEmpty();
    }

    private boolean wouldPushIntoWither(List<Block> blocks, BlockFace direction) {
        for (Block block : blocks) {
            Block destination = block.getRelative(direction);
            BoundingBox blockBox = BoundingBox.of(destination);
            Location destLoc = destination.getLocation();
            for (Entity entity : destLoc.getWorld().getNearbyEntities(destLoc, 2, 4, 2)) {
                if (entity instanceof Wither) {
                    if (entity.getBoundingBox().overlaps(blockBox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setEventCancelled(Object event) {
        if (event instanceof BlockPistonExtendEvent) {
            ((BlockPistonExtendEvent)event).setCancelled(true);
        } else if (event instanceof BlockPistonRetractEvent) {
            ((BlockPistonRetractEvent)event).setCancelled(true);
        }
    }

    private void handlePistonBreaking(Block piston) {
        if (this.getConfig().getBoolean("break_piston_on_disable")) {
            Material pistonMaterial = piston.getType();
            piston.setType(Material.AIR);
            if (this.getConfig().getBoolean("drop_piston_on_break")) {
                piston.getWorld().dropItemNaturally(piston.getLocation(), new ItemStack(pistonMaterial, 1));
            }
        }
    }

    public void loadDisabledBlocks() {
        this.disabledBlocks.clear();
        List<String> blockNames = this.getConfig().getStringList("disabled_blocks");
        for (String name : blockNames) {
            Material material = Material.matchMaterial(name);
            if (material == null) continue;
            this.disabledBlocks.add(material);
        }
    }

    public void loadDisabledSlimeHoneyInteractions() {
        this.disabledSlimeHoneyInteractions.clear();
        List<String> blockNames = this.getConfig().getStringList("disabled_slime_honey_interactions");
        for (String name : blockNames) {
            Material material = Material.matchMaterial(name);
            if (material == null) continue;
            this.disabledSlimeHoneyInteractions.add(material);
        }
    }
}

