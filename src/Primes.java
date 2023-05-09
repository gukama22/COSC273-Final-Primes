// A class for computing primes and other associated tasks.
// ***DO NOT MODIFY THIS FILE***

public class Primes {

    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE); //10,249
    public static final int MAX_SMALL_PRIME = 1 << 20; //1,048,576

    // Use the sieve of Eratosthenes to compute all prime numbers up
    // to max. The largest allowed value of max is MAX_SMALL_PRIME.
    public static int[] getSmallPrimesUpTo(int max) {

        // check that the value max is in bounds, and throw an
        // exception if not
        if (max > MAX_SMALL_PRIME) {
            throw new RuntimeException("The value " + max + "exceeds the maximum small prime value (" + MAX_SMALL_PRIME + ")");
        }

        //array must be for the small primes.
        // isPrime[i] will be true if and only if i is
        // prime. Initially set isPrime[i] to true for all i >= 2.
        boolean[] isPrime = new boolean[max];

        for (int i = 2; i < max; i++) {
            isPrime[i] = true;
        }



        // Apply the sieve of Eratosthenes to find primes. The
        // procedure iterates over values i = 2, 3,.... If isPrime[i]
        // == true, then i is a prime. When a prime value i is found,
        // set isPrime[j] = false for all multiples j of i. The
        // procedure terminates once we've examined all values i up to
        // Math.sqrt(max).
        int rootMax = (int) Math.sqrt(max);
        for (int i = 2; i < rootMax; i++) {
            if (isPrime[i]) {
                for (int j = 2 * i; j < max; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        // Count the number of primes we've found, and put them
        // sequentially in an appropriately sized array.
        int count = trueCount(isPrime);

        int[] primes = new int[count];
        int pIndex = 0;

        for (int i = 2; i < max; i++) {
            if (isPrime[i]) {
                primes[pIndex] = i; //any number x is at position isPrime[x].
                pIndex++;
            }
        }

        return primes;
    }

    // Count the number of true values in an array of boolean values,
    // arr
    public static int trueCount(boolean[] arr) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i])
                count++;
        }

        return count;
    }

    // Returns an array of all prime numbers up to ROOT_MAX
    public static int[] getSmallPrimes() {
        return getSmallPrimesUpTo(ROOT_MAX);
    }


    // Compute a block of prime values between start and start +
    // isPrime.length. Specifically, after calling this method
    // isPrime[i] will be true if and only if start + i is a prime
    // number, assuming smallPrimes contains all prime numbers of to
    // sqrt(start + isPrime.length).
    //remember that in order to find prime numbers, we exclude multiples (of each smallPrimes; hence we start by p in smallPrimes), hence we need the smallPrimes array.
    private static void primeBlock(boolean[] isPrime, int[] smallPrimes, int start) {

        // initialize isPrime to be all true
        for (int i = 0; i < isPrime.length; i++) {
            isPrime[i] = true;
        }

        for (int p : smallPrimes) {

            // find the next number >= start that is a multiple of p
            int i = (start % p == 0) ? start : p * (1 + start / p);
            i -= start;
            //the previous line is to ensure that we don't  use the actual values of the


            while (i < isPrime.length) {
                isPrime[i] = false;
                i += p;
            }
        }
    }

    // Compute the first primes.length prime numbers and write them
    // sequentially into the array primes.
    public static void baselinePrimes(int[] primes) {

        // compute small prime values
        int[] smallPrimes = getSmallPrimes();
        int nPrimes = primes.length;


        // write small primes to primes
        int count = 0;
        int minSize = Math.min(nPrimes, smallPrimes.length);
        for (; count < minSize; count++) {
            primes[count] = smallPrimes[count];
        }

        // check if we've already filled primes, and return if so
        if (nPrimes == minSize) {
            return;
        }

        // Apply the sieve of Eratosthenes to find primes. This
        // procedure partitions the sieving task up into several
        // blocks, where each block isPrime stores boolean values
        // associated with ROOT_MAX consecutive numbers. Note that
        // partitioning the problem in this way is necessary because
        // we cannot create a boolean array of size MAX_VALUE.
        boolean[] isPrime = new boolean[ROOT_MAX];
        for (long curBlock = ROOT_MAX; curBlock < MAX_VALUE; curBlock += ROOT_MAX) {
            primeBlock(isPrime, smallPrimes, (int) curBlock);
            for (int i = 0; i < isPrime.length && count < nPrimes; i++) {
                if (isPrime[i]) {
                    primes[count++] = (int) curBlock + i;
                }
            }
        }
    }
}
