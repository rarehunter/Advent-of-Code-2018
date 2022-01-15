import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day9_Marble_Mania {
    public static void main(String[] args) {
       File file = new File("./inputs/day9/day9.txt");

        try {
            Scanner sc = new Scanner(file);
            int numPlayers = 0;
            int lastMarble = 0;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(" ");
                numPlayers = Integer.parseInt(tokens[0]);
                lastMarble = Integer.parseInt(tokens[6]);
            }

            int part1 = part1(numPlayers, lastMarble);
            System.out.println("Part 1 is: " + part1);

            long part2 = part2(numPlayers, lastMarble * 100);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Part 1: Given the number of players and the last marble number, simulate the marble game
    // and return the maximum score.
    private static int part1(int numPlayers, int lastMarble) {
        int[] scores = new int[numPlayers]; // keeps track of the scores of all the players

        // Simulate the first two placements of marbles.
        List<Integer> circle = new ArrayList<>();
        circle.add(0);
        circle.add(1);

        int currentMarble = 1; // index of the current marble
        int currentPlayer = 2; // index of the current player
        int marbleNumber = 2; // Start with the marble 2.

        while(marbleNumber <= lastMarble) {
            // If the marble to be placed is a multiple of 23, the current player keeps the marble
            // they would have placed, adding it to their score.
            // Then, the marble 7 marbles left of the current marble is removed from the circle
            // and also added to the current player's score. The marble located immediately right
            // of the marble that was removed becomes the new current marble.
            if (marbleNumber % 23 == 0) {
                int marbleToRemove = (currentMarble - 7) % circle.size();
                // make sure that the index of the marble to remove is between 0 and circle size
                if (marbleToRemove < 0)
                    marbleToRemove = marbleToRemove + circle.size();

                int removedMarble = circle.remove(marbleToRemove);
                scores[currentPlayer] += (removedMarble + marbleNumber);
                currentMarble = marbleToRemove;
            } else {
                // For any other marble number, place the marble two spaces to the right of the current marble,
                // wrapping around if needed.
                int newIndex = 0;
                if (currentMarble + 2 == circle.size())
                    newIndex = circle.size();
                else
                    newIndex = (currentMarble + 2) % circle.size();

                currentMarble = newIndex;
                circle.add(newIndex, marbleNumber);
            }

            marbleNumber++;
            currentPlayer = (currentPlayer + 1) % numPlayers; // move to the next player
        }

        // Finally, calculate the max score among all the players and return it.
        int maxScore = 0;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore)
                maxScore = scores[i];
        }

        return maxScore;
    }

    // Part 2: Using an ArrayList (which internally uses an array) in part 1 is very slow.
    // Specifically, any insertions into the middle of our ArrayList will require that all elements
    // to the right of it need to shifted to the right by one and any removals from our ArrayList will require
    // that all elements to the right of it need to be shifted to the left by one.
    // This can be expensive given a large enough array. Because we need to simulate the marble circle game for tens
    // of millions of marbles, we need a data structure that gives us O(1) insertion and removal.
    // The Java built-in LinkedList class is a singly LinkedList which doesn't provide us the ability
    // to look "backwards" by 7 marbles (that we need) nor the ability to store a pointer to the node
    // (i.e. the current marble) so as to prevent iterating over the entire list to determine the position to
    // insert the next marble. Therefore, we need to solve this by implementing our own circular doubly-linked list.
    private static long part2(int numPlayers, int lastMarble) {
        long[] scores = new long[numPlayers]; // keeps track of the scores of all the players

        // Initialize our circular DLL to store the game.
        MarbleCircleGame game = new MarbleCircleGame();
        int currentPlayer = 0; // index of the current player
        int marbleNumber = 0;

        while(marbleNumber <= lastMarble) {
            int score = game.addNextMarble(marbleNumber);

            // update the score of the current player and move to the next player.
            scores[currentPlayer] += score;
            currentPlayer = (currentPlayer + 1) % numPlayers;

            marbleNumber++;
        }

        // Finally, find the max score among all the players and return it.
        long maxScore = 0L;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore)
                maxScore = scores[i];
        }

        return maxScore;
    }

    // Class representing a marble. A marble has a marble to its right/next/clockwise
    // and a marble to its left/previous/counter-clockwise.
    static class Marble {
        int value;
        Marble next;
        Marble prev;

        public Marble(int value) {
            this.value = value;
            this.next = null;
            this.prev = null;
        }

        public String toString() {
            return String.valueOf(this.value);
        }
    }

    // Class representing a marble circle game. Implemented as a circular doubly-linked list (DLL).
    static class MarbleCircleGame {
        Marble head; // pointer to the first marble
        Marble currentMarble; // pointer to the current marble
        int numMarbles; // keep track of the number of marbles for printing/debugging purposes.

        // When starting this marble circle game, the marble numbered 0 is first placed into the circle.
        // Then the game starts from there.
        public MarbleCircleGame() {
            Marble zero = new Marble(0);
            head = zero;
            currentMarble = zero;
            numMarbles = 1;

            // At the start, even though the game only has a single marble,
            // it is still a circle: the marble is both clockwise from itself
            // and counter-clockwise from itself.
            zero.prev = zero;
            zero.next = zero;
        }

        // Adds the given marble value to the marble circle and returns the value
        // of any marbles scored. For marbles that are a multiple of 23, the score is the value of the current
        // marble plus the value of the marble 7 spots counter-clockwise (left) of the current marble.
        // For marbles that are NOT a multiple of 23, there is no score.
        public int addNextMarble(int value) {
            Marble n = new Marble(value);
            int score = 0;
            if (value % 23 == 0) {
                // For marbles with a multiple of 23, we don't insert the marble into the circle.
                // Instead, we find the marble that is 7 spots to the left/counter-clockwise of the
                // current marble and remove it form the circle. The score is the value of the marble
                // that we would have inserted plus the value of the marble that was removed.
                Marble marbleToBeRemoved = currentMarble.prev.prev.prev.prev.prev.prev.prev;

                // Remove the marble
                currentMarble = marbleToBeRemoved.next;
                marbleToBeRemoved.prev.next = marbleToBeRemoved.next;
                marbleToBeRemoved.next.prev = marbleToBeRemoved.prev;
                numMarbles -= 1;

                score += (value + marbleToBeRemoved.value);
            } else {  // Insert non-multiple-of-23 marbles.
                // Identify the marbles that are one and two spots clockwise/right of the current marble.
                // We want to insert our new marble between these two marbles.
                Marble oneAway = currentMarble.next;
                Marble twoAway = currentMarble.next.next;

                oneAway.next = n;
                n.prev = oneAway;
                n.next = twoAway;
                twoAway.prev = n;

                // Update our current marble pointer.
                currentMarble = n;
                numMarbles += 1;
            }

            return score;
        }

        // Prints the marbles in the game. Because this is implemented as a circular DLL,
        // we use the marble count to determine when to stop printing.
        public void print() {
            Marble curr = head;
            for (int i = 0; i < numMarbles; i++) {
                System.out.print(curr);
                System.out.print(" -> ");
                curr = curr.next;
            }
            System.out.println();
        }
    }
}
