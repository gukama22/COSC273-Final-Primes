import java.util.*;
import java.util.concurrent.*;

public class ParallelPrimes {
    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE);
    public static final String TEAM_NAME = "Sunny-Day";

    public static void optimizedPrimes(int[] primes) {
        int[] smallPrimes = Primes.getSmallPrimes();
        System.arraycopy(smallPrimes, 0, primes, 0, smallPrimes.length);

        int nextIndex = smallPrimes.length;
        int numPrimes = nextIndex;
        int nThreads = Runtime.getRuntime().availableProcessors();

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        int block_increment = Math.max(1_000_000, (MAX_VALUE - ROOT_MAX) / nThreads);

        int numTasks = (int)Math.ceil((double)(MAX_VALUE - ROOT_MAX) / block_increment);
        ArrayList<Future<BitSet>> results = new ArrayList<>(numTasks);

        for (long i = ROOT_MAX; i < MAX_VALUE; i += block_increment) {
            long endIndex = Math.min((i + block_increment), (long) MAX_VALUE);
            results.add(pool.submit(new IsPrimeTask(new BitSet((int)(endIndex - i + 1)), smallPrimes, i, endIndex)));
        }

        int start_offset = ROOT_MAX;
        try {
            for (int Task = 0; Task < results.size(); Task++){
                BitSet result = results.get(Task).get();
                for(int bit_index = result.nextSetBit(0); bit_index >= 0 && numPrimes < N_PRIMES - 1; bit_index = result.nextSetBit(bit_index + 1)){
                    primes[nextIndex++] = bit_index + start_offset + block_increment * Task;
                    numPrimes++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        primes[N_PRIMES - 1] = MAX_VALUE;
        pool.shutdown();
    }
}



// A Callable class that calculates prime numbers in a block
class IsPrimeTask implements Callable<BitSet> {
    private BitSet toBeComputed;
    private int[] smallPrimes;
    private long startIndex;
    private long endIndex;

    public IsPrimeTask(BitSet toBeComputed, int[] smallPrimes, long startIndex, long endIndex) {
        super();
        this.toBeComputed = toBeComputed;
        this.smallPrimes = smallPrimes;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        toBeComputed.set(0, (int)(endIndex - startIndex + 1));
    }

    @Override
    public BitSet call() {
        for (int p : smallPrimes) {
            long firstMultiple = (startIndex % p == 0) ? startIndex : p * (1 + startIndex / p);
            for (long multiple = firstMultiple; multiple <= endIndex; multiple += p) {
                toBeComputed.clear((int)(multiple - startIndex));
            }
        }
        return toBeComputed;
    }

}
