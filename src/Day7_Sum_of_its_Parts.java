
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day7_Sum_of_its_Parts {
    public static void main(String[] args) {
        File file = new File("./inputs/day7/day7.txt");

        try {
            Scanner sc = new Scanner(file);

            // Adjacency list representation of the graph formed by the steps
            Map<String, List<String>> adjacencyList = new HashMap<>();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(" ");
                String step1 = tokens[1];
                String step2 = tokens[7];

                updateAdjacencyList(step1, step2, adjacencyList);
            }

            // Stores the indegrees of each step.
            int[] indegrees = new int[adjacencyList.size()];

            for (String node : adjacencyList.keySet()) {
                for (String child : adjacencyList.get(node)) {
                    // increment the count of incoming edges
                    // convert the step letter to its ASCII representation and normalize by subtracting 'A'
                    // to achieve zero-indexing.
                    indegrees[(int)child.charAt(0) - 'A']++;
                }
            }

            // We clone the original indegrees array that we'll use in part 2
            // because the topological sort algorithm will modify the indegrees array.
            int[] indegrees2 = indegrees.clone();

            String part1 = part1(adjacencyList, indegrees);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(adjacencyList, indegrees2, 5);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Given an adjacency list, a first step, and a second step that depends upon the first step,
    // adds a mapping between those two steps.
    private static void updateAdjacencyList(String step1, String step2, Map<String, List<String>> adjacencyList) {
        if (adjacencyList.containsKey(step1)) {
            List<String> children = adjacencyList.get(step1);
            children.add(step2);
            adjacencyList.put(step1, children);
        } else {
            List<String> children = new ArrayList<>();
            children.add(step2);
            adjacencyList.put(step1, children);
        }

        if (!adjacencyList.containsKey(step2)) {
            adjacencyList.put(step2, new ArrayList<>());
        }
    }

    // Part 1: Returns the order in which the steps must be completed as a string.
    // We represent the ordering of the steps as a directed graph where a node is a step in sleigh-building
    // instructions. We implement Kahn's algorithm for a topological sorting of this graph.
    // We need to maintain a store of indegrees of each node in the graph and a priority queue
    // which gives us the node to visit next: the next node of indegree 0 to visit
    // (which means it has no non-visited prerequisite steps)
    // Note: We assume that no cycle exists in our graph. Also, we don't need to keep track of visited nodes,
    // because we only add any new nodes with an indegree of 0 to the queue.
    private static String part1(Map<String, List<String>> adjacencyList, int[] indegrees) {
        StringBuilder sb = new StringBuilder();
        PriorityQueue<String> queue = new PriorityQueue<>(new StringComparator());

        // Seed the queue with all nodes that have indegree 0 and mark them as visited.
        for (int i = 0; i < indegrees.length; i++) {
            if (indegrees[i] == 0) {
                queue.add(String.valueOf((char)(i+'A')));
            }
        }

        while (!queue.isEmpty()) {
            String currentStep = queue.poll();
            sb.append(currentStep);

            // All children of this node should have their indegree values decreased by 1
            // as we are done processing this node. If, during this process, any nodes
            // now have an indegree of 0, add it to our queue.
            List<String> children = adjacencyList.get(currentStep);
            for (String child : children) {
                indegrees[(int)child.charAt(0)-'A']--;

                if (indegrees[(int)child.charAt(0)-'A'] == 0) {
                    queue.add(child);
                }
            }
        }

        return sb.toString();
    }

    // Given an array of workers, returns the next available worker
    // (a worker who isn't actively working on a step).
    private static Worker getAvailableWorker(Worker[] workers) {
        for (int i = 0; i < workers.length; i++) {
            if (workers[i].step == null) {
                return workers[i];
            }
        }

        return null;
    }

    // Assigns any available steps to any available workers.
    // Iterates through the available steps, determines the next worker available, and assigns
    // the step to that worker if one is available.
    private static void assignAvailableStepsToWorkers(Worker[] workers, Queue<String> availableSteps) {
        Set<String> stepsToRemove = new HashSet<>();

        // Assign each worker an available step to work on (one that has indegree of 0).
        for (String step : availableSteps) {
            Worker availableWorker = getAvailableWorker(workers);
            if (availableWorker != null) {
                stepsToRemove.add(step);

                availableWorker.step = step;

                // Each step takes 60 seconds plus an amount corresponding to its letter:
                // A=1, B=2, C=3, and so on. So, step A takes 60+1=61 seconds,
                // while step Z takes 60+26=86 seconds.
                // We convert the char of that character to its ASCII decimal representation and subtract 4
                // which is the difference between the ASCII decimal and the amount of seconds corresponding
                // to each letter.
                // e.g. A = 65 in ASCII so subtracting 4 gives us 61. Z is 90 is ASCII and subtracting 4
                // gives us 86.
                availableWorker.timeRemaining = ((int)step.charAt(0)) - 4;
            }
        }

        // Now that these steps have been assigned, they are no longer available for another worker
        // to work on so they should be removed.
        for (String stepToRemove : stepsToRemove) {
            availableSteps.remove(stepToRemove);
        }
    }

    // Iterate through all workers and decrement the time remaining
    // on any steps that are actively being worked on by one.
    // If any times hit 0, unassign that step from the worker and mark it as complete.
    // Returns a list of steps that completed this iteration.
    private static List<String> work(Worker[] workers) {
        List<String> completedSteps = new ArrayList<>();
        for (int i = 0; i < workers.length; i++) {
            if (workers[i].step != null) {
                workers[i].timeRemaining--;

                if (workers[i].timeRemaining == 0) {
                    completedSteps.add(workers[i].step);
                    workers[i].step = null;
                }
            }
        }

        return completedSteps;
    }

    // Part 2: Returns the number of seconds it takes to follow the topological ordering of the graph
    // if there are n workers and each step takes a certain number of seconds to complete. Simulates
    // the topological sort graph traversal. Keep track of a queue of available steps to be worked on.
    // At very iteration of the loop, one second passes. At each second, the time remaining for a particular step
    // being worked one is decremented by one. If any steps complete, those steps are unassigned from their workers,
    // and its children are added to a queue of available steps to work on. Those available steps are then assigned
    // to any available workers and the loop repeats. At the end, return the number of seconds (or loop iterations)
    // that have passed such that all steps are fully complete.
    private static int part2(Map<String, List<String>> adjacencyList, int[] indegrees, int numWorkers) {
        Queue<String> availableSteps = new PriorityQueue<>(new StringComparator());
        Set<String> completedSteps = new HashSet<>();

        Worker[] workers = new Worker[numWorkers];

        // Initially, all workers are not working on anything.
        for (int i = 0; i < numWorkers; i++) {
            workers[i] = new Worker(i+1, null, 0);
        }

        // All steps that have indegree 0 are available to be worked on.
        for (int i = 0; i < indegrees.length; i++) {
            if (indegrees[i] == 0) {
                availableSteps.add(String.valueOf((char)(i+'A')));
            }
        }

        // Assign any available steps to any available workers.
        assignAvailableStepsToWorkers(workers, availableSteps);

        int second = 0;

        // Keep going until we've completed all the steps.
        while (completedSteps.size() != adjacencyList.keySet().size()) {
            // Decrement all times by one.
            // If any steps are complete, unassign it from the worker.
            List<String> newlyCompleted = work(workers);

            if (newlyCompleted.size() > 0) {
                completedSteps.addAll(newlyCompleted);

                for (String completedStep : newlyCompleted) {
                    availableSteps.remove(completedStep);

                    // All children of this node should have their indegree values decreased by 1
                    // as we are done processing this node. If, during this process, any nodes
                    // now have an indegree of 0, add it to our queue of available steps.
                    List<String> children = adjacencyList.get(completedStep);
                    for (String child : children) {
                        indegrees[(int)child.charAt(0)-'A']--;

                        if (indegrees[(int)child.charAt(0)-'A'] == 0) {
                            availableSteps.add(child);
                        }
                    }
                }
            }

            // If there are any available steps, assign them to any available workers.
            assignAvailableStepsToWorkers(workers, availableSteps);

            second++;
        }

        return second;
    }

    // Comparator class for PriorityQueue ordering. This is most likely not needed as
    // the priority queue will use the "natural ordering" of its elements which, for strings, is its
    // lexicographic ordering. However, this is included here as a safeguard in the event that
    // requirements change.
    static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String x, String y) {
            // If the first string is lexicographically greater than the second string,
            // it returns a positive number (difference of character value).
            // If the first string is less than the second string lexicographically,
            // it returns a negative number,
            // and if the first string is lexicographically equal to the second string, it returns 0.
            return x.compareTo(y);
        }
    }

    // Class to represent a worker.
    // The step being worked on will either be an active step or null,
    // indicating that nothing is being worked on now.
    // The time remaining is the amount of time units it takes for the step to be complete.
    static class Worker {
        int id;
        String step;
        int timeRemaining;

        public Worker(int id, String step, int timeRemaining) {
            this.id = id;
            this.step = step;
            this.timeRemaining = timeRemaining;
        }

        public String toString() {
            return "Worker " + id + ": " + (step != null ? step + ", " + timeRemaining + " seconds left." : "-");
        }
    }
}