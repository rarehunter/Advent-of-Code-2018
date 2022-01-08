import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day5_Alchemical_Reduction {
    public static void main(String[] args) {
        File file = new File("./inputs/day5/day5.txt");

        try {
            Scanner sc = new Scanner(file);

            String polymer = "";
            while (sc.hasNextLine()) {
                polymer = sc.nextLine();
            }

            int part1 = part1(polymer);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(polymer);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Returns true if the given characters react with each other (if they are the lowercase and uppercase version
    // of the same character). Returns false otherwise. Note: 32 is the difference in ASCII values between
    // uppercase and lowercase characters.
    private static boolean reacts(char a, char b) {
        return (Math.abs((int)a - (int)b) == 32 &&
            ((Character.isUpperCase(a) && Character.isLowerCase(b)) ||
            (Character.isUpperCase(b) && Character.isLowerCase(a))));
    }

    // Given a polymer string and two characters to ignore, iterates through the characters
    // of the polymer and removes adjacent pairs of uppercase and lowercase letters of the same letter.
    // (Only used in part 2: If an uppercase/lowercase char is given, ignores such chars from the polymer)
    private static int reactPolymer(String polymer, char uppercase, char lowercase) {
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < polymer.length(); i++) {
            char c = polymer.charAt(i);
            if (c == uppercase || c == lowercase) continue;

            if (!stack.isEmpty()) {
                Character top = stack.peek();
                if (reacts(c, top)) {
                    stack.pop();
                } else {
                    stack.push(c);
                }
            } else {
                stack.push(c);
            }
        }

        return stack.size();
    }

    // Part 1: Iterate through the characters of the polymer.
    // Use a stack to keep track of the characters currently in the polymer.
    // Every time the next character of the polymer is considered, check if the top of the stack contains
    // a character that is the same letter but of opposite case. If so, those two characters "destroy" each other.
    // Otherwise, add it to the stack and continue on. The final answer is the length of the polymer remaining
    // after all characters have "reacted" with each other.
    private static int part1(String polymer) {
        // \u0000 is the null character (default char value)
        return reactPolymer(polymer, '\u0000' , '\u0000');
    }

    // Part 2: Go through all pairs of uppercase and lowercase alphabet letters and run the polymer
    // algorithm from part 1 on it, removing those characters from the polymer if encountered.
    // Return the min length of the resulting polymer.
    private static int part2(String polymer) {
        int min = Integer.MAX_VALUE;
        // 65 equal 'A' and 90 = 'Z'
        // 32 is the difference between uppercase letters and lowercase letters in ASCII.
        for (int i = 65; i <= 90; i++) {
            char uppercaseLetter = (char) i;
            char lowercaseLetter = (char) (i + 32);

            int length = reactPolymer(polymer, uppercaseLetter, lowercaseLetter);
            if (length < min)
                min = length;
        }

        return min;
    }
}