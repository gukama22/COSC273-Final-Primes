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

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        int block_increment = ((MAX_VALUE - ROOT_MAX) / nThreads);
        int numTasks =(int)Math.ceil((double)(MAX_VALUE - ROOT_MAX) / block_increment) ;

        ArrayList<Future<int[]>> results = new ArrayList<Future<int[]>>(numTasks)  ;

        int start_offset = ROOT_MAX;
        int num_Task = 0;
        int Task_number  = 0;
        try {
        for (long i = ROOT_MAX; i < MAX_VALUE; i += block_increment) {
            long endIndex = Math.min((i + block_increment), (MAX_VALUE));
            results.add(pool.submit(new isPrimeTask(new BitSet((int)(endIndex - i +1)), smallPrimes, i, endIndex, Task_number, start_offset, block_increment)));
            Task_number++;
        }

for (Future<int []> result_1 : results){
    int[] result = result_1.get();

    System.arraycopy(result, 0, primes,  nextIndex, result.length);
    num_Task ++;
    nextIndex = nextIndex+ result.length;
}

       } catch ( Exception f){ //InterruptException is thrown when a thread is interrupted while it is waiting, sleeping or blocked for some reasonl
          // an ExecutionException will be thrown to indicate that an error occurred during the execution of a task in the thread pool.
            f.printStackTrace();
        }

     //   Primes.baselinePrimes(primes);
        //primes[N_PRIMES - 1] = MAX_VALUE;
        pool.shutdown();
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

    }

    @Override
    public int[] call() {
        toBeComputed.set(0, (int)(endIndex - startIndex)); //setting all the bits in the bitset to true.

       // System.out.println(" AT FIRST from start_index " + startIndex + " the number of prime numbers is " + toBeComputed.cardinality());
        for (int p : smallPrimes) {
            int i = (int) ((startIndex % p == 0) ? startIndex : p * (1 + startIndex / p));
            i -= startIndex;
            while (i <  (int)(endIndex - startIndex + 1)) {
                toBeComputed.set(i, false);
                i += p;
            }
        }
        System.out.println( " for "  + ID + " from start_index " + startIndex + " the number of prime numbers is " + toBeComputed.cardinality());
        int [] toReturn = new  int [toBeComputed.cardinality()];

        int bit_index =  toBeComputed.nextSetBit(0);

        System.out.println(" bit_index equals " + bit_index + " and the number of bits is equal to " + toBeComputed.cardinality());
        for(int i = 0; i<toReturn.length; i++){
            toReturn[i] = bit_index + startOffset +  block_increment* ID;
            bit_index = (toBeComputed.nextSetBit(bit_index + 1));


           // bit_index = (result.nextSetBit(bit_index + 1));
        }
        if(ID == 0){
        for( int k = 0; k< 23; k++){
            System.out.println(" printing what's inside " + toReturn[k] + " for task " + ID);
        }}

        return toReturn;
    }

}


