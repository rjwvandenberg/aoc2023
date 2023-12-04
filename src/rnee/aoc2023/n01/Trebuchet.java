package rnee.aoc2023.n01;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.stream.Stream;

public class Trebuchet {
    public static void main(String[] args) throws Exception {
        try (FileReader fin = new FileReader("res/n01/test2.txt"); Stream<String> lines = new LineNumberReader(fin).lines()) {
            long sum = lines.map(line -> {
                        int[] digits = replaceNumberStrings(line).chars().map(v->v-'0').toArray();
                        System.out.println(Arrays.toString(digits));
                        return (long) digits[0] * 10 + digits[digits.length - 1];
                    })
                    .reduce(0L, Long::sum);

            System.out.println(sum);
        }
    }

    private static final String[] NUMBER_STRINGS = new String[]{"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    private static CharSequence replaceNumberStrings(String line) {
        // replaces all strings, only need first and last
        // ambiguity oneight -> 11 or 18
        // i choose 11
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            int cp = line.codePointAt(i);
            if (Character.isDigit(cp)) {
                sb.append(Character.toChars(cp)[0]);
            } else {
                for (int d = 0; d < NUMBER_STRINGS.length; d++) {
                    if (line.substring(i).startsWith(NUMBER_STRINGS[d])) {
                        sb.append(d + 1);
                        i += NUMBER_STRINGS[d].length()-1;
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }
}