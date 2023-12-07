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
                    newSeedRanges.addAll(findAllMapping(seedRange, mapping));
                }

                // could explode number of ranges, sort and combine?
                seeds = newSeedRanges;
            }

            // print lowest value after final mapping
            System.out.println(seeds.stream().min(Comparator.comparingLong(a->a.src)).get().src);
        }
    }

    private static List<Range> findAllMapping(Range range, List<Mapping> mappings) {
        List<Range> ranges = new ArrayList<>();
        for (Mapping m : mappings) {
            if (range == null) {
                // processed entire range
                break;
            }
            if (m.source+m.length-1 < range.src) {
                // skip mapping as it fully precedes range
                continue;
            }
            if (range.src+range.length-1<m.source) {
                // range precedes mapping, so break out of mapping loop
                break;
            }
            // There is some overlap
            Range prefix = range.src < m.source ? new Range(range.src, m.source-range.src) : null ;
            Range remainder = range.src+range.length-1 > m.source+m.length-1 ? new Range(m.source+m.length, range.src+range.length-m.source-m.length) : null;
            long overlapstart = Math.max(m.source, range.src);
            long overlaplength = Math.min(range.src+range.length, m.source+m.length) - overlapstart;
            Range overlap = new Range(overlapstart+m.difference, overlaplength);

            // add overlap newly mapped range to ranges
            ranges.add(overlap);
            if (prefix != null) {
                // add prefix without differencing
                ranges.add(prefix);
            }
            // use remainder as next range
            range = remainder;
        }

        if (range != null) {
            // No more mappings so add remaining range
            ranges.add(range);
        }

        return ranges;
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