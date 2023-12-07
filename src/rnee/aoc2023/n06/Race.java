package rnee.aoc2023.n06;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.Iterator;

public class Race {
    public static void main(String[] args) throws Exception {
        try (FileReader fin = new FileReader("res/n06/paper.txt"); LineNumberReader lr = new LineNumberReader(fin)) {
            String times = lr.readLine();
            String distances = lr.readLine();
            part1(times, distances);
            part2(times, distances);
        }
    }

    private static void part2(String times, String distances) {
        long finishTime = Long.parseLong(times.split(":")[1].replaceAll("\\s",""));
        long finishDistance = Long.parseLong(distances.split(":")[1].replaceAll("\\s",""));

        // start 0mm/s
        // windup +1 mm/s initial speed
        // for t time waited, we have an initial speed of t mm/s
        // it can travel d = (finishTime-t)*t in the remaining time
        // newRecord if d > finishDistance

        //  (finishTime-t)*t > finishDistance
        //  0 > t^2 -finishTime*t + finishDistance
        //  a = 1, b = -finishTime, c = 1
        //  t = (finishTime +- sqrt(finishtime*finishtime-4*finishdistance)) / 2
        long disc = finishTime*finishTime-4*finishDistance;
        double sqrt = Math.sqrt(disc);
        double t1 = (finishTime + sqrt)/2;
        double t2 = (finishTime - sqrt)/2;

        long start0 = (long)Math.ceil(Math.max(t2,0));
        long end0 = Math.min((long)Math.floor(t1), finishTime);

        System.out.println(finishTime - start0 - (finishTime-1-end0));

        // sanity check
        int record = 0;
        for (int t = 1; t < finishTime-1; t++) {
            if ((finishTime-t)*t > finishDistance) {
                record++;
            }
        }
        System.out.println(record);
    }

    private static void part1(String times, String distances) {
        int[] finishTimes = Arrays.stream(times.split(":")[1].split("\\s")).filter(s->!s.isEmpty()).mapToInt(Integer::parseInt).toArray();
        int[] finishDistances= Arrays.stream(distances.split(":")[1].split("\\s")).filter(s->!s.isEmpty()).mapToInt(Integer::parseInt).toArray();

        // start 0mm/s
        // windup +1 mm/s initial speed
        // for t time waited, we have an initial speed of t mm/s
        // it can travel d = (finishTime-t)*t in the remaining time
        // newRecord if d > finishDistance

        long answer = 0;
        for (int i = 0; i < finishTimes.length; i++) {
            int record = 0;
            for (int t = 1; t < finishTimes[i]-1; t++) {
                if ((finishTimes[i]-t)*t > finishDistances[i]) {
                    record++;
                }
            }
            if (answer == 0) {
                answer = record;
            } else {
                answer *= record;
            }
        }
        System.out.println(answer);
    }
}