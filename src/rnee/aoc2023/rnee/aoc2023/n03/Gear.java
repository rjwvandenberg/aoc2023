package rnee.aoc2023.rnee.aoc2023.n03;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class Gear {
    public static class Repr {
        int i; String s; int gear; int ratio;

        Repr(int i, String s, int gear, int ratio) {
            this.i = i; this.s = s; this.gear = gear; this.ratio = ratio;
        }
    }
    public static void main(String[] args) throws Exception {
        try (FileReader fin = new FileReader("res/n03/schematics.txt")) {
            List<String> schematic = new LineNumberReader(fin).lines().toList();
            long partSum = 0;

            HashSet<Repr> gears = new HashSet<Repr>();
            HashSet<Repr> previousSymbols = new HashSet<Repr>();
            HashSet<Repr> previousParts = new HashSet<Repr>();

            for (String drawing : schematic) {
                // record index,symbol and index,number pairs
                HashSet<Repr> symbols = new HashSet<Repr>();
                HashSet<Repr> parts = new HashSet<Repr>();

                int i = 0;
                while (i < drawing.length()) {
                    char c = drawing.charAt(i);
                    if (c == '.') {
                        i++;
                    } else if (Character.isDigit(c)) {
                        int end = i;
                        while (end < drawing.length() && Character.isDigit(drawing.charAt(end))) {
                            end++;
                        }
                        parts.add(new Repr(i, drawing.substring(i,end),0,Integer.parseInt(drawing.substring(i,end))));
                        i = end;

                    } else {
                        symbols.add(new Repr(i, Character.toString(c), 0,1));
                        i++;
                    }
                }

                // compare to previous line

                // compare symbol to previous part, remove previous part if adjacent
                for (Repr s : symbols) {
                    HashSet<Repr> compPart = new HashSet<>(previousParts);
                    for (Repr p : compPart) {
                        // diagonal adjacent
                        if (s.i >= p.i-1 && s.i <= (p.i+p.s.length())) {
                            //previousParts.remove(p);
                            partSum += Integer.parseInt(p.s);
                            s.gear++;
                            s.ratio *= Integer.parseInt(p.s);
                        }
                    }
                }

                // parts to previous symbol, remove current part if adjacent
                for (Repr p : new HashSet<>(parts)) {
                    for (Repr s : previousSymbols) {
                        // diagonal adjacent
                        if (s.i >= p.i-1 && s.i <= (p.i+p.s.length())) {
                            //parts.remove(p);
                            partSum += Integer.parseInt(p.s);
                            // skip other symbols for this part
                            //break;
                            s.gear++;
                            s.ratio *= Integer.parseInt(p.s);
                        }
                    }
                }

                // compare current parts to current symbols, remove current part if adjacent
                for (Repr p : new HashSet<>(parts)) {
                    for (Repr s : symbols) {
                        // adjacent
                        if (s.i >= p.i-1 && s.i <= (p.i+p.s.length())) {
                            //parts.remove(p);
                            partSum += Integer.parseInt(p.s);
                            // skip other symbols for this part
                            //break;
                            s.gear++;
                            s.ratio *= Integer.parseInt(p.s);
                        }
                    }
                }

                for (Repr s : symbols) {
                    // find gears
                    if (s.s.equals("*")) {
                        gears.add(s);
                    }
                }


                // replace old with new
                previousSymbols = symbols;
                previousParts = parts;
            }


            System.out.println(partSum);

            // compute sum of gear ratios
            int gearRatios = 0;
            for (Repr g : gears) {
                if (g.gear == 2) {
                    gearRatios += g.ratio;
                }
            }
            System.out.println(gearRatios);
        }
    }
}
