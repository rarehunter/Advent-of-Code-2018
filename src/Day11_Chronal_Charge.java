import java.awt.Point;
import java.util.*;

public class Day11_Chronal_Charge {
    private static final int GRID_SERIAL_NUMBER = 8141;
    private static final int GRID_WIDTH = 300;
    private static final int GRID_HEIGHT = 300;

    public static void main(String[] args) {
        Point maxPoint = part1();
        System.out.println("Part 1 is: (" + maxPoint.x + "," + maxPoint.y + ")");

        FuelCellSquare maxSquare = part2WithSummedAreaTable();
        System.out.println("Part 2 is: (" + maxSquare.point.x + "," + maxSquare.point.y + "," + maxSquare.size + ")");
    }

    private static void print(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    // Returns the hundred digit of a given number.
    // If the number has no hundreds digit, returns 0.
    // E.g. 345 => 3
    // E.g. 162202 => 2
    // E.g. 56 => 0
    private static int hundredsDigit(int n) {
        return (n / 100) % 10;
    }

    // Given the x and y coordinates of a fuel cell, calculate the power level of that cell.
    private static int calculateFuelCellPowerLevel(int x, int y) {
        int rackId = x + 10;
        int powerLevel = rackId * y;
        powerLevel += GRID_SERIAL_NUMBER;
        powerLevel *= rackId;
        powerLevel = hundredsDigit(powerLevel);
        powerLevel -= 5;
        return powerLevel;
    }

    // Given the x and y coordinates of the top left corner of a 3x3 square of fuel cells,
    // returns the sum of all the power levels of the fuel cells within.
    private static int calculate3x3PowerLevel(int x, int y, int[][] grid) {
        return grid[y][x] + grid[y][x+1] + grid[y][x+2] +
                grid[y+1][x] + grid[y+1][x+1] + grid[y+1][x+2] +
                grid[y+2][x] + grid[y+2][x+1] + grid[y+2][x+2];
    }

    // Part 1: Returns a point representing the (x,y) coordinate of the top-left point of a 3x3 square
    // with the largest total power. Iterates through each 3x3 square in our 300x300 grid and calculates
    // the sum of the power levels of the 9 cells within each 3x3 square. Stores all such sums in a list
    // and returns the point associated with the largest sum.
    private static Point part1() {
        int[][] grid = new int[GRID_HEIGHT][GRID_WIDTH];

        // Populate each cell of a grid with its power level.
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                grid[y][x] = calculateFuelCellPowerLevel(x,y);
            }
        }

        // Track the max square seen so far.
        FuelCellSquare maxSquare = new FuelCellSquare(null, Integer.MIN_VALUE, 3);

        // Iterate through each 3x3 square in the grid, calculate its sum,
        // and keep track of the max square seen so far.
        for (int x = 0; x < GRID_HEIGHT - 2; x++) {
            for (int y = 0; y < GRID_WIDTH - 2; y++) {
                int powerLevelOfSquare = calculate3x3PowerLevel(x, y, grid);

                if (powerLevelOfSquare > maxSquare.powerLevel) {
                    maxSquare.powerLevel = powerLevelOfSquare;
                    maxSquare.point = new Point(x,y);
                }
            }
        }

        return maxSquare.point;
    }

    private static int calculateRightBottomBorderSum(Point coordinate, int size, Map<Point,Integer> powerLevelsForSizeOne) {
        int sum = 0;

        // Start by summing up the right border cells from top to bottom, including the bottom-right corner cell.
        for (int y = coordinate.y; y <= coordinate.y+size-1; y++) {
            sum += powerLevelsForSizeOne.get(new Point(coordinate.x+size-1, y));
        }

        // Next, sum up the bottom border cells from left to right, excluding the bottom-right corner cell.
        for (int x = coordinate.x; x <= coordinate.x + size - 2; x++) {
            sum += powerLevelsForSizeOne.get(new Point(x, coordinate.y+size-1));
        }

        return sum;
    }

    // Part 2: For each square size (1..300), keep track of a HashMap mapping a point in the grid to its power level sum.
    // By iterating through each square size from smallest to largest, we incrementally build up these partial sums.
    // If an even square size is needed, we can find the power level sum by adding up 4 half-sized squares.
    // If an odd square size is needed, we can find the power level sum by using the power level sum of the square size
    // that is one less and the sum of the right and bottom border cell's power levels. Takes about 30 sec to run.
    private static FuelCellSquare part2() {
        // Stores a HashMap for each square size. Each square size has a HashMap storing the top-left coordinate
        // and its corresponding power level sum.
        Map<Integer, Map<Point, Integer>> powerLevels = new HashMap<>();

        // For the initial size of 1, we need to populate our map.
        Map<Point, Integer> powerLevelSizeOne = new HashMap<>();
        for (int y = 1; y <= GRID_HEIGHT; y++) {
            for (int x = 1; x <= GRID_WIDTH; x++) {
                powerLevelSizeOne.put(new Point(x,y), calculateFuelCellPowerLevel(x,y));
            }
        }
        powerLevels.put(1, powerLevelSizeOne);

        // Keep track of the max power level seen so far.
        FuelCellSquare maxFuelCellSquare = new FuelCellSquare(null, Integer.MIN_VALUE, 1);

        // Iterate through the rest of the square sizes.
        // If a square size is even, its sum can be calculated by adding up the power levels of squares of size / 2.
        // If a square size is odd, its sum can be calculated by adding up the power level of the square of size - 1
        // and the sums of the fuel cells of its right and bottom border.
        for (int size = 2; size <= 300; size++) {
            boolean isEvenSize = size % 2 == 0;

            Map<Point, Integer> newSizeMap = new HashMap<>();
            Map<Point, Integer> smallerSizeMap = isEvenSize
                    ? powerLevels.get(size / 2)
                    : powerLevels.get(size-1);

            for (int y = 1; y <= GRID_HEIGHT - size + 1; y++) {
                for (int x = 1; x <= GRID_WIDTH - size + 1; x++) {
                    Point coordinate = new Point(x, y);

                    int sum;
                    if (isEvenSize) {
                        // For square sizes that are even, the new sum will always
                        // be four sums of half-sized squares.
                        sum = smallerSizeMap.get(new Point(x, y)) +
                                smallerSizeMap.get(new Point(x + (size / 2), y)) +
                                smallerSizeMap.get(new Point(x, y + (size / 2))) +
                                smallerSizeMap.get(new Point(x + (size / 2), y + (size / 2)));
                    } else {
                        // For square sizes that are odd, the new sum is the power level of the square size that is
                        // one smaller and the sum of the right and bottom borders.
                        int sumOfBorders = calculateRightBottomBorderSum(coordinate, size, powerLevels.get(1));
                        sum = sumOfBorders + smallerSizeMap.get(coordinate);
                    }

                    newSizeMap.put(coordinate, sum);

                    // See if we've found a new max sum
                    if (sum > maxFuelCellSquare.powerLevel) {
                        maxFuelCellSquare.powerLevel = sum;
                        maxFuelCellSquare.size = size;
                        maxFuelCellSquare.point = coordinate;
                    }
                }
            }

            powerLevels.put(size, newSizeMap);
        }

        return maxFuelCellSquare;
    }

    // Given a grid G of values, return its summed-area table SAT. A summed-area table is a 2d array where
    // SAT[i,j] is the sum of all cells G[a,b] where 0 <= a <= i and 0 <= b <= j.
    // SAT[i,j] can be calculated by considering the cell immediately above it
    // and the cell immediately to the left of it. More specifically:
    // SAT[i,j] = SAT[i-1,j] + SAT[i,j-1] + G[i,j] - SAT[i-1,j-1]
    private static int[][] calculateSummedAreaTable(int[][] grid) {
        int[][] summedAreaTable = new int[GRID_HEIGHT][GRID_WIDTH];

        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                int topValue = y-1 >= 0 ? summedAreaTable[y-1][x] : 0;
                int leftValue = x-1 >= 0 ? summedAreaTable[y][x-1] : 0;
                int diagonalValue = x-1 >= 0 && y-1 >= 0 ? summedAreaTable[y-1][x-1] : 0;
                summedAreaTable[y][x] = topValue + leftValue + grid[y][x] - diagonalValue;
            }
        }

        return summedAreaTable;
    }

    // This is an attempt to re-implement part 2 but using a summed-area table for further optimization.
    // Takes about 50ms to run.
    private static FuelCellSquare part2WithSummedAreaTable() {
        int[][] grid = new int[GRID_HEIGHT][GRID_WIDTH];

        // Populate each cell of a grid with its power level.
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                grid[y][x] = calculateFuelCellPowerLevel(x,y);
            }
        }

        // Construct a summed-area table where each cell i,j in this table is the
        // sum of all cells <= i and all cells <= j in the grid.
        int[][] sat = calculateSummedAreaTable(grid);

        FuelCellSquare maxSquare = new FuelCellSquare(null, Integer.MIN_VALUE, 1);

        // Iterate through all square sizes. Iterate through each cell. Each cell is considered to be the top-left
        // corner of a square. The sum of that square is calculated by using the summed-area table (SAT).
        // The square sum is calculated: SAT[i,j] - SAT[i-1,j] - SAT[i,j-1] + SAT[i-1,j-1]
        for (int size = 1; size <= 300; size++) {
            for (int y = 0; y < GRID_HEIGHT - size + 1; y++) {
                for (int x = 0; x < GRID_WIDTH - size + 1; x++) {
                    int topSum = y-1 >= 0 ? sat[y-1][x+size-1] : 0;
                    int leftSum = x-1 >= 0 ? sat[y+size-1][x-1] : 0;
                    int diagonalSum = x-1 >= 0 && y-1 >= 0 ? sat[y-1][x-1] : 0;
                    int squareSum = sat[y+size-1][x+size-1] - topSum - leftSum + diagonalSum;

                    // Keep track of the max power level seen so far.
                    if (squareSum > maxSquare.powerLevel) {
                        maxSquare.powerLevel = squareSum;
                        maxSquare.size = size;
                        maxSquare.point = new Point(x,y);
                    }
                }
            }
        }

        return maxSquare;
    }

    // Class to associate a top-left point of an n x n square of fuel cells
    // with the sum of the power levels of all n^2 of its fuel cells and the size of n.
    static class FuelCellSquare {
        int size;
        Point point; // top-left point of an n x n square of fuel cells
        int powerLevel; // sum of power level of all n^2 of its fuel cells

        public FuelCellSquare(Point point, int powerLevel, int size) {
            this.size = size;
            this.point = point;
            this.powerLevel = powerLevel;
        }
    }
}