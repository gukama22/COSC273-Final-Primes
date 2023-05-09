import java.util.ArrayList;
import java.util.concurrent.*;

import static java.lang.Math.*;

public class ParallelPrimes {

    public static final int MAX_VALUE = Integer.MAX_VALUE; //2^31-1 = 2147483647
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE);
    public static final int MAX_SMALL_PRIME = 1 << 20; //1,048,576

    public static final int corecount = Runtime.getRuntime().availableProcessors();

    // replace this string with your team name
    public static final String TEAM_NAME = "Sunny-Day";

    public static void optimizedPrimes(int[] primes) {

        int [] smallPrimes = Primes.getSmallPrimes();

        int nextIndex = smallPrimes.length;

        // example found here: https://www.youtube.com/watch?v=zK_Gi5X0jpc
        System.arraycopy(smallPrimes, 0, primes, 0, smallPrimes.length);

        // creating the thread pool.
        int nThreads = Runtime.getRuntime().availableProcessors();

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        int block_increment =  (MAX_VALUE - ROOT_MAX)/nThreads;

        System.out.println(" the value of block_increment is " + block_increment);
        /*
        went with assigning larger tasks  because assigning smaller tasks to a pool is more computationally expensive.
         */

        //boolean to hold whether each number is a prime number.
        boolean [] isPrime = new boolean [block_increment];

        // use a simple list //linkedblockingqueue will execute the operation in a sequential manner.
        ArrayBlockingQueue<Future<boolean [] >> results = new ArrayBlockingQueue <Future<boolean[]>> (20);


        int numTasks = 0;


            for (long i = ROOT_MAX; i < MAX_VALUE; i+= block_increment) {


                long endIndex = Math.min((i+block_increment), (MAX_VALUE));
                results.add(pool.submit(new isPrimeTask(isPrime, smallPrimes, i , endIndex)));
                numTasks++;
          //! were in use until recently      System.out.println("iteration " + numTasks+ " started from i = " + (i) + " to endIndex = "+ endIndex);
          // ! was in use until recently      System.out.println("started from ");
            }

      //! were in use until recently.  System.out.println(" the size of the linkedblockingqueue is " + results.size());

            // just check the ending, the start and the leftover.

        /*
        array blocking queue take method?
        https://www.geeksforgeeks.org/arrayblockingqueue-take-method-in-java/
         */
        int start_offset = ROOT_MAX;
      //! was in use until recently  System.out.println(" is start_offset equal to " + start_offset + " primes[smallPrimes.length] " + primes[smallPrimes.length - 1] );


        /** each time we go down this loop, by removing the task at the head, the new Task is at position 0.

         */
        // reset what follows:
        for (int Task = 0; Task<numTasks; Task++)
         try {
             // removing and returning the head of the blocking queue
             // and the position of the boolean array in the blocking queue corresponds
             // to the index of the task.


         boolean [] result = results.take().get();

  //! was in use until recently
          //    System.out.println(" the value of result.length is " +result.length);

//             System.out.println("Task number " + Task + " and nextIndex is " + (nextIndex)
//                     + " and the end of nextIndex should be: " + (nextIndex+result.length)
//                     + " and the numbers at those indexes are: " +  start_offset + (int) (block_increment*Task) +  " and "
//                     + (int) (block_increment*Task) + result.length);

             ///!:!redo what follows! reset it.
       //      for (int j = 0; j<result.length && nextIndex <primes.length; j++) {
             for (int j = 0; j<2000; j++){
       // !! return this:   for (int j = 0; j<result.length; j++) {
             //j is the within block offset
             if((result[j]) && Task == 0){
            primes[nextIndex++] = (start_offset + 1) + (int) (result.length*Task) + j;

               //  System.out.println("thanks to task " + Task + " now next Index is " + nextIndex + " and value is " + ((start_offset + 1) + (int) (result.length*Task) + j));

        //     primes[start_offset + (int) (block_increment*Task) + j]
             }
         }


        } catch (Exception e) {
            e.printStackTrace();
        }


        pool.shutdown();





}

     public static boolean debug_simple () {
        return false;
    }
   public static  boolean debug_complex () {
        return false;
    }


}

