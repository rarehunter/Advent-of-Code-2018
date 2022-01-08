import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day2_Inventory_Management_System {
    public static void main(String[] args) {
        File file = new File("./inputs/day2/day2.txt");

        try {
            Scanner sc = new Scanner(file);

            List<String> boxes = new ArrayList<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                boxes.add(line);
            }

            int part1 = part1(boxes);
            System.out.println("Part 1 is: " + part1);

            String part2 = part2(boxes);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Part 1: Iterate through each box ID, keeping track of a dictionary of character frequencies.
    // Check each frequency mapping and increment a count of box IDs that have exactly two characters or
    // exactly three characters.
    private static int part1(List<String> boxes) {
        int exactlyTwo = 0;
        int exactlyThree = 0;

        Map<Character, Integer> frequency = new HashMap<>();
        for (String box : boxes) {
            for (int i = 0; i < box.length(); i++) {
                char c = box.charAt(i);
                if (frequency.containsKey(c)) {
                    frequency.put(c, frequency.get(c) + 1);
                } else {
                    frequency.put(c, 1);
                }
            }

            if (frequency.values().contains(2))
                exactlyTwo++;
            if (frequency.values().contains(3))
                exactlyThree++;

            frequency.clear();
        }

        return exactlyTwo * exactlyThree;
    }

    // If the two strings differ by exactly one character at the same index in both strings,
    // returns that index. Otherwise, if both strings have no differing characters or if both strings
    // differ by more than one character at any index, returns -1.
    private static int stringDifferIndex(String b1, String b2) {
        boolean differed = false;
        int differIndex = -1;

        for (int i = 0; i < Math.min(b1.length(), b2.length()); i++) {
            char c1 = b1.charAt(i);
            char c2 = b2.charAt(i);

            if (c1 != c2) {
                // we found two differing characters
                if (differed) {
                    return -1;
                } else {
                    differIndex = i;
                    differed = true;
                }
            }
        }

        return differIndex;
    }

    // Part 2: Iterate through all pairs of box IDs and checking the strings differ by exactly one character
    // in one location. Return a string of the characters in common.
    private static String part2(List<String> boxes) {
        for (int i = 0; i < boxes.size()-1; i++) {
            for (int j = i+1; j < boxes.size(); j++) {
                int index = stringDifferIndex(boxes.get(i), boxes.get(j));
                if (index != -1) {
                    return boxes.get(i).substring(0,index) + boxes.get(i).substring(index+1);
                }
            }
        }

        return "";
    }
}