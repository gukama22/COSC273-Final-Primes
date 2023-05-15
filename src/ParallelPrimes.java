import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.*;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.*;

public class ParallelPrimes {

    // replace this string with your team name
    public static final String TEAM_NAME = "Sunny-Day";

    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE);
    public static final int nThreads = Runtime.getRuntime().availableProcessors();

    public static void optimizedPrimes(int[] primes) {

        // getting smallPrimes up until ROOT_MAX
        int[] smallPrimes = Primes.getSmallPrimes();
        System.arraycopy(smallPrimes, 0, primes, 0, smallPrimes.length);

        // storing what is the next available index in the primes array
        int nextIndex = smallPrimes.length;

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        //determining the size of each block of numbers to be evaluated.
        // the number of tasks to be created will dependent on  the block_increment

        int block_increment = ((MAX_VALUE - ROOT_MAX) / nThreads);
        int numTasks = (int) Math.ceil((double) (MAX_VALUE - ROOT_MAX) / block_increment);


        //create the arrayList that will hold the results of the evaluation, which will be returned as an array of
        // integers that are prime numbers within a specific interval.
        ArrayList<Future<int[]>> results = new ArrayList<Future<int[]>>(numTasks);


        int start_offset = ROOT_MAX;
        int Task_number = 0;

        //creating and submitting the task
        try {
            for (long i = ROOT_MAX; i < MAX_VALUE; i += block_increment) {
                long endIndex = Math.min((i + block_increment), (MAX_VALUE));
                results.add(pool.submit(new isPrimeTask(new BitSet((int) (endIndex - i + 1)), smallPrimes, i, endIndex, Task_number, start_offset, block_increment)));
                Task_number++;
            }

            pool.shutdown();

            for (int i = 0; i < results.size(); i++) {
                int[] result = results.get(i).get();
                System.arraycopy(result, 0, primes, nextIndex, result.length);
                nextIndex += result.length;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Primes.baselinePrimes(primes);
        primes[N_PRIMES - 1] = MAX_VALUE;
    }
}


class isPrimeTask implements Callable<int[]> {

    private BitSet toBeComputed;
    private int[] smallPrimes;
    private long startIndex;
    private long endIndex;
    private int ID;
    private int startOffset;
    private int block_increment;


    public isPrimeTask(BitSet toBeComputed, int[] smallPrimes, long startIndex, long endIndex, int ID, int startOffset, int block_increment) {
        super();
        this.toBeComputed = toBeComputed;
        this.smallPrimes = smallPrimes;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.ID = ID;
        this.startOffset = startOffset;
        this.block_increment = block_increment;
    }


    @Override
    public int[] call() {
        toBeComputed.set(0, (int) (endIndex - startIndex)); //setting all the bits in the bitset to true.

        //System.out.println(" cardinality.length is " + toBeComputed.cardinality());
        for (int p : smallPrimes) {
            long firstMultiple = (startIndex % p == 0) ? startIndex : p * (1 + startIndex / p);
            for (long multiple = firstMultiple; multiple <= endIndex; multiple += p) {
                toBeComputed.clear((int) (multiple - startIndex));
            }
        }

        int cardinality = toBeComputed.cardinality();
        int[] toReturn = new int[cardinality];

        int bit_index = toBeComputed.nextSetBit(0);
        int nextIndex = 0;
        while (bit_index >= 0 && nextIndex < cardinality) {
            int prime = bit_index + startOffset + block_increment * ID;
            toReturn[nextIndex++] = prime;
            bit_index = toBeComputed.nextSetBit(bit_index + 1);
        }

        return toReturn;
    }
}
