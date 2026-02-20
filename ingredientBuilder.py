import json
import os

DATA_FOLDER = "CraftingData"

WEAPON_CATEGORIES = ["1H", "2H", "Rapier", "Dagger", "Melee"]
ARMOR_CATEGORIES = ["Armor", "Lower_Headwear", "Upper_Headwear", "Shields"]

CATEGORIES = [
    "1H",
    "2H",
    "Rapier",
    "Dagger",
    "Melee",
    "Shields",
    "Ores",
    "Crafted_Materials",
    "Armor",
    "Lower_Headwear",
    "Upper_Headwear"
]

def ensure_data_folder():
    if not os.path.exists(DATA_FOLDER):
        os.makedirs(DATA_FOLDER)

def load_existing_data(filename):
    if os.path.exists(filename):
        with open(filename, "r") as f:
            return json.load(f)
    return []

def save_data(filename, data):
    with open(filename, "w") as f:
        json.dump(data, f, indent=4)

def select_category():
    print("\nSelect Category:")
    for i, cat in enumerate(CATEGORIES, 1):
        print(f"{i}. {cat}")

    while True:
        try:
            choice = int(input("\nEnter number: "))
            if 1 <= choice <= len(CATEGORIES):
                return CATEGORIES[choice - 1]
            else:
                print("Invalid number.")
        except ValueError:
            print("Enter a valid number.")

def get_int_input(prompt):
    while True:
        try:
            return int(input(prompt))
        except ValueError:
            print("Enter a valid number.")

def create_item():
    ensure_data_folder()

    category = select_category()
    filename = os.path.join(DATA_FOLDER, f"{category}.json")

    item = {}
    item["name"] = input("\nItem Name: ").strip()

    # Required smithing level (ALL items)
    item["required_smithing_level"] = get_int_input("Required Smithing Level: ")

    # Weapon stat
    if category in WEAPON_CATEGORIES:
        item["SK"] = get_int_input("Enter SK stat: ")

    # Armor stat
    elif category in ARMOR_CATEGORIES:
        item["level"] = get_int_input("Enter Level requirement: ")

    # Ingredients
    ingredients = []
    print("\nEnter ingredients (leave blank to finish)\n")

    while True:
        name = input("Ingredient Name: ").strip()
        if name == "":
            break

        count = input("Count: ").strip()

        ingredients.append({
            "name": name,
            "count": count
        })
        print()

    item["ingredients"] = ingredients

    data = load_existing_data(filename)
    data.append(item)
    save_data(filename, data)

    print(f"\n✅ Saved to {filename}")

def main():
    while True:
        create_item()
        again = input("\nAdd another item? (y/n): ").lower()
        if again != "y":
            break

if __name__ == "__main__":
    main()
