package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final String FILENAME = "worldStat.txt";
    public static final int SCALE = 16;
    public static final int BIG = 30;
    public static final int SMALL = 15;
    public static final int TIME = 50;
    public static final int SIZE = 10;
    private String NAME = "";
    TERenderer ter = new TERenderer();
    public static boolean isLightOn = false;

    public static TETile[][] interactWithInputString(String input) {
        try {
            if (input.charAt(0) == 'n' || input.charAt(0) == 'N') {
                long seed = Long.parseLong(input.replaceAll("[^0-9]", ""));
                Random random = new Random(seed);
                TETile[][] world = worldInit();
                WorldCreator.worldGenerator(random, world);
                String pos = getAvatarPos(world);
                int avatarPosx = Integer.parseInt(pos.split(" ")[0]);
                int avatarPosy = Integer.parseInt(pos.split(" ")[1]);
                int startPos = 0;
                for (int i = 0; i < input.length(); i++) {
                    if (input.charAt(i) == 's' || input.charAt(i) == 'S') {
                        startPos = i + 1;
                        break;
                    }
                }
                moveString(input, startPos, world, avatarPosx, avatarPosy);
                return world;
            } else if (input.charAt(0) == 'L' || input.charAt(0) == 'l') {
                TETile[][] world = worldInit();
                String content = Files.readString(Path.of(FILENAME));
                int startPos = 0;
                int avatarPosx = 0;
                int avatarPosy = 0;
                for (int x = 0; x < world.length; x++) {
                    for (int y = 0; y < world[0].length; y++) {
                        char ch = content.charAt(startPos);
                        if (ch == '1') {
                            world[x][y] = Tileset.FLOOR;
                        } else if (ch == '2') {
                            world[x][y] = Tileset.WALL;
                        } else if (ch == '3') {
                            world[x][y] = Tileset.AVATAR;
                            avatarPosy = y;
                            avatarPosx = x;
                        } else if (ch == '4') {
                            world[x][y] = Tileset.NOTHING;
                        } else {
                            world[x][y] = Tileset.LOCKED_DOOR;
                        }
                        startPos++;
                    }
                }
                moveString(input, 1, world, avatarPosx, avatarPosy);
                return world;
            }
        } catch (IOException e) {
            System.out.println("File error");
            e.printStackTrace();
        }
        return null;
    }

    public static String getAvatarPos(TETile[][] world) {
        boolean foundAvatar = false;
        int avatarX = 0;
        int avatarY = 0;
        for (int y = 0; y < world[0].length; y++) {
            for (int x = 0; x < world.length; x++) {
                if (world[x][y].equals(Tileset.AVATAR)) {
                    avatarX = x;
                    avatarY = y;
                    foundAvatar = true;
                    break;
                }
            }
            if (foundAvatar) {
                break;
            }
        }
        return avatarX + " " + avatarY;
    }

    public static boolean isValid(int x, int y, TETile[][] world) {
        return (x < world.length) && (x >= 0) && (y >= 0) && (y < world[0].length);
    }

    public static boolean isTraversable(TETile[][] world, int x, int y) {
        return world[x][y].equals(Tileset.FLOOR)
                || world[x][y].equals(Tileset.LIGHT)
                || world[x][y].equals(Tileset.LIGHT_SOURCE);
    }

    private static String moveString(String input, int start, TETile[][] world, int avatarPosx, int avatarPosy) {
        try {
            int startPos = start;
            while (startPos < input.length()) {
                char ch = input.charAt(startPos);
                if (ch == 'A' || ch == 'a') {
                    int newx = avatarPosx - 1;
                    if (isValid(newx, avatarPosy, world)) {
                        if (isTraversable(world, newx, avatarPosy)) {
                            world[newx][avatarPosy] = Tileset.AVATAR;
                            world[avatarPosx][avatarPosy] = Tileset.FLOOR;
                            avatarPosx = newx;
                        }
                    }
                } else if (ch == 'S' || ch == 's') {
                    int newy = avatarPosy - 1;
                    if (isValid(avatarPosx, newy, world)) {
                        if (isTraversable(world, avatarPosx, newy)) {
                            world[avatarPosx][newy] = Tileset.AVATAR;
                            world[avatarPosx][avatarPosy] = Tileset.FLOOR;
                            avatarPosy = newy;
                        }
                    }
                } else if (ch == 'd' || ch == 'D') {
                    int newx = avatarPosx + 1;
                    if (isValid(newx, avatarPosy, world)) {
                        if (isTraversable(world, newx, avatarPosy)) {
                            world[newx][avatarPosy] = Tileset.AVATAR;
                            world[avatarPosx][avatarPosy] = Tileset.FLOOR;
                            avatarPosx = newx;
                        }
                    }
                } else if (ch == 'W' || ch == 'w') {
                    int newy = avatarPosy + 1;
                    if (isValid(avatarPosx, newy, world)) {
                        if (isTraversable(world, avatarPosx, newy)) {
                            world[avatarPosx][newy] = Tileset.AVATAR;
                            world[avatarPosx][avatarPosy] = Tileset.FLOOR;
                            avatarPosy = newy;
                        }
                    }
                } else if (ch == ':') {
                    if (startPos + 1 < input.length()) {
                        char ch1 = input.charAt(startPos + 1);
                        if (ch1 == 'Q' || ch1 == 'q') {
                            exportLightStatus();
                            try (FileWriter fileWriter = new FileWriter(FILENAME)) {
                                for (TETile[] teTiles : world) {
                                    for (int y = 0; y < world[0].length; y++) {
                                        fileWriter.write(getString(teTiles, y));
                                    }
                                }
                            }
                            return avatarPosx + " " + avatarPosy;
                        }
                        startPos += 1;
                    }
                } else if (ch == 'l' || ch == 'L') {
                    String content = Files.readString(Path.of(FILENAME));
                    for (int x = 0; x < world.length; x++) {
                        for (int y = 0; y < world[0].length; y++) {
                            char chContent = content.charAt(startPos);
                            boolean shouldSet = setWorld(chContent, x, y, world);
                            if (shouldSet) {
                                avatarPosy = y;
                                avatarPosx = x;
                            }
                            startPos++;
                        }
                    }
                }
                startPos += 1;
            }
            return avatarPosx + " " + avatarPosy;
        } catch (IOException e) {
            return null;
        }
    }


    public static boolean setWorld(char ch, int x, int y, TETile[][] world) {
        if (ch == '1') {
            world[x][y] = Tileset.FLOOR;
        } else if (ch == '2') {
            world[x][y] = Tileset.WALL;
        } else if (ch == '3') {
            world[x][y] = Tileset.AVATAR;
            return true;
        } else if (ch == '4') {
            world[x][y] = Tileset.NOTHING;
        } else if (ch == '5') {
            world[x][y] = Tileset.LOCKED_DOOR;
        } else if (ch == '6') {
            world[x][y] = Tileset.LIGHT_SOURCE;
        } else if (ch == '7') {
            world[x][y] = Tileset.LIGHT;
        }
        return false;
    }


    public static String getString(TETile[] teTiles, int y) {
        if (teTiles[y].equals(Tileset.FLOOR)) {
            return "1";
        } else if (teTiles[y].equals(Tileset.WALL)) {
            return "2";
        } else if (teTiles[y].equals(Tileset.AVATAR)) {
            return "3";
        } else if (teTiles[y].equals(Tileset.NOTHING)) {
            return "4";
        } else if (teTiles[y].equals(Tileset.LOCKED_DOOR)) {
            return "5";
        } else if (teTiles[y].equals(Tileset.LIGHT_SOURCE)) {
            return "6";
        } else if (teTiles[y].equals(Tileset.LIGHT)) {
            return "7";
        }
        return "1";
    }


    public static TETile[][] worldInit() {
        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        return world;
    }

    public static void lastLightStatus() {
        String fileName = "lightedStatus.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("true")) {
                    isLightOn = true;
                } else {isLightOn = false;}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Add light function, including caching and read light points file
    Add and remove lights controls preHUD.
     */
    public static void exportLightSource() {
        String fileName = "lightSource.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Point point : Room.lightList) {
                writer.write(point.getX() + "," + point.getY());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportLightedPoints(TETile[][] world) {
        String fileName = "lightedPoints.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Point point : getLightedPoints(world)) {
                writer.write(point.getX() + "," + point.getY());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportLightStatus() {
        String fileName = "lightedStatus.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(String.valueOf(isLightOn));
            }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Point> readPoints(String fileName) {
        List<Point> points = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                points.add(new Point(x, y));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }

    public static List<Point> getLightedPoints(TETile[][] world) {
        List<Point> lightedPoints = new ArrayList<>();
        for (Point lt : Room.lightList) {
            int xStart = lt.getX() - 2;
            int yStart = lt.getY() - 2;
            Point startP = new Point(xStart, yStart);
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (startP.getX() + i>= 0 && startP.getY() + j>=0 &&
                            startP.getX() + i < WIDTH && startP.getY() + j < HEIGHT) {
                        if (world[startP.getX() + i][startP.getY() + j].equals(Tileset.FLOOR)
                        || world[startP.getX() + i][startP.getY() + j].equals(Tileset.AVATAR)) {
                        lightedPoints.add(new Point(startP.getX() + i, startP.getY() + j));
                        }
                    }
                }
            }
        }
        return lightedPoints;

    }

    public static void addLight(TETile[][] world) {
        List<Point> lightSourceList = readPoints("lightSource.txt");
        List<Point> lightedPointList = readPoints("lightedPoints.txt");
        for (Point light : lightedPointList) {
            if (!world[light.getX()][light.getY()].equals(Tileset.AVATAR)) {
                world[light.getX()][light.getY()] = Tileset.LIGHT;
            }
        }
        for (Point lt : lightSourceList) {
            if (!world[lt.getX()][lt.getY()].equals(Tileset.AVATAR)) {
                world[lt.getX()][lt.getY()] = Tileset.LIGHT_SOURCE;
            }
        }
        }

    public static void removeLight(TETile[][] world) {
        List<Point> lightSourceList = readPoints("lightSource.txt");
        List<Point> lightedPointList = readPoints("lightedPoints.txt");
        for (Point light : lightedPointList) {
            if (!world[light.getX()][light.getY()].equals(Tileset.AVATAR)) {
                world[light.getX()][light.getY()] = Tileset.FLOOR;
            }
        }
        for (Point lt : lightSourceList) {
            if (!world[lt.getX()][lt.getY()].equals(Tileset.AVATAR)) {
                world[lt.getX()][lt.getY()] = Tileset.FLOOR;
            }
        }
    }

    public static void updateLight(TETile[][] world) {
        if (isLightOn == true) {addLight(world);}
        else if (!isLightOn) {removeLight(world);}
    }


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        drawStartPage();
        TETile[][] world = startGame();
        playGame(world);
    }

    private void drawStartPage() {
        StdDraw.setCanvasSize(WIDTH * SCALE, HEIGHT * SCALE);
        Font font = new Font("Monaco", Font.BOLD, BIG);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, BIG);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "CS61B: THE GAME");
        Font fontSmall = new Font("Monaco", Font.BOLD, SMALL);
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "New Game (N)");
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 4 - 1, "Load Game (L)");
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 4 - 2, "Quit (Q)");
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 4 - 3, "Create Name (*)");
        StdDraw.show();
    }

    public void drawFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, SIZE);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    private void displayMenu(TETile[][] world) {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        String text = "";
        if (!isValid(mouseX, mouseY, world)) {
            return;
        }
        if (world[mouseX][mouseY].equals(Tileset.AVATAR)) {
            text = "avatar";
        } else if (world[mouseX][mouseY].equals(Tileset.FLOOR)) {
            text = "floor";
        } else if (world[mouseX][mouseY].equals(Tileset.WALL)) {
            text = "wall";
        } else if (world[mouseX][mouseY].equals(Tileset.LOCKED_DOOR)) {
            text = "locked door";
        } else {
            text = "nothing";
        }
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, SIZE);
        StdDraw.setFont(fontBig);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.filledRectangle(WIDTH/2, HEIGHT - 1, WIDTH/2, 1);
        StdDraw.setPenColor(Color.WHITE);
        if (NAME.length() > 0) {
            StdDraw.text(WIDTH/2, HEIGHT - 1, "Hello, " + NAME.substring(1) + "!");
        } else {
            StdDraw.text(WIDTH/2, HEIGHT - 1, "Hello, Avator!");
        }
        StdDraw.text(10, HEIGHT - 1, "This is a " + text);
        StdDraw.text(WIDTH - 10, HEIGHT - 1, "Light mode: Press o");
        StdDraw.text(WIDTH - 20, HEIGHT - 1, "Dark mode: Press f");
        StdDraw.show();
    }

    private void playGame(TETile[][] world) {

        String pos0 = getAvatarPos(world);
        int avatarX = Integer.parseInt(pos0.split(" ")[0]);
        int avatarY = Integer.parseInt(pos0.split(" ")[1]);
        lastLightStatus();

        while (true) {
            displayMenu(world);
            if (StdDraw.hasNextKeyTyped()) {
                String input = Character.toString(StdDraw.nextKeyTyped());
                if (input.charAt(0) != ':') {
                    if (input.charAt(0) == 'O' || input.charAt(0) == 'o') {
                        isLightOn = true;
                        updateLight(world);
                    }
                    if (input.charAt(0) == 'F' || input.charAt(0) == 'f') {
                        isLightOn = false;
                        updateLight(world);
                    }
                    if (input.charAt(0) == 'L' || input.charAt(0) == 'l') {
                        String pos = moveString("L", 0, world, avatarX, avatarY);
                        avatarX = Integer.parseInt(pos.split(" ")[0]);
                        avatarY = Integer.parseInt(pos.split(" ")[1]);
                        ter.renderFrame(world);
                    } else {
                        String pos = moveString(input, 0, world, avatarX, avatarY);
                        avatarX = Integer.parseInt(pos.split(" ")[0]);
                        avatarY = Integer.parseInt(pos.split(" ")[1]);
                        updateLight(world);
                        ter.renderFrame(world);
                    }
                } else {
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            String input2 = Character.toString(StdDraw.nextKeyTyped());
                            if (input2.charAt(0) == 'q' || input2.charAt(0) == 'Q') {
                                moveString(":Q", 0, world, avatarX, avatarY);
                                return;
                            }
                        }
                    }
                }

            }
        }

    }

    private TETile[][] startGame() {
        String ans = "";
        String name = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String input = Character.toString(StdDraw.nextKeyTyped());
                if (input.charAt(0) == '*') {
                    drawFrame("Avatar Name (End with #):");
                    name += input;
                } else if (name.length() > 0 && name.charAt(0) == '*' && input.charAt(0) != '#') {
                    if (Character.isLetter(input.charAt(0))) {
                        name += input;
                        drawFrame("Avatar Name (End with #):" + name);
                    }
                } else if (input.charAt(0) == '#') {
                    NAME = name;
                    name = "";
                    drawStartPage();
                } else if (input.charAt(0) == 'n' || input.charAt(0) == 'N') {
                    ans += input;
                    drawFrame("Input a seed (end with s): " + ans);
                    StdDraw.pause(TIME);
                } else if (input.charAt(0) == 's' || input.charAt(0) == 'S') {
                    ans += input;
                    drawFrame(ans);
                    long seed = Long.parseLong(ans.replaceAll("[^0-9]", ""));
                    Random random = new Random(seed);
                    TETile[][] world = worldInit();
                    WorldCreator.worldGenerator(random, world);
                    exportLightSource();
                    exportLightedPoints(world);
                    exportLightStatus();
                    StdDraw.pause(TIME);
                    ter.renderFrame(world);
                    displayMenu(world);
                    return world;
                } else if (input.charAt(0) == 'l' || input.charAt(0) == 'L') {
                    ans += input;
                    drawFrame(ans);
                    TETile[][] world = worldInit();
                    moveString("L", 0, world, -1, -1);
                    ter.renderFrame(world);
                    displayMenu(world);
                    return world;
                } else if (input.charAt(0) == 'q' || input.charAt(0) == 'Q') {
                    return null;}
                else {
                    ans += input;
                    drawFrame("Input a seed (end with s): " + ans);
                    StdDraw.pause(TIME);
                }
            }
        }
    }
}
