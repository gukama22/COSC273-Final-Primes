import java.util.Arrays;

public class primeBlockTask implements Runnable {
    private boolean[] isPrime;
    private int[] smallPrimes;
    private int start;
    private int id;

    public primeBlockTask(boolean[] ip, int[] sp, int s, int i) {
        isPrime = ip;
        smallPrimes = sp;
        start = s;
        id = i;
    }

    @Override
    public void run() {
        // initialize isPrime to be all true
        Arrays.fill(isPrime, true);

        for (int p : smallPrimes) {
            // find the next number >= start that is a multiple of p
            int i = (start % p == 0) ? start : p * (1 + start / p);
            i -= start;

            while (i < isPrime.length) {
                isPrime[i] = false;
                i += p;
            }
        }
    }
}
