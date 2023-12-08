package rnee.aoc2023.n08;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.*;
import java.util.stream.Stream;

public class Wasteland {
    public record Path(int L, int R) {
    }

    public static int nodeStringToNode(String nodeString) {
        int a = nodeString.charAt(0) << 16;
        int b = nodeString.charAt(1) << 8;
        int c = nodeString.charAt(2);
        return a + b + c;
    }

    public static boolean isEnd(int node) {
        return (node & 0xff) == 'Z';
    }

    public static boolean isStart(int node) {
        return (node & 0xff) == 'A';
    }

    public static Map<Integer, Path> constructMap(Iterator<String> lines) {
        HashMap<Integer, Path> map = new HashMap<>();

        while (lines.hasNext()) {
            String[] line = lines.next().split("=");
            int node = nodeStringToNode(line[0].trim());

            line = line[1].trim().split(",");
            int leftPath = nodeStringToNode(line[0].substring(1).trim());
            int rightPath = nodeStringToNode(line[1].trim().substring(0, 3));

            map.put(node, new Path(leftPath, rightPath));
        }

        return map;
    }

    public static final int MAX_STEPS = 10000000;
    public static final int START_NODE = nodeStringToNode("AAA");
    public static final int END_NODE = nodeStringToNode("ZZZ");

    public record StepNode(long step, int nodeKey) {
    }

    public static void main(String[] args) throws Exception {
        try (FileReader fin = new FileReader("res/n08/network.txt")) {
            Iterator<String> lines = new LineNumberReader(fin).lines().iterator();

            String instructions = lines.next().trim();
            lines.next(); // skip blank line

            Map<Integer, Path> map = constructMap(lines);


            // A solution would have to calculate the reduced graph. start -> endnode and endnode -> endnode
            // then evaluate how many steps it would take to have all starts in endnode by what step.

            // for every start do the following:
            // start -> end1 -> end2 -> end3 -> end4 -> .... -> endn, recording the steps inbetween, stopping when the cycle is detected


            Stream<ArrayList<StepNode>> cycles = map.keySet().stream().filter(Wasteland::isStart).map(startKey -> {
                ArrayList<StepNode> cycle = new ArrayList<>();
                cycle.add(new StepNode(0, startKey));
                HashSet<Integer> visited = new HashSet<>();

                int nodeKey = startKey;
                while (!visited.contains(nodeKey)) {
                    visited.add(nodeKey);

                    int step = 0;
                    do {
                        boolean goLeft = instructions.charAt(step % instructions.length()) == 'L';
                        Path p = map.get(nodeKey);
                        nodeKey = goLeft ? p.L : p.R;
                        step++;
                    } while (step < MAX_STEPS && !isEnd(nodeKey));

                    if (step == MAX_STEPS) {
                        throw new IllegalStateException("Too many steps to find next endNode");
                    }

                    cycle.add(new StepNode(step, nodeKey));
                }
                return cycle;
            });

            // in general this will result in a complicated cycle length matching?
            // start + (c1 + ... + ca) * x + (cb + cc + .... + cn) * (x-1) = match_steps     where x is the cycles travelled by this starting point
            // and has to match this with the other starting points at any possible endpoint.

            // In this particular case, the cycles are one step long. Node a -> Node a   in    x steps
            // So find the smalled number that is wholly divisible by all the node cycles.
            // Least common multiple of a, b, c, d, etc

            // Lets implement prime factorization to find it

            int[] primes = new int[10000];
            // compute primes, not prime = -1, prime = 0
            primes[0] = -1;
            primes[1] = -1;
            for (int n = 2; n < primes.length; n++) {
                if (primes[n] == -1) {
                    continue;
                }

                for (int div = 2; div <= n; div++) {
                    if (primes[div] == -1) {
                        break;
                    }

                    if (div == n) {
                        for (int i = 1; i * n < primes.length; i++) {
                            primes[i * n] = -1;
                        }
                        break;
                    }

                    if (n % div == 0) {
                        primes[n] = -1;
                        break;
                    }
                }

            }

            // find factorizations and record them in primes (need max of each factor to ensure cycle factorization is subset of factorization of the final multiple)
            cycles.forEach(cycle -> {
                long n = cycle.get(cycle.size() - 1).step;

                for (int prime = 2; prime < primes.length; prime++) {
                    if (n == 1) {
                        break;
                    }
                    if (primes[prime] == -1) {
                        continue;
                    }
                    int times = 0;
                    while (n % prime == 0) {
                        times++;
                        n /= prime;
                    }

                    primes[prime] = Math.max(primes[prime], times);
                }
            });

            // Calculate the multiple from the "union" of factorizations
            long multiple = 1;
            for (int i = 0; i < primes.length; i++) {
                if (primes[i] < 1) {
                    continue;
                }
                System.out.println(i + "**" + primes[i]);
                while (primes[i] > 0) {
                    multiple *= i;
                    primes[i]--;
                }
            }
            System.out.println(multiple);


            // solution below still too bruteforce and doesn't result in an answer
            // nextZ gets filled with 12 stepnode->stepnode linked (6 from step 0 and 6 further endnode->endnode links

            // maintain priorityqueue with steps, node
            // remove elements with steps equal to lowest step
            // if empty, found #steps
            // else readd elements by following

            // also need to maintain a  -> (nextz,nextznode) map
            // Map to maintain nextZstep and nextZnode for (step%instr.size(),node)
//            HashMap<StepNode, StepNode> nextZ = new HashMap<>();
//            PriorityQueue<StepNode> stepNodes = new PriorityQueue<>(Comparator.comparingLong(a -> a.step));
//            map.keySet().stream().filter(Wasteland::isStart).forEach(nodeKey -> stepNodes.add(new StepNode(0, nodeKey)));
//            int startingNodesSize = stepNodes.size();
//
//            // continue while not all are endNodes and steps are not equal
//            while (!isComplete(stepNodes)) {
//                // replace nodes with all stepNodes that match the minimum step in stepNodes
//                ArrayList<StepNode> nodes = new ArrayList<>();
//                StepNode min = stepNodes.peek();
//                while (!stepNodes.isEmpty() && stepNodes.peek().step == min.step) {
//                    nodes.add(stepNodes.remove());
//                }
//
//                // for every node with minimum stepcount, progress to next endNode
//                for (StepNode stepnode : nodes) {
//                    StepNode nextCheck = new StepNode(stepnode.step%instructions.length(), stepnode.nodeKey);
//                    StepNode next = nextZ.get(nextCheck);
//                    if (next == null) {
//                        // compute next for stepNode
//                        int nodeKey = stepnode.nodeKey;
//                        long step = 0;
//                        do {
//                            int tempStep = (int)((stepnode.step+step) % instructions.length());
//                            boolean goLeft = instructions.charAt(tempStep) == 'L';
//                            Path p = map.get(nodeKey);
//                            nodeKey = goLeft ? p.L : p.R;
//                            step++;
//                        } while (!isEnd(nodeKey));
//
//                        // found nextZ for key nodeCheck at offset step, leading to nodeKey
//                        next = new StepNode(step, nodeKey);
//                        // add computation to nextZ map
//                        nextZ.put(nextCheck, next);
//                    }
//
//                    // progress stepnode to next endNode
//                    stepNodes.add(new StepNode(stepnode.step+next.step, next.nodeKey));
//                }
//            }
//
//            //
//
//            assert(stepNodes.size() == startingNodesSize);
//            assert(stepNodes.isEmpty());
//            System.out.println(stepNodes.remove().step);

            // bruteforce
//            while (steps < MAX_STEPS && !nodes.stream().allMatch(Wasteland::isEnd)) {
//                boolean goLeft = instructions.charAt(steps % instructions.length()) == 'L';
//                nodes = nodes.stream().map(node -> {
//                    Path paths = map.get(node);
//                    return goLeft ? paths.L : paths.R;
//                }).toList();
//
//                steps++;
//            }
//
//            if (steps == MAX_STEPS) {
//                throw new IllegalStateException("Exceeded maximum steps allowed");
//            }

//            System.out.println(steps);
        }
    }

    private static boolean isComplete(PriorityQueue<StepNode> stepNodes) {
        StepNode min = stepNodes.peek();
        if (min == null) {
            throw new IllegalStateException("Cannot be null");
        }
        long currentStep = min.step;
        return stepNodes.stream().allMatch(stepNode -> stepNode.step == currentStep && isEnd(stepNode.nodeKey));
    }
}
