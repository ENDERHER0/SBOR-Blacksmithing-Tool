# SBOR Blacksmithing Tool

A lightweight desktop tool for players of Sword Blox Online on Roblox.

This program helps manage crafting recipes and track progress using simple external JSON files.

---

## Features

- Loads crafting recipes from a `CraftingData` folder
- Displays items in a clean desktop interface
- Tracks crafting counters
- Automatically saves progress locally
- Works completely offline

---

## File Structure

```
SBOR-Blacksmithing-Tool/
 ├─ SBOR-Blacksmithing-Tool.exe
 ├─ CraftingData/
 │    ├─ item1.json
 │    ├─ item2.json
 │    └─ ...
```

The `CraftingData` folder must remain next to the executable.

All recipe `.json` files go inside that folder.

---

## How to Use

1. Place your JSON files inside the `CraftingData` folder
2. Run `SBOR-Blacksmithing-Tool.exe`
3. Browse items and track progress

The program automatically loads all `.json` files in `CraftingData`.

Counter data is saved locally and restored on the next launch.

No installation required.

---

## Data Saving

- Counter data is stored locally on the user's machine
- Progress persists between sessions
- No internet connection required

---

## For Developers

- Built with Java and JavaFX
- Uses external JSON for recipe definitions
- Designed for easy modification and extension

To build:
- Compile the project
- Build the jar artifact
- Optionally wrap with Launch4j for Windows

---
