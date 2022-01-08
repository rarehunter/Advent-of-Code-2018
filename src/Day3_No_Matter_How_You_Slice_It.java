import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day3_No_Matter_How_You_Slice_It {
    public static void main(String[] args) {
        File file = new File("./inputs/day3/day3.txt");

        try {
            Scanner sc = new Scanner(file);
            List<Fabric> fabrics = new ArrayList<>();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(" ");
                int id = Integer.parseInt(tokens[0].substring(1));
                String[] offsets = tokens[2].split(",");
                int left = Integer.parseInt(offsets[0]);
                int top = Integer.parseInt(offsets[1].substring(0,offsets[1].length()-1));
                String[] dimensions = tokens[3].split("x");
                int width = Integer.parseInt(dimensions[0]);
                int height = Integer.parseInt(dimensions[1]);
                fabrics.add(new Fabric(id, left, top, width, height));
            }

            int part1 = part1(fabrics);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(fabrics);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Return the square inches where two or more fabrics overlap.
    private static int countOverlaps(char[][] grid) {
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 'X') {
                    count++;
                }
            }
        }

        return count;
    }

    // Given a grid and a fabric object, fills in the grid accordingly where the fabric should go.
    // If the fabric placement overlaps an existing fabric, replaces the cell with an 'X'. Otherwise,
    // changes the cell to a '*'.
    private static void placeFabric(char[][] grid, Fabric fabric) {
        for (int i = fabric.top; i < fabric.top + fabric.height; i++) {
            for (int j = fabric.left; j < fabric.left + fabric.width; j++) {
                if (grid[i][j] == 'X' || grid[i][j] == '*') {
                    grid[i][j] = 'X';
                } else {
                    grid[i][j] = '*';
                }
            }
        }
    }

    // Part 1: Iterate through the list of fabrics and fill in a grid with fabric values: a 'X' indicating
    // that at least two fabrics have been placed there and a '*' indicating that a single fabric is placed there.
    // Return the count of all 'X' values.
    private static int part1(List<Fabric> fabrics) {
        char[][] grid = new char[1000][1000];

        for (Fabric fabric : fabrics) {
            placeFabric(grid, fabric);
        }

        return countOverlaps(grid);
    }

    // Given a grid and a fabric object, fills in the grid accordingly where the fabric
    // should go, using the fabric's id as the filler. Overlaps are denoted by -1
    private static void placeFabricInt(int[][] grid, Fabric fabric) {
        for (int i = fabric.top; i < fabric.top + fabric.height; i++) {
            for (int j = fabric.left; j < fabric.left + fabric.width; j++) {
                if (grid[i][j] != 0) {
                    grid[i][j] = -1;
                } else {
                    grid[i][j] = fabric.id;
                }
            }
        }
    }

    // Part 2:
    private static int part2(List<Fabric> fabrics) {
        int[][] grid = new int[1000][1000];

        for (Fabric fabric : fabrics) {
            placeFabricInt(grid, fabric);
        }

        Map<Integer, Integer> freq = new HashMap<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (freq.containsKey(grid[i][j])) {
                    freq.put(grid[i][j], freq.get(grid[i][j])+1);
                } else {
                    freq.put(grid[i][j], 1);
                }
            }
        }

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            int fabricId = entry.getKey();
            int num = entry.getValue();

            Fabric found = null;
            for (Fabric f : fabrics) {
                if (f.id == fabricId) {
                    found = f;
                }
            }

            if (found != null && found.width * found.height == num) {
                return fabricId;
            }
        }

        return 0;
    }

    static class Fabric {
        int id;
        int top;
        int left;
        int width;
        int height;

        public Fabric(int id, int left, int top, int width, int height) {
            this.id = id;
            this.top = top;
            this.left = left;
            this.width = width;
            this.height = height;
        }

        public String toString() {
            return "(" + this.id + "," + this.left + "," + this.top + "," + this.width + "," + this.height + ")";
        }
    }
}