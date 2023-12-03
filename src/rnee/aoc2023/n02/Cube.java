package rnee.aoc2023.n02;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class Cube {
    public static class Balls {
        int red; int green; int blue;

        public Balls(int red, int green, int blue) {
            this.red = red; this.green = green; this.blue = blue;
        }

        public static Balls max(Balls a, Balls b) {
            return new Balls(Math.max(a.red, b.red), Math.max(a.green, b.green), Math.max(a.blue, b.blue));
        }
    }

    public static void main(String[] args) throws Exception {
        try (FileReader fin = new FileReader("res/n02/games.txt"); Stream<String> lines = new LineNumberReader(fin).lines()) {
            System.out.println(lines.map(Cube::getMinBalls).reduce(0, Integer::sum));
        }
    }

    private static int getMinBalls(String line) {
        String[] game = line.split(":");
        Balls minBalls = new Balls(0, 0, 0);

        String[] draws = game[1].split(";");

        for (String draw : draws) {
            Balls drawBalls = new Balls(0, 0, 0);
            String[] balls = draw.split(",");

            for (String ball : balls) {
                String[] ballSplit = ball.trim().split(" ");

                int numBalls = Integer.parseInt(ballSplit[0].trim());
                switch(ballSplit[1]) {
                    case "blue":
                        drawBalls.blue = numBalls;
                        break;
                    case "green":
                        drawBalls.green = numBalls;
                        break;
                    case "red":
                        drawBalls.red = numBalls;
                        break;
                }
            }
            minBalls = Balls.max(minBalls, drawBalls);
        }

        return minBalls.red * minBalls.green * minBalls.blue;
    }

    private final static int MAX_RED = 12;
    private final static int MAX_GREEN = 13;
    private final static int MAX_BLUE = 14;
    public static long getIdIfValidGame(String line) {
        String[] game = line.split(":");
        boolean valid = true;
        long gameId = Long.parseLong(game[0].split(" ")[1]);

        String[] draws = game[1].split(";");
        int d = 0;
        while (valid && d < draws.length) {
            String draw = draws[d];
            String[] balls = draw.split(",");

            int b = 0;
            while (valid && b < balls.length) {
                String[] ball = balls[b].trim().split(" ");

                int numBalls = Integer.parseInt(ball[0].trim());
                switch(ball[1]) {
                    case "blue":
                        if (numBalls > MAX_BLUE) valid = false;
                        break;
                    case "green":
                        if (numBalls > MAX_GREEN) valid = false;
                        break;
                    case "red":
                        if (numBalls > MAX_RED) valid = false;
                        break;
                }

                b++;
            }


            d++;
        }


        return valid ? gameId : 0L;
    }
}
