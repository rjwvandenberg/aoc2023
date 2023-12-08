package rnee.aoc2023.n07;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntUnaryOperator;

public class Camel {
    public record Bid(Hand hand, int amount){}
    public record Hand(int strength, int[] cards){}
    public static void main(String[] args) throws Exception {
        try (FileReader fin = new FileReader("res/n07/bid.txt")) {
            // parse and order the bids by hand strength
            List<Bid> bids = new LineNumberReader(fin).lines().map(s->{
                String[] b = s.trim().split("\\s");
                return new Bid(parseHand(b[0]), Integer.parseInt(b[1]));
            }).sorted(getBidComparator()).toList();

            // compute and print winnings
            int winnings = 0;
            for (int i = 0; i < bids.size(); i++) {
                winnings += (i+1) * bids.get(i).amount;
            }
            System.out.println(winnings);

            // part 2, replace J by jokers
            bids = bids.stream().map(b->{
                int[] cards = Arrays.stream(b.hand.cards).map(c-> c==11 ? 1:c).toArray();
                System.out.println(Arrays.toString(cards));
                int jokers = Arrays.stream(cards).filter(v->v==1).sum();
                return new Bid(computeHandStrength(cards, jokers), b.amount);
            }).sorted(getBidComparator()).toList();
            // compute and print winnings
            winnings = 0;
            for (int i = 0; i < bids.size(); i++) {
//                System.out.println(bids.get(i).amount + " * " + (i+1) + "    " + bids.get(i).hand.strength + "     " + Arrays.toString(bids.get(i).hand.cards));
                winnings += (i+1) * bids.get(i).amount;
            }
            System.out.println(winnings);
        }
    }

    private static Comparator<Bid> getBidComparator() {
        return (b1, b2) -> {
            // sort by strength
            int typeStrength = b1.hand.strength - b2.hand.strength;
            if (typeStrength != 0) return typeStrength;
            // else pairwise card comparison front to back
            return Arrays.compare(b1.hand.cards, b2.hand.cards);
        };
    }

    public static Hand parseHand(String s) {
        int[] cards = s.trim().chars().map(charToCard()).toArray();
        return computeHandStrength(cards, 0);
    }

    private static Hand computeHandStrength(int[] cards, int jokers) {
        int strength = 0; // high card
        int[] counts = new int[15];
        for (int card : cards) {
            counts[card]++;
        }
        int max = Arrays.stream(counts).max().getAsInt();
        long twos = Arrays.stream(counts).filter(v->v==2).count();

        if (max == 5) {
            strength = 6; // five of a kind
        } else if (max == 4) {
            strength = 5; // four of a kind
        } else if (max == 3 && twos > 0) {
            strength = 4; // full house
        } else if (max == 3) {
            strength = 3; // three of a kind
        } else if (twos == 2) {
            strength = 2; // two pair
        } else if (twos == 1) {
            strength = 1; // one pair
        }

        // adjust for jokers
        if (jokers == 4 ) {
            strength = 6;
        } else if (jokers == 3) {
            strength = switch(strength) {
                case 4: yield 6; // full house to five of kind
                case 3: yield 5; // three jokers to foru of a kind
                default: yield strength; // others cant exist
            };
        } else if (jokers == 2) {
            strength = switch(strength) {
                case 4: yield 6; // full house to five of kind
                case 2: yield 5; // two pair to four of a kind
                case 1: yield 3; // pair jokers to three of kind
                default: yield strength; // others cant exist
            };
        } else if (jokers == 1) {
            strength = switch (strength) {
                case 5: yield 6; // four of kind to five of kind
                case 3: yield 5; // three of kind to four of kind
                case 2: yield 4; // two pair to full house
                case 1: yield 3; // pair to three of kind
                case 0: yield 1; // high card to pair
                default: yield strength; // others cases cant exist
            };
        }
        
        return new Hand(strength ,cards);
    }

    private static IntUnaryOperator charToCard() {
        return c -> {
            if (c <= '9') return c - '0';
            return switch (c) {
                case 'T' -> 10;
                case 'J' -> 11;
                case 'Q' -> 12;
                case 'K' -> 13;
                case 'A' -> 14;
                default -> throw new IllegalStateException("Non-existent card: " + c);
            };
        };
    }
}
