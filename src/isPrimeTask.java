import java.util.Arrays;
import java.util.concurrent.Callable;

public class isPrimeTask implements Callable <boolean[]>{

    private boolean[] toBeComputed;
    private int[] smallPrimes;

  //  private int id;
    private long startIndex;
    private long endIndex;

    public isPrimeTask(boolean[] toBeComputed, int[] smallPrimes,  long startIndex, long endIndex) {
        super();
        this.toBeComputed = toBeComputed;
        this.smallPrimes = smallPrimes;
       // this.id = id;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

    }

    @Override
    public boolean[] call()  {

        Arrays.fill(toBeComputed, true);

        for (int p : smallPrimes) {

            // find the next number >= start that is a multiple of p
            int i = (int) ((startIndex % p == 0) ? startIndex : p * (1 + startIndex / p));
            i -= startIndex;

            // finding more
            while (i < toBeComputed.length && i <= endIndex - startIndex) {
                toBeComputed[i] = false;
                i += p;
            }
        }
        return toBeComputed;
    }
}
