import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.*;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.*;

public class ParallelPrimes {

    // replace this string with your team name
    public static final String TEAM_NAME = "baseline";

    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE);
    public static final int MAX_SMALL_PRIME = 1 << 20;
    public static final int nThreads = Runtime.getRuntime().availableProcessors();

    public static void optimizedPrimes(int[] primes) {

        int[] smallPrimes = Primes.getSmallPrimes();
        System.arraycopy(smallPrimes, 0, primes, 0, smallPrimes.length);

        int nextIndex = smallPrimes.length;
        System.out.println(" the nextIndex is " + nextIndex);

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        int block_increment = ((MAX_VALUE - ROOT_MAX) / nThreads);
        int numTasks = (int) Math.ceil((double) (MAX_VALUE - ROOT_MAX) / block_increment);

        ArrayList<Future<int[]>> results = new ArrayList<Future<int[]>>(numTasks);

        int start_offset = ROOT_MAX;
        int Task_number = 0;

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
        toBeComputed.set(0, (int)(endIndex - startIndex)); //setting all the bits in the bitset to true.

        for (int p : smallPrimes) {
            long firstMultiple = (startIndex % p == 0) ? startIndex : p * (1 + startIndex / p);
            for (long multiple = firstMultiple; multiple <= endIndex; multiple += p) {
                toBeComputed.clear((int)(multiple - startIndex));
            }
        }

        System.out.println(" for task " + ID + " the cardinality equals " + toBeComputed.cardinality());
        int cardinality = toBeComputed.cardinality();
        int [] toReturn = new int[cardinality];

        int bit_index = toBeComputed.nextSetBit(0);
        int nextIndex = 0;
        while (bit_index >= 0 && nextIndex < cardinality) {
            int prime = bit_index + startOffset + block_increment * ID;
            toReturn[nextIndex++] = prime;
            bit_index = toBeComputed.nextSetBit(bit_index + 1);
        }

        return toReturn;
    } }
