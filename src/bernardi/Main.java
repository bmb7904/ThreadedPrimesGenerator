package bernardi;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This application will generate primes numbers from 1 - Max, where max is specified
 * by the user. This will generate primes in three different ways. One with concurrent
 * multithreading, another with non-concurrent multithreading, and finally, with no
 * multithreading.
 */
public class Main {

    // "global" array that stores the primes
    static ArrayList<Long> listOfPrimes = new ArrayList<>();

    static class WorkerThread implements Runnable {

        long start;
        long stop;

        // Constructor
        WorkerThread(long start, long stop) {
            this.start = start;
            this.stop = stop;
        }

        @Override
        public void run() {
            for(long i = start; i <= stop; i++) {
                if(isPrime(i)) {
                    listOfPrimes.add(i);
                }
            }
        }
    }

    public static void main(String[] args) {
        final long maxRange;
        final int numThreads = 10;
        Thread[] threads = new Thread[numThreads];
        WorkerThread[] workerThreads = new WorkerThread[numThreads];

        Scanner input = new Scanner(System.in);

        System.out.print("Please enter the Max Range: ");
        maxRange = input.nextLong();

        System.out.println("I will find all the prime numbers between 1 - " + maxRange);

        for(int i = 1; i <= numThreads; i++) {
            if(i == 1) {
                long tempEnd = maxRange / numThreads;
                workerThreads[i-1] = new WorkerThread(1, maxRange / numThreads);
                System.out.println("1 - " + tempEnd);
            }

            else if(i == numThreads) {
                if(maxRange % numThreads == 0) {
                    long tempStart = (maxRange/numThreads) * (i-1) + 1;
                    long tempEnd = ((maxRange/numThreads) * i);
                    workerThreads[i-1] = new WorkerThread(tempStart, tempEnd);
                    System.out.println(tempStart + " - " + tempEnd);
                }
                // not evenly divisible into maxRange
                else {
                    long extra = maxRange - ((maxRange / numThreads) * numThreads);
                    long tempStart = (maxRange/numThreads) * (i-1) + 1;
                    long tempEnd = ((maxRange/numThreads) * i) + extra;
                    workerThreads[i-1] = new WorkerThread(tempStart, tempEnd);
                    System.out.println(tempStart + " - " + tempEnd);
                }
            }

            else {
                long tempStart = (maxRange/numThreads) * (i-1) + 1;
                long tempEnd = ((maxRange/numThreads) * i);
                workerThreads[i-1] = new WorkerThread(tempStart, tempEnd);
                System.out.println(tempStart + " - " + tempEnd);
            }
        }

        /*
         * Here, we are splitting up the work among the threads, and having the threads
         *  do the calculations concurrently.
         */

        // create Threads
        for(int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(workerThreads[i]);
        }

        long startTime = System.nanoTime();

        for(int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for(int j = 0; j < threads.length; j++) {

            try {
                threads[j].join();
            }
            catch (InterruptedException e) {
                System.out.println("Problem with joining threads!");
            }
        }

        printPrimes();
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;

        /*
         * Calculating primes with multithreading, but with each thread running in
         * sequence, where the next thread won't execute until the previous thread is
         * done. No concurrency.
         */

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        // clear global arraylist of primes
        listOfPrimes = null;
        listOfPrimes = new ArrayList<Long>();

        threads = null;
        threads = new Thread[numThreads];
        for(int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(workerThreads[i]);
        }



        // here we are joining each thread directly after calling the start method
        long startTime2 = System.nanoTime();
        for(int i = 0; i < threads.length; i++) {
            threads[i].start();
            try {
                threads[i].join();
            }
            catch (InterruptedException e) {
                System.out.println("Problem with joining threads!");
            }

        }
        printPrimes();
        long endTime2 = System.nanoTime();
        long totalTime2 = endTime2 - startTime2;

        /*
         * Finally, we are calculating the primes with just one thread.
         */

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        listOfPrimes = null;
        listOfPrimes = new ArrayList<>();

        long startTime3 = System.nanoTime();
        for(long i = 1; i < maxRange; i++) {
            int numFactors = 0;
            for(long j = 1; j <= i; j++) {
                if(i % j == 0) {
                    numFactors++;
                }
            }
            if(numFactors == 2) {
                listOfPrimes.add(i);
            }
        }

        printPrimes();
        long endTime3 = System.nanoTime();
        long totalTime3 = endTime3 - startTime3;
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.printf("Time: Concurrent Multithreading: %d \n " +
                          "  Non-Concurrent Mulithreading: %d \n " +
                          "               Single-Threaded: %d \n",
                                                 totalTime, totalTime2, totalTime3);

    }

    /**
     * Checks the long argument to see if it is prime. This is not at all optimized.
     * Just a very "dumb" brute force algorithm.
     * @param num
     * @return boolean true if prime, false if now
     */
    private static boolean isPrime(long num) {
        int numFactors = 0;

        for(long i = 1; i <= num; i++) {
            if(num % i == 0) {
                numFactors++;
            }
        }

        return numFactors == 2;
    }

    /**
     * Prints out all primes in global arrayList
     */
    private static void printPrimes() {
        int index = 0;
        for(long prime:listOfPrimes) {
            System.out.print(prime + " ");
            if(index == 40) {
                // add new line
                System.out.println();
                index = 0;
            }
            index++;
        }

        System.out.println();
    }
}
