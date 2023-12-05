package rnee.aoc2023.n05;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.*;
import java.util.stream.Stream;

public class Fertilizer {
    public static class Mapping {
        long difference;
        long source;
        long length;

        Mapping(long dest, long src, long length) {
            this.difference = dest - src;
            this.source = src;
            this.length = length;
        }
    }

    public static record Range (long src, long length){}
    public static void main(String[] args) throws Exception {
        try (FileReader fin = new FileReader("res/n05/almanac.txt")) {
            Iterator<String> almanac = new LineNumberReader(fin).lines().iterator();

            Iterator<Long> seedNumbers = Arrays.stream(almanac.next().split(":")[1].trim().split("\\s")).mapToLong(Long::parseLong).iterator();
            List<Range> seeds = new ArrayList<>();
            while (seedNumbers.hasNext()) {
                long source = seedNumbers.next();
                long length = seedNumbers.next();
                seeds.add(new Range(source, length));
            }

            // alternative: if sorted non overlap ranges, and sorted non overlap maps
            // then linear traversal of range+maps
            // and mlogm,  sort/combine of ranges

            almanac.next(); // whiteline
            while (almanac.hasNext()) { // while there are mappings
                almanac.next(); // skip header

                // create map - One to one maps, defined as dest start, source start, length
                List<Mapping> mapping = createMapping(almanac);

                List<Range> newSeedRanges = new ArrayList<>();
                // apply the mapping to all seeds
                for (int s = 0; s < seeds.size(); s++) {
                    Range seedRange = seeds.get(s);

                    // need to find all mappings that overlap with seedRange
                    List<Mapping> m = findAllMapping(seedRange.src, mapping);

                    while (seedRange != null) { // could encounter range that spans multiple maps
                        // split seedRange to the mappings and non mappings


                        if (seedRange.src+seedRange.length-1 >= m.source+m.length) {
                            // reassign seedRange to split range
                        } else {
                            seedRange = null;
                        }
                    }

                    // apply map - unmapped values remain unchanged
                    seeds[s] = n;
                }

                // could explode number of ranges, sort and combine?
                seeds = newSeedRanges;
            }

            // print lowest value after final mapping
            System.out.println(seeds.stream().min(Comparator.comparingLong(a->a.src)));
        }
    }

    private static List<Mapping> findAllMapping(long src, List<Mapping> mapping) {
        return new ArrayList<Mapping>();
    }

    private static Mapping findMapping(long n, List<Mapping> mapping) {
        // linear search, could optimize with bin search if non overlapping ranges
        for (Mapping m: mapping) {
            if (n >= m.source && n < m.source+m.length) {
                return m;
            }
        }

        return null;
    }

    private static List<Mapping> createMapping(Iterator<String> almanac) {
        ArrayList<Mapping> mappings = new ArrayList<>();

        while (almanac.hasNext()) { // eof indicates end of mapping
            String line = almanac.next();
            if (line.isEmpty()) {
                break; // empty line indicates end of mapping
            }

            String[] numbers = line.split("\\s");
            long dest = Long.parseLong(numbers[0]);
            long src = Long.parseLong(numbers[1]);
            long length = Long.parseLong(numbers[2]);

            Mapping m = new Mapping(dest, src, length);
            mappings.add(m);
        }
        mappings.sort(Comparator.comparingLong(a -> a.source));

        // assert non-overlapping
        for (int i = 0; i < mappings.size()-1; i++){
            Mapping a = mappings.get(i);
            Mapping b = mappings.get(i+1);
            assert(a.source+a.length-1 < b.source);
        }

        return mappings;
    }
}