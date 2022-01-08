import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day4_Repose_Record {
    private static final int NEW_SHIFT_RECORD = 1;
    private static final int FALL_ASLEEP_RECORD = 2;
    private static final int WAKE_UP_RECORD = 3;

    public static void main(String[] args) {
        File file = new File("./inputs/day4/day4.txt");

        try {
            Scanner sc = new Scanner(file);
            List<Record> records = new ArrayList<>();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                int year = Integer.parseInt(line.substring(1,5));
                int month = Integer.parseInt(line.substring(6,8));
                int day = Integer.parseInt(line.substring(9,11));
                int hour = Integer.parseInt(line.substring(12,14));
                int minute = Integer.parseInt(line.substring(15,17));
                Date d = new Date(year, month-1, day, hour, minute);

                int guardId = -1;
                int recordType;
                if (line.contains("asleep")) {
                    recordType = FALL_ASLEEP_RECORD;
                } else if (line.contains("wakes")) {
                    recordType = WAKE_UP_RECORD;
                } else {
                    recordType = NEW_SHIFT_RECORD;
                    guardId = Integer.parseInt(line.split(" ")[3].substring(1));
                }

                Record r = new Record(d, recordType);

                if (guardId != -1) {
                    r.setGuardId(guardId);
                }

                records.add(r);
            }

            Collections.sort(records);

            int part1 = part1(records);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(records);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Given a dictionary that maps a guard to their list of sleep-time and wake-time pairs,
    // return the ID of the guard who slept the most.
    private static int findMostAsleepGuard(Map<Integer, List<Pair>> map) {
        int guardIdMaxAsleep = 0;
        int maxMinutesAsleep = 0;

        for (Integer guardId : map.keySet()) {
            List<Pair> pairs = map.get(guardId);
            int sum = 0;
            for (Pair p : pairs) {
                sum += (p.wakeMinute - p.sleepMinute + 1);
            }
            if (sum > maxMinutesAsleep) {
                maxMinutesAsleep = sum;
                guardIdMaxAsleep = guardId;
            }
        }

        return guardIdMaxAsleep;
    }

    // Given a list of sleep-time and wake-time pairs, return the most common minute represented.
    private static int findMostAsleepMinute(List<Pair> asleepTimes) {
        Map<Integer, Integer> freq = new HashMap<>();

        for (Pair range : asleepTimes) {
            for (int i = range.sleepMinute; i <= range.wakeMinute; i++) {
                if (freq.containsKey(i)) {
                    freq.put(i, freq.get(i) + 1);
                } else {
                    freq.put(i, 1);
                }
            }
        }

        int maxMinute = 0;
        int mostFrequent = 0;

        for (Integer i : freq.keySet()) {
            if (freq.get(i) > mostFrequent) {
                maxMinute = i;
                mostFrequent = freq.get(i);
            }
        }

        return maxMinute;
    }

    private static Map<Integer, List<Pair>> constructGuardIdTimeMappings(List<Record> records) {
        int currentGuardId = 0;
        int currentSleepMinute = 0;

        // Maps a guard ID to a list of sleep-time and wake-time pairs
        Map<Integer, List<Pair>> map = new HashMap<>();
        for (int i = 0; i < records.size(); i++) {
            Record r = records.get(i);

            if (r.recordType == NEW_SHIFT_RECORD) {
                currentGuardId = r.guardId;
            } else if (r.recordType == FALL_ASLEEP_RECORD) {
                currentSleepMinute = r.timestamp.getMinutes();
            } else if (r.recordType == WAKE_UP_RECORD) {
                if (map.containsKey(currentGuardId)) {
                    List<Pair> currentPairs = map.get(currentGuardId);
                    currentPairs.add(new Pair(currentSleepMinute, r.timestamp.getMinutes()-1));
                    map.put(currentGuardId, currentPairs);
                } else {
                    List<Pair> newPair = new ArrayList<>();
                    newPair.add(new Pair(currentSleepMinute, r.timestamp.getMinutes()-1));
                    map.put(currentGuardId, newPair);
                }
            }
        }

        return map;
    }

    // Part 1: Finds the guard that has the most minutes asleep and the minute that guard spent asleep the most.
    // Iterate through the records and construct a dictionary mapping guard IDs to a list of their
    // sleep-time and wake-time pairs. Using that dictionary, find the guard who spent the most time asleep.
    // Then, iterate through the sleep-time and wake-time pairs of that guard and find the minute (0-59) they
    // were most often asleep.
    private static int part1(List<Record> records) {
        Map<Integer, List<Pair>> map = constructGuardIdTimeMappings(records);
        int guardIdMaxAsleep = findMostAsleepGuard(map);
        int minuteMostAsleep = findMostAsleepMinute(map.get(guardIdMaxAsleep));

        return guardIdMaxAsleep * minuteMostAsleep;
    }

    // Part 2: Of all guards, finds the guard who is most frequently asleep on the same minute.
    // Iterate through the records and construct a dictionary mapping guard IDs to a list of their
    // sleep-time and wake-time pairs. Using that dictionary, for each guard, calculate the frequency of each
    // minute they spent asleep. Keep track of the most frequent minute that was spent asleep and the corresponding
    // guard ID.
    private static int part2(List<Record> records) {
        Map<Integer, List<Pair>> map = constructGuardIdTimeMappings(records);

        int mostCommonMinute = 0;
        int freqOfMostCommonMinute = 0;
        int guardWithMostCommonMinute = 0;

        for (Integer guardId : map.keySet()) {
            List<Pair> ranges = map.get(guardId);
            Map<Integer, Integer> freq = new HashMap<>();
            for (Pair p : ranges) {
                for (int i = p.sleepMinute; i <= p.wakeMinute; i++) {
                    if (freq.containsKey(i)) {
                        freq.put(i, freq.get(i) + 1);
                    } else {
                        freq.put(i, 1);
                    }
                }
            }

            for (Integer minute : freq.keySet()) {
                if (freq.get(minute) > freqOfMostCommonMinute) {
                    freqOfMostCommonMinute = freq.get(minute);
                    mostCommonMinute = minute;
                    guardWithMostCommonMinute = guardId;
                }
            }
        }

        return guardWithMostCommonMinute * mostCommonMinute;
    }

    static class Pair {
        int sleepMinute;
        int wakeMinute;

        public Pair(int sleepMinute, int wakeMinute) {
            this.sleepMinute = sleepMinute;
            this.wakeMinute = wakeMinute;
        }

        public String toString() {
            return "(" + this.sleepMinute + "," + this.wakeMinute + ")";
        }
    }

    static class Record implements Comparable<Record> {
        Date timestamp;
        int recordType;
        int guardId;

        public Record(Date timestamp, int recordType) {
            this.timestamp = timestamp;
            this.recordType = recordType;
            this.guardId = 0;
        }

        public void setGuardId(int guardId) {
            this.guardId = guardId;
        }

        public String toString() {
            return "(" + this.timestamp.getYear() + "-" +
                    (this.timestamp.getMonth()+1) + "-" +
                    this.timestamp.getDate() + " " +
                    this.timestamp.getHours() + ":" +
                    this.timestamp.getMinutes() + "," +
                    this.recordType + "," +
                    this.guardId + ")";
        }

        public int compareTo(Record r) {
            return this.timestamp.compareTo(r.timestamp);
        }
    }
}