
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Day6_Chronal_Coordinates {
    public static void main(String[] args) {
        File file = new File("./inputs/day6/day6.txt");

        try {
            Scanner sc = new Scanner(file);
            List<Point> points = new ArrayList<>();
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = 0;
            int maxY = 0;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(",");
                int x = Integer.parseInt(tokens[0].trim());
                int y = Integer.parseInt(tokens[1].trim());
                points.add(new Point(x, y));

                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }

            // Find the min/max x values and the min/max y values to form a
            // "bounding box" around the possible points.
            Pair xBounds = new Pair(minX, maxX);
            Pair yBounds = new Pair(minY, maxY);

            int part1 = part1(points, xBounds, yBounds);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(points, xBounds, yBounds);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Returns the Manhattan distance between the two given points.
    private static int manhattanDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    // Returns the unique point in our list of points that is the closest (based off of Manhattan distance)
    // to the given point p. If more than one point is equally close, returns null.
    private static Point findClosestPoint(List<Point> points, Point p) {
        // Maps a distance to a list of points with that distance
        Map<Integer, List<Point>> freq = new HashMap<>();

        for (Point point : points) {
            int distance = manhattanDistance(point, p);
            if (freq.containsKey(distance)) {
                List<Point> theList = freq.get(distance);
                theList.add(point);
                freq.put(distance, theList);
            } else {
                List<Point> newList = new ArrayList<>();
                newList.add(point);
                freq.put(distance, newList);
            }
        }

        int minDistance = Integer.MAX_VALUE;
        for (Integer distance : freq.keySet()) {
            if (distance < minDistance) {
                minDistance = distance;
            }
        }

        // Check if the min distance we've found is unique. if so, return the point.
        // Otherwise, return null.
        return freq.get(minDistance).size() == 1 ? freq.get(minDistance).get(0) : null;
    }

    // Part 1: Finds the size of the largest area that isn't infinite. Iterates through all points in the "grid",
    // and finds the closest point to that point. If a unique closest point is find, store it in a dictionary that
    // maps a given point to the size of the area of points closest to that point. Afterwards, iterate through the
    // points along the border of the "bounding box" and remove any points from the dictionary. The reason for this
    // is that points along the border can be treated as infinite areas, and we don't want to count those.
    // Finally, determine the largest area of the points in our dictionary remaining.
    private static int part1(List<Point> points, Pair xBounds, Pair yBounds) {
        Map<Point, Integer> areas = new HashMap<>();

        // For every coordinate in our "bounding box" (bounded by min/max x and min/max y values),
        // find the closest point to each coordinate.
        for (int x = xBounds.min; x <= xBounds.max; x++) {
            for (int y = yBounds.min; y <= yBounds.max; y++) {
                Point closest = findClosestPoint(points, new Point(x,y));
                if (closest != null) {
                    if (areas.containsKey(closest)) {
                        areas.put(closest, areas.get(closest) + 1);
                    } else {
                        areas.put(closest, 1);
                    }
                }
            }
        }

        // Now that we have a dictionary with the number of points closest to each point,
        // we need to determine which points to exclude because they are on the border and hence,
        // are actually infinite areas. Iterate through the points on the border and find the closest point to them.
        // Remove those points from the dictionary to not consider them.
        for (int x = xBounds.min; x <= xBounds.max; x++) {
            Point topBorderPoint = findClosestPoint(points, new Point(x, yBounds.min));
            Point bottomBorderPoint = findClosestPoint(points, new Point(x, yBounds.max));
            areas.remove(topBorderPoint);
            areas.remove(bottomBorderPoint);
        }

        for (int y = yBounds.min; y <= yBounds.max; y++) {
            Point leftBorderPoint = findClosestPoint(points, new Point(xBounds.min, y));
            Point rightBorderPoint = findClosestPoint(points, new Point(xBounds.max, y));
            areas.remove(leftBorderPoint);
            areas.remove(rightBorderPoint);
        }

        int maxArea = 0;

        // Finally, from the remaining points in the dictionary,
        // return the one that has the max area.
        for (Point p : areas.keySet()) {
            if (areas.get(p) > maxArea)
                maxArea = areas.get(p);
        }

        return maxArea;
    }

    // Given a list of points and a point, determines the sum of all the Manhattan distances between the point
    // and each point in the list.
    private static int findManhattanDistanceSum(List<Point> points, Point p) {
        int sum = 0;
        for (Point point : points) {
            int distance = manhattanDistance(point, p);
            sum += distance;
        }

        return sum;
    }

    // Part 2: Finds the size of the region containing all locations which have a
    // total distance to all given points of less than 10,000. Iterate through all points in the "grid",
    // and determine the sum of the Manhattan distances from this point to the list of given points.
    // If that sum is less than 10,000, accumulate a variable which keeps track of the size of this region.
    private static int part2(List<Point> points, Pair xBounds, Pair yBounds) {
        int regionSize = 0;
        for (int x = xBounds.min; x <= xBounds.max; x++) {
            for (int y = yBounds.min; y <= yBounds.max; y++) {
                int sum = findManhattanDistanceSum(points, new Point(x,y));

                if (sum < 10000)
                    regionSize++;
            }
        }

        return regionSize;
    }

    // Class to represent a pair of min and max bounds.
    static class Pair {
        int min;
        int max;

        public Pair(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }
}