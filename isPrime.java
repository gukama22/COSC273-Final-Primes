import java.util.concurrent.Callable;

// the callable interface is a functional interface in Java that represents a task that can be executed and returns a result.
class isPrimeCallable<T> implements Callable<boolean[]> {

    private int[] toBeComputed;
    private int[] smallPrimes;
    private int id;
    private int startIndex;
    private int endIndex;

    public isPrimeCallable(int[] toBeComputed, int[] smallPrimes, int id, int startIndex, int endIndex) {
        super();
        this.toBeComputed = toBeComputed;
        this.smallPrimes = smallPrimes;
        this.id = id;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

    }

    @Override
    public boolean[] call() throws Exception {

        boolean[] toBeReturned = new boolean[startIndex - endIndex];

        //initialize the array of primes to be returned to be all true.
        for (int i = 0; i < toBeReturned.length; i++) {
            toBeReturned[i] = true;
        }

        for (int p : smallPrimes) {

            // find the next number >= start that is a multiple of p
            int i = (startIndex % p == 0) ? startIndex : p * (1 + startIndex / p);
            i -= startIndex;
            //the previous line is to ensure that we don't  use the actual values of the


            while (i < toBeReturned.length) {
                toBeReturned[i] = false;
                i += p;
            }
        }

        return toBeReturned;
    }
}
