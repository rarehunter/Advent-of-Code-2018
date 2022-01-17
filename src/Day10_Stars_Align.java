import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day10_Stars_Align {
    public static void main(String[] args) {
        File file = new File("./inputs/day10/day10.txt");

        try {
            Scanner sc = new Scanner(file);

            List<Light> lights = new ArrayList<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String processedLine = line.replaceAll("\\s","");

                int startBracket1 = processedLine.indexOf('<');
                int endBracket1 = processedLine.indexOf('>');
                String position = processedLine.substring(startBracket1 + 1, endBracket1);
                String[] positionTokens = position.split(",");

                int startBracket2 = processedLine.indexOf('<', startBracket1 + 1);
                int endBracket2 = processedLine.indexOf('>', endBracket1 + 1);
                String velocity = processedLine.substring(startBracket2 + 1, endBracket2);
                String[] velocityTokens = velocity.split(",");

                Position p = new Position(Integer.parseInt(positionTokens[0]), Integer.parseInt(positionTokens[1]));
                Velocity v = new Velocity(Integer.parseInt(velocityTokens[0]), Integer.parseInt(velocityTokens[1]));

                lights.add(new Light(p, v));
            }

            System.out.println("Part 1 is: ");
            int seconds = part1and2(lights);
            System.out.println("Part 2 is: " + seconds);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Given a list of lights and a position (x,y), returns true if there exists a light
    // that is in the given position. Returns false, otherwise.
    private static boolean isLightInPosition(List<Light> lights, Position p) {
        for (Light light : lights) {
            if (light.position.equals(p))
                return true;
        }
        return false;
    }

    // Given a list of lights, prints all the positions of the lights to the console in a visual grid.
    // A cell with a light is denoted with a '#'. Otherwise, a cell without a light is denoted with a space.
    private static void printLights(List<Light> lights) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        // Determine the bounding box of the lights.
        for (Light light : lights) {
            minX = Math.min(light.position.x, minX);
            maxX = Math.max(light.position.x, maxX);
            minY = Math.min(light.position.y, minY);
            maxY = Math.max(light.position.y, maxY);
        }

        // Iterate through the cells of the bounding box, printing a '#' if that cell is occupied by a light
        // and printing a space if that cell is not occupied.
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (isLightInPosition(lights, new Position(x,y))) {
                    System.out.print("#");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    // Given a list of lights, adds each light's velocity to its position.
    private static void step(List<Light> lights) {
        for (Light light : lights) {
            light.position.x = light.position.x + light.velocity.x;
            light.position.y = light.position.y + light.velocity.y;
        }
    }

    // Given a list of lights, subtracts each light's velocity from its position.
    private static void reverseStep(List<Light> lights) {
        for (Light light : lights) {
            light.position.x = light.position.x - light.velocity.x;
            light.position.y = light.position.y - light.velocity.y;
        }
    }

    // Returns the range of y positions in the given list of lights.
    private static int rowSpread(List<Light> lights) {
        List<Integer> yPositions = new ArrayList<>();

        for (Light light : lights) {
            yPositions.add(light.position.y);
        }

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Integer i : yPositions) {
            minY = Math.min(minY, i);
            maxY = Math.max(maxY, i);
        }

        return maxY - minY;
    }

    // Part 1 and 2: We find the message in the night sky by applying the respective velocity vectors
    // of the lights to their positions. We simulate each step of this in a while loop.
    // In order to determine when to step, we make an observation/assumption about the nature of the "bounding box"
    // (the box formed by the min/max x and y values) of the lights. We observe that in order for the message
    // to be properly formed, the spread/range of y-values needs to be at a minimum. Therefore, we assume
    // that in the beginning, the range of y values will be ever decreasing until the message is formed, after which
    // the range will begin increasing again. Therefore, we iterate until we find a minimum range of y-values.
    private static int part1and2(List<Light> lights) {
        int previousRowSpread = Integer.MAX_VALUE;
        int seconds = 0; // Keep track of the number of seconds that have passed. Used in part 2.

        // Keep iterating until we determine that our range of y-values is now increasing.
        while (true) {
            step(lights);

            // Check if the range of values is now increasing.
            int rowSpread = rowSpread(lights);
            if (rowSpread > previousRowSpread) {
                reverseStep(lights); // If so, we've stepped one second too far, so reverse the lights once.
                break;
            }

            previousRowSpread = rowSpread;
            seconds++;
        }

        printLights(lights);
        return seconds;
    }


    // Class representing a point of light in the night sky. Each light has a position and a velocity.
    static class Light {
        Position position;
        Velocity velocity;

        public Light(Position position, Velocity velocity) {
            this.position = position;
            this.velocity = velocity;
        }
    }

    // Class representing the position of a point of light in the night sky.
    static class Position {
        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "P(" + x + "," + y + ")";
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || o.getClass() != this.getClass()) return false;
            Position p = (Position)o;
            return (x == p.x && y == p.y);
        }

        public int hashCode() {
            return Objects.hash(x,y);
        }
    }

    // Class representing the veloctiy of a point of light in the night sky.
    static class Velocity {
        int x;
        int y;

        public Velocity(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String Velocity() {
            return "V(" + x + "," + y + ")";
        }
    }
}
