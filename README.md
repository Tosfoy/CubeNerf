# CubeNerf

CubeNerf is a farm-nerfing plugin designed to reduce the strength of AFK and redstone-based farms without fully breaking normal gameplay.

It allows servers to selectively block or nerf automation methods such as:

- Pistons pushing or breaking crops
- Flying machines harvesting crops
- Slime and honey block crop interactions
- Dispensers using water to move mobs
- Dispensers shearing animals automatically
- Villager crop harvesting
- Minecart / saddle / boat harvesting exploits
- Observer-based farm automation for selected blocks

The goal of CubeNerf is to keep useful redstone contraptions working where possible, while stopping fully automatic crop, mob, and item farms from becoming too strong.

## Features

### 1. Piston Crop Protection
CubeNerf can stop pistons from interacting with selected farm blocks such as sugar cane, bamboo, melons, and pumpkins.

This can be used to:
- Prevent pistons from breaking crops
- Stop flying machines from harvesting crops
- Nerf large-scale AFK crop farms

You can also choose whether pistons should:
- Simply fail to work, or
- Break when they attempt a disabled interaction

### 2. Slime / Honey Interaction Control
CubeNerf can block slime blocks and honey blocks from moving selected farm blocks.

This is especially useful for nerfing:
- Flying machines
- Auto-harvest crop sweepers
- Slime/honey-based farm pushers

Minecart exceptions can also be configured separately.

### 3. Dispenser Automation Nerfs
CubeNerf can block dispensers from using selected items on selected blocks or mobs.

Examples:
- Prevent water buckets from pushing mobs
- Prevent bonemeal automation on crops
- Prevent shears from automatically shearing animals or beehives
- Stop dispenser-based farm automation from replacing manual gameplay

### 4. Observer Farm Nerfs
CubeNerf can stop certain blocks from triggering observers.

This helps reduce or disable:
- Observer-powered sugar cane farms
- Observer-powered bamboo farms
- Fully automated redstone harvesting loops

### 5. Villager Harvesting Control
Villager crop harvesting can be disabled to reduce large villager breeder/farm combinations and AFK villager crop automation.

### 6. Anti-Exploit Protections
CubeNerf also includes protections against certain automation or abuse cases, such as:
- Wither suffocation using pistons
- Minecart crop breaking
- Saddle-based crop harvesting
- Boat harvesting exploits

## Commands

- `/cn reload` — Reloads the CubeNerf configuration

## Permissions

- `cubenerf.reload` — Allows use of `/cn reload`

## Example Use Cases

CubeNerf can be configured to do things like:

- Allow pistons for normal redstone, but prevent them from breaking sugar cane
- Let flying machines move, but stop them from harvesting crops
- Disable dispensers from pushing water onto mobs
- Disable dispenser shearing on sheep or beehives
- Prevent villagers from auto-harvesting carrots, potatoes, or wheat
- Block observers from powering bamboo and sugar cane farm loops

## Configuration Notes

After editing the config, run:

`/cn reload`

Material names should use Bukkit/Spigot material formatting.

Example materials:
- `SUGAR_CANE`
- `BAMBOO`
- `PUMPKIN`
- `MELON`
- `WHEAT`
- `CARROTS`
- `POTATOES`

## Purpose

CubeNerf is meant for servers that want to preserve survival gameplay and the usefulness of redstone, while reducing the efficiency of fully AFK farm setups.

Instead of disabling automation entirely, CubeNerf gives fine control over what should and should not be automated.