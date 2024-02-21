package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;
import java.util.Random;

public class WorldCreator {
    public static TETile[][] worldGenerator(Random RANDOM, TETile[][] world) {
        List<Room> rooms= Room.roomGenerator(RANDOM, world);
        Hallway hallway = new Hallway(rooms);
        hallway.buildWholeWorld(world, RANDOM);
        boolean found_locked_door = false;
        for (int y = 0; y < world[0].length; y++) {
            for (int x = 0; x < world.length; x++) {
                if (world[x][y].equals(Tileset.WALL)) {
                    world[x][y] = Tileset.LOCKED_DOOR;
                    found_locked_door = true;
                    break;
                }
            }
            if(found_locked_door) {
                break;
            }
        }
        boolean found_avatar = false;
        for (int y = 0; y < world[0].length; y++) {
            for (int x = 0; x < world.length; x++) {
                if (world[x][y].equals(Tileset.FLOOR)) {
                    world[x][y] = Tileset.AVATAR;
                    found_avatar = true;
                    break;
                }
            }
            if(found_avatar) {
                break;
            }
        }
        return world;
    }

    public static void main(String[] args) {
        int WIDTH = 80, HEIGHT = 50;
        int seed = 1234567;
        Random RANDOM = new Random(seed);
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        worldGenerator(RANDOM, world);
        ter.renderFrame(world);
    }
}
