// importing packages:

import java.util.ArrayList;
import java.util.concurrent.*;
import static java.lang.Math.*;

public class ParallelPrimes {

    //copied from ParallePrimes.
    public static final int MAX_VALUE = Integer.MAX_VALUE; //2^31-1 = 2147483647
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE);


    // replace this string with your team name
    public static final String TEAM_NAME = "Sunny-Day";
//    public static void optimizedPrimes(int[] primes) {
//        Primes.baselinePrimes(primes);
//    }

    public static void optimizedPrimes(int[] primes) {

        // getting the first primes until ROOT_MAX.
        int[] smallPrimes = Primes.getSmallPrimes();

        // example of the use of arraycopy found here: https://www.youtube.com/watch?v=zK_Gi5X0jpc

        // Initializing the prime numbers array by first storing the known small primes up to ROOT_MAX.
       // The 'arraycopy' method is used to copy the elements from the small primes array to the beginning of the primes array.
       // The remaining primes will be found and added to the primes array later.

        System.arraycopy(smallPrimes, 0, primes, 0, smallPrimes.length);

        // The next available index in the primes array is the one immediately after the end of the small primes.
        int nextIndex = smallPrimes.length;

        // determining  the number of available processors in the system to determine the optimal number of threads
        // to use for parallel processing.
        int nThreads = Runtime.getRuntime().availableProcessors();

        //creating a thread pool with a fixed number of threads based on the number of available processors.
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        //because finding which numbers from ROOT_MAX to MAX_VAlUE is computationally demanding, we
        // are partitioning the above-mentioned range into smaller intervals.
        // the size of each interval is determined by dividing the total range by the number of threads.

        int block_increment = (MAX_VALUE - ROOT_MAX) / nThreads;


        // I was first introduced to what an array blocking queue thanks to Prof. Rosenbaum:
        // https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/package-summary.html

        // an ArrayBlockingQueue is a data structure that will allow us to store the results of determining whether
        // numbers in a certain range are prime numbers.
        // Since the computations are done on an interval, the result of the operation is saved in
        // an array. We had to use a future object because it allows us to return something from
        // non-simultaneous computation.

        //I figured that the capacity of the arrayblockingqueue could be 20 through a series of trials and errors.
      //  ArrayBlockingQueue<Future<boolean[]>> results = new ArrayBlockingQueue<Future<boolean[]>>(20);
        ArrayList<Future<boolean[]>> results = new ArrayList<Future<boolean[]>>(20)  ;
        // Initializing a counter to keep track of the number of tasks created.
        int numTasks = 0;

        // Looping through the list of elements from ROOT_MAX to MAX_VALUE with a block_increment interval
        // in order to determine whether each number in the interval is a prime number.

        for (long i = ROOT_MAX; i < MAX_VALUE; i += block_increment) {

            //since the number of numbers between ROOT_MAX and MAX_VALUE might not be divisible by 8 (the number of my processors),
            // not all intervals need to be of block_increment size, especially not the last one.
          //  long endIndex = Math.min((i + block_increment), (MAX_VALUE)); //credits to Naila Thevenot for a useful conversation about why this line is useful

         //   boolean[] isPrime = new boolean[(int) (endIndex - i)];


            // Initializing a task to find prime numbers, submitting it to the thread pool, and adding the result to the results arrayBlockingQueue.
            // The tasks are submitted sequentially, and the results are added in the same order. However, there's no sequential restriction on how they are computed.
            results.add(pool.submit(new isPrimeTask(new boolean[(int) (Math.min((i + block_increment), (MAX_VALUE)) - i)], smallPrimes, i, Math.min((i + block_increment), (MAX_VALUE)))));
            numTasks++;

        }

        /*
        array blocking queue take method?
        https://www.geeksforgeeks.org/arrayblockingqueue-take-method-in-java/
         */
        int start_offset = ROOT_MAX;


// Iterate through each task and its corresponding results by dequeuing the tasks from the ArrayBlockingQueue using .take().get()
// method to avoid reading the results associated multiple times. For each boolean value that is true, since it corresponds to a prime number,
// add the prime number back to the primes array, which contains all the prime numbers between ROOT_MAX and MAX_VALUE. We also have to be mindful
// of the offset, we start filling the array by index = smallPrimes.length(), because we added the small primes to the bigger array.

        for (int Task = 0; Task < numTasks; Task++)
            try {

                boolean[] result = results.get(Task).get();
                for (int j = 0; j < result.length; j++) {
                    if ((result[j])) {
                        primes[nextIndex++] = (start_offset) + (int) (result.length * Task) + j;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        // The interval of numbers to be evaluated cannot include Integer.MAX_VALUE, so I intentionally had to add the
        // last number in the interval that is a prime number to the primes array.

        primes[N_PRIMES - 1] = MAX_VALUE;

        // After adding all prime numbers to the primes array, shut down the thread pool.
        pool.shutdown();

    }



}

