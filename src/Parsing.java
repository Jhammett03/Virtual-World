import processing.core.PImage;

import java.util.*;

/**
 * This class contains many functions written in a procedural style.
 * You will reduce the size of this class over the next several weeks
 * by refactoring this codebase to follow an OOP style.
 */
public final class Parsing {
    public static final Random rand = new Random();

    public static final List<String> PATH_KEYS = new ArrayList<>(Arrays.asList("bridge", "dirt", "dirt_horiz", "dirt_vert_left", "dirt_vert_right", "dirt_bot_left_corner", "dirt_bot_right_up", "dirt_vert_left_bot"));

    private static final int PROPERTY_KEY = 0;
    private static final int PROPERTY_ID = 1;
    private static final int PROPERTY_COL = 2;
    private static final int PROPERTY_ROW = 3;
    private static final int ENTITY_NUM_PROPERTIES = 4;

    private static final int STUMP_NUM_PROPERTIES = 0;

    private static final int SAPLING_HEALTH = 0;
    private static final int SAPLING_NUM_PROPERTIES = 1;

    private static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_ANIMATION_PERIOD = 0;
    private static final int OBSTACLE_NUM_PROPERTIES = 1;

    private static final String DUDE_KEY = "dude";
    private static final int DUDE_ACTION_PERIOD = 0;
    private static final int DUDE_ANIMATION_PERIOD = 1;
    private static final int DUDE_LIMIT = 2;
    private static final int DUDE_NUM_PROPERTIES = 3;

    private static final String HOUSE_KEY = "house";
    private static final int HOUSE_NUM_PROPERTIES = 0;

    private static final String FAIRY_KEY = "fairy";
    private static final int FAIRY_ANIMATION_PERIOD = 0;
    private static final int FAIRY_ACTION_PERIOD = 1;
    private static final int FAIRY_NUM_PROPERTIES = 2;

    private static final int TREE_ANIMATION_PERIOD = 0;
    private static final int TREE_ACTION_PERIOD = 1;
    private static final int TREE_HEALTH = 2;
    private static final int TREE_NUM_PROPERTIES = 3;

    private static final String STUMP_KEY = "stump";
    private static final String SAPLING_KEY = "sapling";
    private static final String TREE_KEY = "tree";


    private static final String PORTAL_KEY = "portal";
    private static final int PORTAL_LINKED_ID = 4; // Assuming the linked portal ID is the 5th property
    private static final int PORTAL_NUM_PROPERTIES = 5;


    //move parse* methods to Entity because they create new entities or move to WorldModel as private utility methods
    public static void parseSapling(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = Factory.createSapling(id, pt, imageStore.getImageList(SAPLING_KEY), health);
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }

    public static void parseDude(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Entity entity = Factory.createDudeNotFull(id, pt, Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[DUDE_LIMIT]), imageStore.getImageList(DUDE_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }

    public static void parseFairy(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Entity entity = Factory.createFairy(id, pt, Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]), imageStore.getImageList(FAIRY_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }

    public static void parseTree(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Entity entity = Factory.createTree(id, pt, Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]), imageStore.getImageList(TREE_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }

    public static void parseObstacle(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Entity entity = Factory.createObstacle(id, pt, Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]), imageStore.getImageList(OBSTACLE_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }

    public static void parseHouse(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Entity entity = Factory.createHouse(id, pt, imageStore.getImageList(HOUSE_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }

    public static void parseStump(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Entity entity = Factory.createStump(id, pt, imageStore.getImageList(STUMP_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }


    public static void load(WorldModel worldModel, Scanner saveFile, ImageStore imageStore, Background defaultBackground) {
        parseSaveFile(worldModel, saveFile, imageStore, defaultBackground);
        if (worldModel.background == null) {
            worldModel.background = new Background[worldModel.getNumRows()][worldModel.getNumCols()];
            for (Background[] row : worldModel.background)
                Arrays.fill(row, defaultBackground);
        }
        if (worldModel.occupancy == null) {
            worldModel.occupancy = new Entity[worldModel.getNumRows()][worldModel.getNumCols()];
            worldModel.entities = new HashSet<>();
        }
    }

    public static void parsePortal(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == 3) {
            String linkedPortalId = properties[0];
            int x = Integer.parseInt(properties[1]);
            int y = Integer.parseInt(properties[2]);

            Point linkedPt = new Point(x, y);

            Entity entity = Factory.createPortal(id, pt, imageStore.getImageList(Portal.PORTAL_KEY), 0, 0, linkedPortalId, linkedPt);
            world.tryAddEntity(entity);
        }
    }




    static void parseEntity(WorldModel worldModel, String line, ImageStore imageStore) {
        String[] properties = line.split(" ", ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[PROPERTY_COL]), Integer.parseInt(properties[PROPERTY_ROW]));

            properties = properties.length == ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case OBSTACLE_KEY -> parseObstacle(worldModel, properties, pt, id, imageStore);
                case DUDE_KEY -> parseDude(worldModel, properties, pt, id, imageStore);
                case FAIRY_KEY -> parseFairy(worldModel, properties, pt, id, imageStore);
                case HOUSE_KEY -> parseHouse(worldModel, properties, pt, id, imageStore);
                case TREE_KEY -> parseTree(worldModel, properties, pt, id, imageStore);
                case SAPLING_KEY -> parseSapling(worldModel, properties, pt, id, imageStore);
                case STUMP_KEY -> parseStump(worldModel, properties, pt, id, imageStore);
                case PORTAL_KEY -> parsePortal(worldModel, properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        } else {
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }

    static void parseBackgroundRow(WorldModel worldModel, String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if (row < worldModel.getNumRows()) {
            int rows = Math.min(cells.length, worldModel.getNumCols());
            for (int col = 0; col < rows; col++) {
                worldModel.background[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
            }
        }
    }

    private static void parseSaveFile(WorldModel worldModel, Scanner saveFile, ImageStore imageStore, Background defaultBackground) {
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while (saveFile.hasNextLine()) {
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if (line.endsWith(":")) {
                headerLine = lineCounter;
                lastHeader = line;
                switch (line) {
                    case "Backgrounds:" -> worldModel.background = new Background[worldModel.getNumRows()][worldModel.getNumCols()];
                    case "Entities:" -> {
                        worldModel.occupancy = new Entity[worldModel.getNumRows()][worldModel.getNumCols()];
                        worldModel.entities = new HashSet<>();
                    }
//                    case "Portals:" -> parsePortalSection(worldModel, saveFile, imageStore);
                }
            } else {
                switch (lastHeader) {
                    case "Rows:" -> worldModel.numRows = Integer.parseInt(line);
                    case "Cols:" -> worldModel.numCols = Integer.parseInt(line);
                    case "Backgrounds:" -> parseBackgroundRow(worldModel, line, lineCounter - headerLine - 1, imageStore);
                    case "Entities:" -> parseEntity(worldModel, line, imageStore);
                    // No changes needed for "Portals:" section here
                }
            }
        }
    }
//    private static void parsePortalSection(WorldModel worldModel, Scanner saveFile, ImageStore imageStore) {
//        while (saveFile.hasNextLine()) {
//            String line = saveFile.nextLine().strip();
//            if (line.isBlank()) {
//                // End of the Portals section
//                break;
//            }
//            parsePortal(worldModel, line.split(" "), imageStore);
//        }
//    }

}
