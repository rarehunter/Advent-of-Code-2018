import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day1_Chronal_Calibration {
    public static void main(String[] args) {
        File file = new File("./inputs/day1/day1.txt");

        try {
            Scanner sc = new Scanner(file);

            List<String> instructions = new ArrayList<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                instructions.add(line);
            }

            int part1 = part1(instructions);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(instructions);
            System.out.println("Part 1 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Part 1: Iterates through the list of instructions, applying changes to a result variable.
    private static int part1(List<String> instructions) {
        int result = 0;

        for (String instruction : instructions) {
            char firstChar = instruction.charAt(0);
            int num = Integer.parseInt(instruction.substring(1));
            if (firstChar == '+') {
                result += num;
            } else if (firstChar == '-') {
                result -= num;
            }
        }

        return result;
    }

    // Part 2: Keep a set of frequencies that we've seen. Apply each instruction to our result,
    // storing it in our set if we haven't seen it yet. As soon as we've seen a frequency before, we break
    // and return that frequency.
    private static int part2(List<String> instructions) {
        Set<Integer> frequencies = new HashSet<>();

        int result = 0;
        frequencies.add(result);
        int i = 0;
        while (true) {
            String instruction = instructions.get(i % instructions.size());
            char firstChar = instruction.charAt(0);
            int num = Integer.parseInt(instruction.substring(1));

            if (firstChar == '+')
                result += num;
            else if (firstChar == '-')
                result -= num;

            if (frequencies.contains(result))
                break;
            else
                frequencies.add(result);

            i++;
        }

        return result;
    }
}