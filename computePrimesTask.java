// because

public class computePrimesTask implements Runnable{

    private boolean [] isPrime;
    private int []primes;
    private int startIndex;

    public  computePrimesTask( boolean []isPrimes, int [] primes, int startIndex){
        super();
       this.startIndex = startIndex;
       this.isPrime = isPrime;
       this.primes = primes;
    }

    public void run() {

    }
}
