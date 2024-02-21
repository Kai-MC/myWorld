package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

import static byow.Core.RandomUtils.uniform;

class Room {
    //p represent left bottom point
    Point p;
    int width;
    int height;
    public static List<Point> lightList = new ArrayList<>();

    public Room(Point point,int w, int h) {
        p = point;
        width = w;
        height = h;
    }

    public int height() {
        return height;
    }
    public int width() {
        return width;
    }
    public Point position() {
        return p;
    }
    public Point BottomPoint() {
        int setX = p.getX() + 1;
        int setY = p.getY() + 1;
        return new Point(setX, setY);
    }
    public Point UpperPoint() {
        int setX = p.getX() + width - 2;
        int setY = p.getY() + height - 2;
        return new Point(setX, setY);
    }

//    public static void printLight(TETile[][] world, List<Point> LightList) {
//        for (Point lt: LightList){
//            world[lt.getX()][lt.getY()] = Tileset.LIGHT_SOURCE;
//        }
//        int xStart = light.getX()-1;
//        int yStart = light.getY()-1;
//        Point Start = new Point(xStart, yStart);
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (Start.getX()+i > p.getX() && Start.getX()+i<p.getX()+width-1
//                && Start.getY()+j > p.getY() && Start.getY()+j<p.getY()+height-1) {
//                    world[Start.getX() + i][Start.getY() + j] = Tileset.LIGHT;
//                }
//            }
//        }


    public void printRoom(TETile[][] world) {
        // Print vertical wall.
        for (int i = 0; i < height; i++) {
            // If crash the edges, update height and width.
            world[p.getX()][p.getY() + i] = Tileset.WALL;
            world[p.getX() + width - 1][p.getY() + i] = Tileset.WALL;
        }
        // Print horizontal wall.
        for (int i = 0; i < width; i++) {
            world[p.getX() + i][p.getY()] = Tileset.WALL;
            world[p.getX() + i][p.getY() + height - 1] = Tileset.WALL;
        }
        // Print floor.
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                world[p.getX() + j][p.getY() + i] = Tileset.FLOOR;
            }
        }
    }

    private static Room randomRoom(Random RANDOM, TETile[][] world) {
        int xP = uniform(RANDOM, 0, world.length - 11);
        int yP = uniform(RANDOM, 0, world[0].length - 11);
        Point p = new Point(xP, yP);

        int height = uniform(RANDOM, 6, 12);
        int width = uniform(RANDOM, 6, 12);
        return new Room(p, width, height);
    }


    private static Point randomLightPos(Random RANDOM, Room ROOM) {
        int xLight = uniform(RANDOM, ROOM.p.getX()+1, ROOM.p.getX()+ROOM.width-1);
        int yLight = uniform(RANDOM, ROOM.p.getY()+1, ROOM.p.getY()+ROOM.height-1);
        return new Point(xLight, yLight);
    }

    public boolean isRoomOverlap(Room r1, Room r2) {
        return !(r1.p.getX() >= r2.p.getX() + r2.width ||
                r1.p.getY() >= r2.p.getY() + r2.height ||
                r1.p.getX() + r1.width <= r2.p.getX()  ||
                r1.p.getY() + r1.height <= r2.p.getY());
    }

    public boolean isValidRoom(List<Room> rooms) {
        for (Room r : rooms) {
            if (isRoomOverlap(this, r)){
                return false;
            }
        }
        return true;
    }


    public static List<Room> roomGenerator(Random RANDOM, TETile[][] world) {
        int numOfRooms = 5 + RANDOM.nextInt(10);
        List<Room> rooms = new LinkedList<>();
        int curNumOfRooms = 0;
        while (curNumOfRooms < numOfRooms) {
            Room curRoom = randomRoom(RANDOM, world);
            if (curRoom.isValidRoom(rooms)){
                rooms.add(curRoom);
                lightList.add(randomLightPos(RANDOM,curRoom));
                curNumOfRooms += 1;
            }
        }
        for (Room r: rooms) {
            r.printRoom(world);
        }
//        Map<String,List> map =new HashMap();
//        map.put("roomList",rooms);
//        map.put("lightList", lightPoints);
        return rooms;
    }
}