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
        int[] smallPrimes = Primes.getSmallPrimes();

        // example of the use of arraycopy found here: https://www.youtube.com/watch?v=zK_Gi5X0jpc
        // since we want to compute all the primes up until MAX_VALUE, we can start storing some primes (that we found using Primes.getSmallPrimes())
        // in the array of ints that will be returned.
        System.arraycopy(smallPrimes, 0, primes, 0, smallPrimes.length);

        //finding the next index where
        int nextIndex = smallPrimes.length;

        // extracting of available processors in the system to determine the optimal number of threads
        // to use for parallel processing.
        int nThreads = Runtime.getRuntime().availableProcessors();

        //creating a thread pool with a fixed number of threads based on the number of available processors.
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        //because finding which numbers from ROOT_MAX to MAX_VAlUE is computationally demanding, we
        // are partitioning that range into smaller intervals.
        // the size of each interval is determined by dividing the total range by the number of threads.

        int block_increment = (MAX_VALUE - ROOT_MAX) / nThreads;


        // I was first introduced to what an array blocking queue thanks to Prof. Rosenbaum:
        // https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/package-summary.html

        // an ArrayBlockingQueue is a data structure that will allow us to store the results of determining whether
        // numbers in a certain range are prime numbers.
        // Since the computations is done on an interval, the result of the operation is saved in
        // an array. We had to use a future object because it allows us to return something from
        // non-simultaneous computation.

        //I figured that the capacity of the arrayblockingqueue could be 20 through a series of trials and errors.
        ArrayBlockingQueue<Future<boolean[]>> results = new ArrayBlockingQueue<Future<boolean[]>>(20);

        // Initializing a counter to keep track of the number of tasks created.
        int numTasks = 0;

        // Looping through the list of elements from ROOT_MAX to MAX_VALUE with a block_increment interval
        // in order to determine whether each number in the interval is a prime number.

        for (long i = ROOT_MAX; i < MAX_VALUE; i += block_increment) {

            //since the number of numbers between ROOT_MAX and MAX_VALUE might not be divisible by 8 (the number of my processors),
            // not all intervals need to be of block_increment size, especially not the last one.
            long endIndex = Math.min((i + block_increment), (MAX_VALUE)); //credits to Naila Thevenot for a useful conversation about why this line is useful

            boolean[] isPrime = new boolean[(int) (endIndex - i)];

// Initializing a task to find prime numbers, submitting it to the thread pool, and adding the result to the results arrayBlockingQueue.
// The tasks are submitted sequentially, and the results are added in the same order.
            results.add(pool.submit(new isPrimeTask(isPrime, smallPrimes, i, endIndex)));
            numTasks++;

        }

        /*
        array blocking queue take method?
        https://www.geeksforgeeks.org/arrayblockingqueue-take-method-in-java/
         */
        int start_offset = ROOT_MAX;

        // going through each task and the result that it gave by extracting it from the ArrayBlockingQueue via .take().get(), in order to dequeue it so that we don't
        // read through it more than once. Once you are in the specific results, go through each index and if the boolean associated to it is true, meaning that the number corresponding to that index is a prime number
        // add it back to primes array, which is a collection of all prime numbers between ROOT_MAX and MAX_VALUE.

        for (int Task = 0; Task < numTasks; Task++)
            try {

                boolean[] result = results.take().get();
                for (int j = 0; j < result.length; j++) {
                    if ((result[j])) {
                        primes[nextIndex++] = (start_offset) + (int) (result.length * Task) + j;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        primes[N_PRIMES - 1] = MAX_VALUE;

        pool.shutdown();


    }

    public static boolean debug_simple() {
        return false;
    }

    public static boolean debug_complex() {
        return false;
    }


}

