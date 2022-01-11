import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day8_Memory_Maneuver {
    public static void main(String[] args) {
        File file = new File("./inputs/day8/day8.example.txt");

        try {
            Scanner sc = new Scanner(file);
            List<Integer> license = new ArrayList<>();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(" ");
                for (String token : tokens) {
                    license.add(Integer.parseInt(token));
                }
            }

            System.out.println(license);

            int part1 = part1();
            System.out.println("Part 1 is: " + part1);

            int part2 = part2();
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Part 1:
    private static int part1() {
        return 0;
    }

    // Part 2:
    private static int part2() {
        return 0;
    }

    static class Node {
        int numChildren;
        int numMetadata;
        List<Integer> metadata;
        List<Node> children;

        public Node(int numChildren, int numMetadata) {
            this.numChildren = numChildren;
            this.numMetadata = numMetadata;
            children = new ArrayList<>(numChildren);
            metadata = new ArrayList<>(numMetadata);
        }
    }
}