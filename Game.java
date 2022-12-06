/* ***************************************
  @author    Asad Ali Khan
  @SID       220257466
  @version   1

    Explore the never ending castle - game
   ****************************************/

import java.util.Random;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

class Game {

    public static void main(String[] args) {
        mainMenu();
    }

    public static void printString(String string) {
        // prints the string that is passed into it
        System.out.println(string);
    }

    public static void pressEnterToContinue() {
        // waits for the user to enter a new line to continue to the next action
        printString("Press Enter To Continue");
        Scanner scanner = new Scanner(System.in);
        try {
            System.in.read();
            scanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearScreen() {
        // flushes the screen of its text to make the game easier to read
        printString("\033[H\033[2J");
    }

    public static void startNewGame() {
        // where the main game loop takes place
        printString("Welcome to booby trap castle");
        String name = askPlayerName();
        GameInfo gameInfo = createNewGameInfo(10, 2, 1, 0);
        Player player = createNewPlayer(name, 15, 3, 1);
        String fileName = askFileName();
        printString("Hello  " + player.name + "!");
        printString("Get through as many rooms as possible!");
        printString("");

        loadGame(gameInfo, player, fileName);
    }

    public static void loadGame(GameInfo gameInfo, Player player, String fileName) {
        while (gameInfo.gameover == false) {
            saveGame(gameInfo, player, fileName);
            displayHud(gameInfo, player);

            if (itemDropped()) {
                droppedItemSequence(player, gameInfo);
            }

            Room currentRoom = createNewRoom();

            if (currentRoom.isTrap) {
                trappedRoomSequence(player);
                if (player.health <= 0) {
                    gameOverMessage("You died to the trap!");
                    gameInfo.gameover = true;
                }
            }

            boolean fightWon = fightSequence(currentRoom, player, gameInfo);

            if (fightWon & !(gameInfo.gameover)) {
                increaseGameDifficulty(gameInfo);
                increaseBaseStats(player);
                pressEnterToContinue();
                clearScreen();
            } else if (!fightWon & !(gameInfo.gameover)) {
                gameOverMessage("You died in the fight!");
                gameInfo.gameover = true;
            }

        }
        printString("Your final score: " + gameInfo.rooms);
        saveGame(gameInfo, player, fileName);
    }

    public static String askFileName() {
        // asks the suer to enter a file and returns the filename
        String fileName;
        Scanner userinput = new Scanner(System.in);

        printString("Save file name: ");
        fileName = userinput.nextLine();

        return fileName;

    }

    public static String askPlayerName() {
        // takes a user input for the players name and returns it
        String name;
        Scanner userinput = new Scanner(System.in);

        printString("What is your name: ");
        name = userinput.nextLine();

        return name;

    }

    public static GameInfo createNewGameInfo(int maximumHealth, int maximumDamage, int maximumDefence, int rooms) {
        // creates a new gameInfo variable
        GameInfo gameInfo = new GameInfo();

        gameInfo.gameover = false;

        gameInfo.rooms = rooms;
        gameInfo.maximumHealth = maximumHealth;
        gameInfo.maximumDamage = maximumDamage;
        gameInfo.maximumDefence = maximumDefence;

        return gameInfo;
    }

    public static void increaseGameDifficulty(GameInfo gameInfo) {
        // takes the gameInfo and increases its difficulty and increments the room
        gameInfo.rooms += 1;
        gameInfo.maximumHealth += 2;
        if (getRandomNum(2) == 0) {
            gameInfo.maximumDamage += 1;
        } else {
            gameInfo.maximumDefence += 1;
        }

    }

    public static void increaseEnemyDifficulty(Enemy enemy) {
        // takes the enemy and increases its difficulty
        printString(enemy.name + " gets stronger!");
        enemy.health += getRandomNum((enemy.health / 5) + 2);
        enemy.damage += getRandomNum((enemy.damage / 5) + 2);
        enemy.defence += getRandomNum((enemy.defence / 5) + 2);
    }

    public static void increaseBaseStats(Player player) {
        // rolls a 2 sided dice to determine what type of item was dropped
        int numberRolled = getRandomNum(2);
        printString("--------~~~------");
        if (numberRolled == 0) {
            printString("Your damage increased");
            player.damage += 1;
        } else {
            printString("Your defence increased");
            player.defence += 1;
        }
        printString("--------~~~------");
    }

    public static Room createNewRoom() {
        // this creates a new room with the number of enemies specified
        Room room = new Room();
        room.enemies = getRandomNum(2) + 1;
        room.isTrap = determineIfTrappedRoom();
        return room;
    }

    public static Boolean determineIfTrappedRoom() {
        // this rolls a 5 sided dice, if the rolled number is 0 then the next room is a
        // trap
        int numberRolled = getRandomNum(5);

        if (numberRolled == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean determineIfKeyWorks(Key key) {
        // compares the success rate with a random number between 0-100, if its more
        // then the key works
        int numberRolled = getRandomNum(99) + 1;

        if (numberRolled <= key.disarmChance) {
            return true;
        } else {
            return false;
        }
    }

    public static Player createNewPlayer(String name, int health, int damage, int defence) {
        // Creates a new player
        Player player = new Player();
        player.name = name;
        player.health = health;
        player.damage = damage;
        player.defence = defence;

        return player;
    }

    public static Enemy createNewEnemy(GameInfo gameInfo) {
        // this creates a new enemy
        Enemy enemy = new Enemy();

        String[] names = {
                "Slime",
                "Zombie",
                "Skeleton",
                "Ogre",
                "Troll",
                "Goblin",
                "Witch",
        };

        enemy.name = names[getRandomNum(names.length)];
        enemy.health = getRandomNum(gameInfo.maximumHealth) + 1;
        enemy.damage = getRandomNum(gameInfo.maximumDamage) + 1;
        enemy.defence = getRandomNum(gameInfo.maximumDefence);

        return enemy;
    }

    public static Sword createNewSword(GameInfo gameInfo) {
        // this creates a new sword
        Sword sword = new Sword();

        String[] names = {
                "Long Sword",
                "Dagger",
                "Short Sword",
                "Mace",
                "Katana",
                "Battle Axe",
        };

        sword.name = names[getRandomNum(names.length)];
        sword.durability = getRandomNum(7) + 1;
        sword.damage = getRandomNum((int) (gameInfo.maximumHealth / 5.0)) + 1;

        return sword;
    }

    public static Sword loadSword(String name, int[] dataArray) {
        Sword sword = new Sword();
        sword.name = name;
        sword.durability = dataArray[0];
        sword.damage = dataArray[1];

        return sword;

    }

    public static Shield createNewShield(GameInfo gameInfo) {
        // this creates a new shield
        Shield shield = new Shield();

        String[] names = {
                "Broad Shield",
                "Enchanted Shield",
                "Steel Plated Shield",
        };

        shield.name = names[getRandomNum(names.length)];
        shield.durability = getRandomNum((int) (3)) + 1;
        shield.defence = getRandomNum((int) (gameInfo.maximumHealth / 5.0)) + 1;

        return shield;
    }

    public static Shield loadShield(String name, int[] dataArray) {
        Shield shield = new Shield();
        shield.name = name;
        shield.durability = dataArray[0];
        shield.defence = dataArray[1];

        return shield;

    }

    public static Food createNewFood(GameInfo gameInfo) {
        // this creates a new food item
        Food food = new Food();

        String[] names = { "Beef Jerky", "Apple pie", "Hearty bread" };

        food.name = names[getRandomNum(names.length)];
        food.healthRegen = getRandomNum((int) (gameInfo.maximumHealth / 3.5)) + 1;

        return food;
    }

    public static Key createNewKey() {
        // this creates a new key
        Key key = new Key();

        String[] names = { "Gold key", "Old key", "Enchanted key" };

        key.name = names[getRandomNum(names.length)];
        key.disarmChance = getRandomNum(100) + 1;

        return key;
    }

    public static Key loadKey(String name, int disarmChance) {
        Key key = new Key();
        key.name = name;
        key.disarmChance = disarmChance;

        return key;

    }

    public static int getRandomNum(int num) {
        // rolls a dice with the number of sides passed to it
        Random random = new Random();
        int rngNum = random.nextInt(num);

        return rngNum;
    }

    public static boolean itemDropped() {
        // rolls a 5 sided dice to determine if an item was dropped
        int numberRolled = getRandomNum(5);

        if (numberRolled == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String getRandomDroppedItemType() {
        // rolls a 4 sided dice to determine what type of item was dropped
        int numberRolled = getRandomNum(4);

        if (numberRolled == 0) {
            return "sword";
        } else if (numberRolled == 1) {
            return "shield";
        } else if (numberRolled == 2) {
            return "food";
        } else {
            return "key";
        }
    }

    public static String getRandomLostItemType() {
        // rolls a 2 sided dice to determine what type of item was dropped
        int numberRolled = getRandomNum(2);

        if (numberRolled == 0) {
            return "sword";
        } else {
            return "shield";
        }
    }

    public static void displayMainMenu() {
        // displays the Main menu
        printString("--------~~~------");
        printString("Menu");
        printString("(1) Start new game");
        printString("(2) Load game");
        printString("--------~~~------");
    }

    public static void displayDroppedSword(Sword sword) {
        // displays the sword that is dropped
        printString("--------~~~------");
        printString("A Sword Dropped!");
        printString(sword.name + " (+ " + sword.damage + ") " + sword.durability);
        printString("--------~~~------");
    }

    public static void displayDroppedShield(Shield shield) {
        // displays the shield that is dropped
        printString("--------~~~------");
        printString("A Shield Dropped!");
        printString(shield.name + " (+ " + shield.defence + ") " + shield.durability);
        printString("--------~~~------");
    }

    public static void displayDroppedFood(Food food) {
        // displays the food that is dropped
        printString("--------~~~------");
        printString("Some food Dropped!");
        printString(
                "You ate the " +
                        food.name +
                        " and healed " +
                        food.healthRegen +
                        " points.");
        printString("--------~~~------");
    }

    public static void displayDroppedKey(Key key) {
        // displays the key that is dropped
        printString("--------~~~------");
        printString("A Key Dropped!");
        printString(key.name);
        printString("Success Chance: " + key.disarmChance + "%");
        printString("--------~~~------");
    }

    public static void displayScore(GameInfo gameInfo) {
        // displays the current room/score
        printString("--------~~~------");
        printString("Score: " + gameInfo.rooms);
        printString("--------~~~------");
    }

    public static void displayPlayerStats(Player player) {
        // displays the players stats
        printString("--------~~~------");
        printString("name: " + player.name);
        printString("health: " + player.health);
        printString("damage: " + player.damage);
        printString("defence: " + player.defence);
        printString("--------~~~------");
    }

    public static void displayPlayerInventory(Player player) {
        // displays the players inventory
        printString("--------~~~------");
        printString("    Inventory    ");
        displaySword(player);
        displayShield(player);
        displayKey(player);
        printString("--------~~~------");
    }

    public static void displaySword(Player player) {
        // displays an equipped sword
        if (player.inventory.sword == null) {
            printString("sword: empty");
        } else {
            printString("sword: " + player.inventory.sword.name + " (+ " + player.inventory.sword.damage + ") "
                    + player.inventory.sword.durability);
        }
    }

    public static void displayShield(Player player) {
        // displays an equipped shield
        if (player.inventory.shield == null) {
            printString("shield: empty");
        } else {
            printString("shield: " + player.inventory.shield.name + " (+ " + player.inventory.shield.defence + ") "
                    + player.inventory.shield.durability);
        }
    }

    public static void displayKey(Player player) {
        // displays an equipped shield
        if (player.inventory.key == null) {
            printString("key: empty");
        } else {
            printString("key: " + player.inventory.key.name + " (" + player.inventory.key.disarmChance + "%)");
        }
    }

    public static void displayEnemy(Enemy enemy) {
        // displays an enemys stats
        printString("--------~~~------");
        printString("name: " + enemy.name);
        printString("health: " + enemy.health);
        printString("damage: " + enemy.damage);
        printString("defence: " + enemy.defence);
        printString("--------~~~------");
    }

    public static void displayHud(GameInfo gameInfo, Player player) {
        // displays the score and inventory together
        displayScore(gameInfo);
        displayPlayerInventory(player);
    }

    public static boolean askUserToEquip() {
        // asks the user whether they would like to equip the dropped item
        Scanner userinput = new Scanner(System.in);
        printString("Would you like to equip the item");
        String answer = userinput.nextLine();

        while (!answer.equals("yes") & !answer.equals("no")) {
            printString("Invalid input(yes/no)");
            printString("Would you like to equip the item");
            answer = userinput.nextLine();
        }

        if (answer.equals("yes")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean askMenuOption() {
        // asks the user what option they would like to proceed with
        Scanner userinput = new Scanner(System.in);
        printString("Select your option");
        String answer = userinput.nextLine();

        while (!answer.equals("1") & !answer.equals("2")) {
            printString("Invalid input(1/2)");
            printString("Select your option");
            answer = userinput.nextLine();
        }

        if (answer.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public static void eatFood(Player player, Food food) {
        // players health increases with the food
        player.health += food.healthRegen;
    }

    public static void mainMenu() {
        boolean option;
        displayMainMenu();
        option = askMenuOption();
        if (option) {
            startNewGame();
        } else {
            String fileName = askFileName();
            if (!readSaveFile(fileName)) {
                printString("");
            }
        }
    }

    public static void droppedItemSequence(Player player, GameInfo gameInfo) {
        // asks the user whether they would like to equip the dropped item
        String droppedItemType = getRandomDroppedItemType();

        if (droppedItemType.equals("sword")) {
            Sword droppedSword = createNewSword(gameInfo);
            displayDroppedSword(droppedSword);
            if (askUserToEquip()) {
                player.inventory.sword = droppedSword;
            }
        } else if (droppedItemType.equals("shield")) {
            Shield droppedShield = createNewShield(gameInfo);
            displayDroppedShield(droppedShield);
            if (askUserToEquip()) {
                player.inventory.shield = droppedShield;
            }
        } else if (droppedItemType.equals("food")) {
            Food droppedFood = createNewFood(gameInfo);
            displayDroppedFood(droppedFood);
            eatFood(player, droppedFood);
        } else if (droppedItemType.equals("key")) {
            Key droppedKey = createNewKey();
            displayDroppedKey(droppedKey);
            if (askUserToEquip()) {
                player.inventory.key = droppedKey;
            }
        }
    }

    public static void looseItem(Player player, String prompt) {
        // determines what item a player looses/ calculates the amount of health lost
        String lostItem = getRandomLostItemType();

        printString(prompt);

        if (lostItem.equals("sword") & player.inventory.sword != null) {
            printString("You loose your sword");
            player.inventory.sword = null;
        } else if (lostItem.equals("shield") & player.inventory.shield != null) {
            printString("You loose your shield");
            player.inventory.shield = null;
        } else {
            int lostHealth = getRandomNum((player.health / 5) + 2) + 1;
            printString("You loose " + lostHealth + " health");
            player.health -= lostHealth;
        }
        player.inventory.key = null;
    }

    public static void trappedRoomSequence(Player player) {
        // checks whether the player passes a trapped room
        printString("--------~~~------");
        if (player.inventory.key == null) {
            looseItem(player, "The room is trapped and you dont have a key!");
        } else if (!determineIfKeyWorks(player.inventory.key)) {
            looseItem(player, "The room is trapped and your key didnt work!");
        } else {
            printString("The room is trapped but you successfully disarm the trap!");
            player.inventory.key = null;
        }
        printString("--------~~~------");
    }

    public static int playerDamage(Player player) {
        // calculates the total damage of a player in a round, also updates durability
        if (player.inventory.sword == null) {
            return player.damage;
        } else {
            int totalDamage = player.damage + player.inventory.sword.damage;
            player.inventory.sword.durability -= 1;
            if (player.inventory.sword.durability == 0) {
                printString("Your " + player.inventory.sword.name + " broke!");
                player.inventory.sword = null;
            }
            return totalDamage;
        }
    }

    public static int playerDefence(Player player) {
        // calculates the total defence of a player in a round, also updates durability
        if (player.inventory.shield == null) {
            return player.defence;
        } else {
            int totalDefence = player.defence + player.inventory.shield.defence;
            player.inventory.shield.durability -= 1;
            if (player.inventory.shield.durability == 0) {
                printString("Your " + player.inventory.shield.name + " broke!");
                player.inventory.shield = null;
            }
            return totalDefence;
        }
    }

    public static int healthLost(int damage, int defence) {
        // calculates the amount of health lost, if def > dmg then it returns 0
        if (damage <= defence) {
            return 0;
        } else {
            return damage - defence;
        }
    }

    public static boolean fightSequence(Room currentRoom, Player player, GameInfo gameInfo) {
        // carries out the fight phase of the room
        int totalEnemies = currentRoom.enemies;
        printString("--------~~~------");
        while (currentRoom.enemies > 0) {
            printString("-Enemy Encoutered-");
            Enemy enemy = createNewEnemy(gameInfo);
            int round = 1;
            while (player.health > 0 & enemy.health > 0) {
                if (round % 6 == 0) {
                    increaseEnemyDifficulty(enemy);
                }
                printString("(" + currentRoom.enemies + "/" + totalEnemies + ")");
                displayEnemy(enemy);
                printString("        vs.");
                displayPlayerStats(player);
                int enemyHealthLost = healthLost(playerDamage(player), enemy.defence);
                enemy.health -= enemyHealthLost;
                printString("You dealt " + enemyHealthLost + " damage!");

                int playerHealthLost = healthLost(enemy.damage, playerDefence(player));
                player.health -= playerHealthLost;
                printString("You lost " + playerHealthLost + " health!");

                if (itemDropped()) {
                    droppedItemSequence(player, gameInfo);
                }

                pressEnterToContinue();
                clearScreen();
                displayHud(gameInfo, player);
                round++;
            }

            if (player.health <= 0) {
                return false;
            } else if (enemy.health <= 0) {
                currentRoom.enemies -= 1;
                printString("You killed the " + enemy.name + "!");
                printString("--------~~~------");
                pressEnterToContinue();
                clearScreen();
                displayHud(gameInfo, player);
            }

        }

        return true;
    }

    public static void gameOverMessage(String prompt) {
        // prints out a gameover message
        printString("-=-=-=-=-=-=-=-=-=-");
        printString(prompt);
        printString("-=-=-=-=-=-=-=-=-=-");
    }

    public static void saveGame(GameInfo gameInfo, Player player, String fileName) {
        // writes the game data to a save file
        try {
            FileWriter writer = new FileWriter(fileName + ".txt");
            writer.write("GameInfo" + "\n");
            writer.write(gameInfo.maximumHealth + "\n");
            writer.write(gameInfo.maximumDamage + "\n");
            writer.write(gameInfo.maximumDefence + "\n");
            writer.write(gameInfo.rooms + "\n");
            if (gameInfo.gameover) {
                writer.write("true" + "\n");
            } else {
                writer.write("false" + "\n");
            }

            writer.write("Player" + "\n");
            writer.write(player.name + "\n");
            writer.write(player.health + "\n");
            writer.write(player.damage + "\n");
            writer.write(player.defence + "\n");

            if (!(player.inventory.sword == null)) {
                writer.write("Sword" + "\n");
                writer.write(player.inventory.sword.name + "\n");
                writer.write(player.inventory.sword.durability + "\n");
                writer.write(player.inventory.sword.damage + "\n");
                writer.write("-----" + "\n");
            }

            if (!(player.inventory.shield == null)) {
                writer.write("Shield" + "\n");
                writer.write(player.inventory.shield.name + "\n");
                writer.write(player.inventory.shield.durability + "\n");
                writer.write(player.inventory.shield.defence + "\n");
                writer.write("------" + "\n");
            }

            if (!(player.inventory.key == null)) {
                writer.write("Key" + "\n");
                writer.write(player.inventory.key.name + "\n");
                writer.write(player.inventory.key.disarmChance + "\n");
                writer.write("---" + "\n");
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean readSaveFile(String fileName) {
        try {
            File file = new File(fileName + ".txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                if (data.equals("true")) {
                    printString("Save file is already game over");
                    return false;
                }
            }
            reader.close();

            reader = new Scanner(file);

            String data = reader.nextLine();
            int[] dataArray = new int[4];
            for (int i = 0; i < 4; i++) {
                data = reader.nextLine();
                dataArray[i] = Integer.parseInt(data);
            }
            GameInfo gameInfo = createNewGameInfo(dataArray[0], dataArray[1], dataArray[2], dataArray[3]);
            data = reader.nextLine();
            data = reader.nextLine();

            dataArray = new int[3];
            String name = reader.nextLine();
            for (int i = 0; i < 3; i++) {
                data = reader.nextLine();
                dataArray[i] = Integer.parseInt(data);
            }
            Player player = createNewPlayer(name, dataArray[0], dataArray[1], dataArray[2]);
            data = reader.nextLine();

            if (data.equals("Sword")) {
                dataArray = new int[2];
                String swordName = reader.nextLine();
                for (int i = 0; i < 2; i++) {
                    data = reader.nextLine();
                    dataArray[i] = Integer.parseInt(data);
                }
                Sword sword = loadSword(swordName, dataArray);
                player.inventory.sword = sword;
                data = reader.nextLine();
            }

            if (data.equals("Shield")) {
                dataArray = new int[2];
                String shieldName = reader.nextLine();
                for (int i = 0; i < 2; i++) {
                    data = reader.nextLine();
                    dataArray[i] = Integer.parseInt(data);
                }
                Shield shield = loadShield(shieldName, dataArray);
                player.inventory.shield = shield;
                data = reader.nextLine();
            }

            if (data.equals("Key")) {
                String keyName = reader.nextLine();
                int disarmChance = Integer.parseInt(reader.nextLine());

                Key key = loadKey(keyName, disarmChance);
                player.inventory.key = key;
                data = reader.nextLine();
            }
            loadGame(gameInfo, player, fileName);

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

class Player {

    String name;
    int health;
    int damage;
    int defence;
    Inventory inventory = new Inventory();
}

class Inventory {

    Sword sword;
    Shield shield;
    Key key;
}

class Enemy {

    String name;
    int health;
    int damage;
    int defence;
}

class Sword {

    String name;
    int durability;
    int damage;
}

class Shield {

    String name;
    int durability;
    int defence;
}

class Food {

    String name;
    int healthRegen;
}

class Key {

    String name;
    int disarmChance;
}

class Room {

    int enemies;
    boolean isTrap;
}

class GameInfo {

    int maximumHealth; // minium health of spawned enemies
    int maximumDamage; // minimum damage of spawned enemies
    int maximumDefence; // maximum defence of spawned enemies
    int rooms; // stores the number of rooms that the player successfully passed
    boolean gameover; // stores wether the game is over or not
}
