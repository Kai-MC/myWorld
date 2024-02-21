package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;
import java.util.Random;
import static byow.Core.RandomUtils.uniform;

class Hallway {
    private List<Room> rooms;
    private List<Point> points;
    public Hallway(List<Room> rooms_){
        rooms = rooms_;
        points = new ArrayList<>();
    }
    public int min(int a, int b){
        if(a<b) {
            return a;
        }
        return b;
    }
    public int max(int a, int b){
        if(a<b) {
            return b;
        }
        return a;
    }
    public void linkTwoPoints(Point p1, Point p2, TETile[][] world, boolean len2) {
        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();
        int min_x = min(x1, x2);
        int min_y = min(y1, y2);
        int max_x = max(x1, x2);
        int max_y = max(y1, y2);
        if(((x1 == min_x) && (y1 == min_y)) || ((x2 == min_x) && (y2 == min_y))) {
            for(int j = min_y; j<=max_y; j++) {
                world[min_x][j] = Tileset.FLOOR;
                points.add(new Point(min_x, j));
            }
            for(int i = min_x; i<=max_x; i++) {
                world[i][max_y] = Tileset.FLOOR;
                points.add(new Point(i, max_y));
            }

        } else {
            for(int j = max_y; j>=min_y; j--) {
                world[min_x][j] = Tileset.FLOOR;
                points.add(new Point(min_x, j));
            }
            for(int i = min_x; i<=max_x; i++) {
                world[i][min_y] = Tileset.FLOOR;
                points.add(new Point(i, min_y));
            }
        }
    }
    public void linkTwoPointsCase1(Point p1, Point p2, TETile[][] world, boolean len2) {

    }
    public Point getRandomPointFromRoom(Room r1, Random RANDOM) {
        int x_bottom = r1.BottomPoint().getX();
        int y_bottom = r1.BottomPoint().getY();
        int x_upper = r1.UpperPoint().getX();
        int y_upper = r1.UpperPoint().getY();
        int xP = uniform(RANDOM, x_bottom+1, x_upper-1);
        int yP = uniform(RANDOM, y_bottom+1, y_upper-1);
        Point p = new Point(xP, yP);
        return p;
    }
    public void buildHallway(Room r1, Room r2, Random RANDOM, TETile[][] world) {
        int hallwayLen = uniform(RANDOM, 0, 2);
        Point p1 = getRandomPointFromRoom(r1, RANDOM);
        Point p2 = getRandomPointFromRoom(r2, RANDOM);
        if (hallwayLen < 1) {
            linkTwoPoints(p1, p2, world, false);
        } else {
            linkTwoPoints(p1, p2, world, true);
        }
    }
    public boolean shouldAddWall(int x, int y, TETile[][] world) {
        if(x<0 || x>= world.length || y<0 || y>= world[0].length) {
            return false;
        }
        if(world[x][y].equals(Tileset.FLOOR) || world[x][y].equals(Tileset.WALL)) {
            return false;
        }
        return true;
    }
    public void addWalls(TETile[][] world){
        for(Point p : points){
            int x = p.getX();
            int y = p.getY();
            if (shouldAddWall(x-1, y, world)) {
                world[x-1][y] = Tileset.WALL;
            }
            if (shouldAddWall(x+1, y, world)) {
                world[x+1][y] = Tileset.WALL;
            }
            if (shouldAddWall(x, y-1, world)) {
                world[x][y-1] = Tileset.WALL;
            }
            if (shouldAddWall(x, y+1, world)) {
                world[x][y+1] = Tileset.WALL;
            }
            if (shouldAddWall(x+1, y+1, world)) {
                world[x+1][y+1] = Tileset.WALL;
            }
            if (shouldAddWall(x-1, y+1, world)) {
                world[x-1][y+1] = Tileset.WALL;
            }
            if (shouldAddWall(x+1, y-1, world)) {
                world[x+1][y-1] = Tileset.WALL;
            }
            if (shouldAddWall(x-1, y-1, world)) {
                world[x-1][y-1] = Tileset.WALL;
            }
        }
    }
    public void buildWholeWorld(TETile[][] world, Random RANDOM) {
        int numOfHalls = rooms.size() + RANDOM.nextInt(6);
        int count = 0;
        for(int i = 0; i<rooms.size()-1; i++) {
            Room r1 = rooms.get(i);
            Room r2 = rooms.get(i+1);
            buildHallway(r1, r2, RANDOM, world);
            count += 1;
        }
        buildHallway(rooms.get(rooms.size()-1), rooms.get(0), RANDOM, world);
        count += 1;
        for(int i=0; i<(numOfHalls-count); i++) {
            int room_id_1 = RANDOM.nextInt(rooms.size());
            int room_id_2 = RANDOM.nextInt(rooms.size());
            while(room_id_2 == room_id_1) room_id_2 = RANDOM.nextInt(rooms.size());
            buildHallway(rooms.get(room_id_1), rooms.get(room_id_2), RANDOM, world);
        }
        addWalls(world);
    }
}