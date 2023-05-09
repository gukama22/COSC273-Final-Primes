// importing packages:
import java.util.ArrayList;
import java.util.concurrent.*;
import static java.lang.Math.*;

public class ParallelPrimes {

    //copied from
    public static final int MAX_VALUE = Integer.MAX_VALUE; //2^31-1 = 2147483647
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE);


    // replace this string with your team name
    public static final String TEAM_NAME = "Sunny-Day";

    public static void optimizedPrimes(int[] primes) {

        // getting the first primes until ROOT_MAX.
        int [] smallPrimes = Primes.getSmallPrimes();

        //
        int nextIndex = smallPrimes.length;

        // example found here: https://www.youtube.com/watch?v=zK_Gi5X0jpc
        System.arraycopy(smallPrimes, 0, primes, 0, smallPrimes.length);

        // creating the thread pool.
        int nThreads = Runtime.getRuntime().availableProcessors();

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        //partitioning the primes array in function of the number of threads in the thread pool.
        int block_increment =  (MAX_VALUE - ROOT_MAX)/nThreads;

        /*
        went with assigning larger tasks  because assigning smaller tasks to a pool is more computationally expensive.
         */

        // use a simple list //linkdedblockingqueue will execute the operation in a sequential manner.
        ArrayBlockingQueue<Future<boolean [] >> results = new ArrayBlockingQueue <Future<boolean[]>> (20);

        int numTasks = 0;


            for (long i = ROOT_MAX; i < MAX_VALUE; i+= block_increment) {

                //boolean to hold whether each number is a prime number.
                long endIndex = Math.min((i+block_increment), (MAX_VALUE));

                boolean [] isPrime = new boolean [(int) (endIndex - i)];

                results.add(pool.submit(new isPrimeTask(isPrime, smallPrimes, i , endIndex)));
                numTasks++;

            }

        /*
        array blocking queue take method?
        https://www.geeksforgeeks.org/arrayblockingqueue-take-method-in-java/
         */
        int start_offset = ROOT_MAX;

        /** each time we go down this loop, by removing the task at the head, the new Task is at position 0.

         */
        // reset what follows:
        for (int Task = 0; Task<numTasks; Task++)
         try {

         boolean [] result = results.take().get();
             for (int j = 0; j<result.length; j++){
             if((result[j])){
            primes[nextIndex++] = (start_offset) + (int) (result.length*Task) + j;
             }
         }

        } catch (Exception e) {
            e.printStackTrace();
        }

         primes[N_PRIMES - 1] =  MAX_VALUE;

        pool.shutdown();


}

     public static boolean debug_simple () {
        return false;
    }
   public static  boolean debug_complex () {
        return false;
    }


}

