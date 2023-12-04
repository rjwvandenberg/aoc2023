package rnee.aoc2023.n04;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ScratchCard {
    public static void main(String[] args) throws Exception {
        try (FileReader fin = new FileReader("res/n04/cards.txt")) {
            List<String> cards = new LineNumberReader(fin).lines().toList();
            int points = 0;
            long[] copies = new long[cards.size()];
            long totalcopies = 0;


            for (int i = 0; i < cards.size(); i++) {
                String card = cards.get(i);
                int matches = 0;
                String[] numbers = card.split(":")[1].split("\\|");
                HashSet<String> winning = new HashSet<>(Arrays.stream(numbers[0].split(" ")).toList());
                for (String number : numbers[1].split(" ")) {
                    if (!number.isEmpty() && winning.contains(number)) {
                        matches++;
                    }
                }
                int value = (1 << matches) >> 1;
                points += value;

                // also adds to the number of cards
                // starting card
                copies[i]++;
                // new copies won
                for (int j = i + 1; j < copies.length && j < i + 1 + matches; j++) {
                    copies[j] += copies[i];
                }
                // add to total
                totalcopies += copies[i];
            }

            System.out.println(points);
            System.out.println(totalcopies);
        }
    }
}
