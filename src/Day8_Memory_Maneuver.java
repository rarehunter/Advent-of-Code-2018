import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day8_Memory_Maneuver {
    public static void main(String[] args) {
        File file = new File("./inputs/day8/day8.txt");

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

            int part1 = part1(license);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(license);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Given a list of integers (license), a beginning index (begin),
    // and the number of metadata entries (numMetadata), sums up the values in the list
    // beginning at the given index until the number of metadata entries have been summed.
    private static int sumMetadata(List<Integer> license, int numMetadata, int begin) {
        if (numMetadata == 0) return 0;

        int sum = 0;
        for (int i = begin; i < begin + numMetadata; i++) {
            sum += license.get(i);
        }

        return sum;
    }

    // Recursive function to recursively sum up metadata entries of all the "chunks" in the list of integers.
    // The base case is when the number of children is 0 in which case we just return the sum of its metadata
    // entries and the ending index.
    // Otherwise, we iterate through each child "chunk", recursing and summing as we go, and using the ending index
    // of the previous chunk to inform the beginning index of the next "chunk".
    private static Pair1 sumMetadataEntries(List<Integer> license, int begin) {
        int numChildren = license.get(begin);
        int numMetadata = license.get(begin+1);

        // No more child nodes, so sum up any metadata entries, calculate ending index, and return.
        if (numChildren == 0) {
            int sum = sumMetadata(license, numMetadata, begin + 2);
            return new Pair1(sum, begin + numMetadata + 1);
        }

        // Otherwise, we have to start iterating through the list.
        // We start by assigning the beginning index of the next child "chunk".
        int beginningIndex = begin + 2;
        int sum = 0;

        // Then, for each child, we recurse. We accumulate the sum found and the beginning index
        // of the next "chunk" is the given ending index + 1.
        for (int i = 0; i < numChildren; i++) {
            Pair1 p = sumMetadataEntries(license, beginningIndex);
            sum += p.sum;
            beginningIndex = p.endingIndex + 1;
        }

        // Once all child "chunks" have been processed, we calculate the sum of the current "chunk"'s own
        // metadata entries.
        int myMetadataSum = sumMetadata(license, numMetadata, beginningIndex);

        // Return the tuple of the sum of this "chunk" (which is the sum of its own entries plus the sum of
        // all its child "chunk" entries) AND the ending index of this current "chunk"
        return new Pair1(sum + myMetadataSum, beginningIndex+numMetadata-1);
    }

    // Part 1: Use a recursive function to sum up metadata entries of all the "chunks" in the list of integers.
    private static int part1(List<Integer> license) {
        Pair1 p = sumMetadataEntries(license, 0);
        return p.sum;
    }

    // Recursive function to recursively calculate a node's value.
    // The base case is when the number of children is 0 in which case the node's value is the sum
    // of its metadata entries. We return that value and the ending index of this chunk.
    // Otherwise, we iterate through each child "chunk", recursing, storing each child node value in a list.
    // The ending index of the previous chunk informs the beginning index of the next "chunk".
    private static Pair2 calculateNodeValue(List<Integer> license, int begin) {
        int numChildren = license.get(begin);
        int numMetadata = license.get(begin+1);

        // No more child nodes, so the node's value is the sum of its metadata entries.
        if (numChildren == 0) {
            int sum = sumMetadata(license, numMetadata, begin + 2);
            return new Pair2(sum, begin + numMetadata + 1);
        }

        // Otherwise, we have to start iterating through the license file.
        // We start by assigning the beginning index of the next child "chunk".
        int beginningIndex = begin + 2;

        // Then, for each child, we recurse. We store the node value that we get back and the beginning index
        // of the next "chunk" is the given ending index + 1.
        List<Integer> childNodeValues = new ArrayList<>();
        for (int i = 0; i < numChildren; i++) {
            Pair2 p = calculateNodeValue(license, beginningIndex);
            childNodeValues.add(p.value);
            beginningIndex = p.endingIndex + 1;
        }

        // Once all child "chunks" have been processed, we look at the value of the current "chunk"'s own
        // metadata entries. We iterate through each metadata value and check if it is a valid "index"
        // into the number of children this current "chunk" has. We sum up all the child "chunk"'s values
        // as informed by the metadata values.
        int nodeValue = 0;
        for (int i = beginningIndex; i < beginningIndex + numMetadata; i++) {
            int metadataValue = license.get(i);
            if (metadataValue <= numChildren && metadataValue >= 1) {
                nodeValue += childNodeValues.get(metadataValue-1);
            }
        }

        // Return the tuple of the value of this "chunk" and the ending index of this current "chunk"
        return new Pair2(nodeValue, beginningIndex+numMetadata-1);
    }

    // Part 2: Finds the value of the root node. If a node has no children, the value of that node is the sum
    // of its metadata entries. If a node does have children, its metadata entries are used as indices to select
    // which of its children's metadata to sum up. Uses a recursive function to calculate values of all "chunks"
    // and "subchunks" in the license file.
    private static int part2(List<Integer> license) {
        Pair2 p = calculateNodeValue(license, 0);
        return p.value;
    }

    // Class to bundle the sum of metadata entries of a node and
    // the ending index of the node in the license file.
    static class Pair1 {
        int sum;
        int endingIndex;

        public Pair1(int sum, int endingIndex) {
            this.sum = sum;
            this.endingIndex = endingIndex;
        }
    }

    // Class to bundle the value of a node and
    // the ending index of the node in the license file.
    static class Pair2 {
        int value;
        int endingIndex;

        public Pair2(int value, int endingIndex) {
            this.value = value;
            this.endingIndex = endingIndex;
        }
    }
}