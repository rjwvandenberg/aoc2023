package rnee.aoc2023.n09;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

public class Oasis {
    public static void main(String[] args) throws Exception {
        try (FileReader fin = new FileReader("res/n09/oasis.txt")) {
            Iterator<String> histories = new LineNumberReader(fin).lines().iterator();

            long sumOfNext = 0;
            long sumOfPrevious = 0;

            while (histories.hasNext()) {
                int[] graphs = Arrays.stream(histories.next().split("\\s")).mapToInt(Integer::parseInt).toArray();
                Stack<Integer> finalGraphs = new Stack<>();
                Stack<Integer> firstGraphs = new Stack<>();

                while (!Arrays.stream(graphs).allMatch(v -> v == 0)) {
                    finalGraphs.push(graphs[graphs.length - 1]);
                    firstGraphs.push(graphs[0]);

                    int[] tangents = new int[graphs.length - 1];
                    for (int i = 1; i < graphs.length; i++) {
                        tangents[i - 1] = graphs[i] - graphs[i - 1];
                    }

                    graphs = tangents;
                }


                sumOfNext += finalGraphs.stream().reduce(0, Integer::sum);

                int previous = 0;
                while (!firstGraphs.isEmpty()) {
                    int graph = firstGraphs.pop();
                    previous = graph - previous;
                }
                sumOfPrevious += previous;
            }

            System.out.println(sumOfNext);
            System.out.println(sumOfPrevious);
        }
    }
}
