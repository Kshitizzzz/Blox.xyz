
import java.util.Deque;
import java.util.LinkedList;

// In this approach I am maintaining a callQueue which stores all the request.
// This approach helps in preventing the request when the rate limit exceeded

// Drawbacks
/*
 * 1. More memory is required.
 * 2. Less scalable
 * 3. High latency
 */

public class EnhancedRateLimiter {
    private final int maxCalls;
    private final long period; // in milliseconds
    private final Deque<Long> callTimestamps;
    private final Deque<Runnable> callQueue;

    public EnhancedRateLimiter(int maxCalls, long periodInSeconds) {
        this.maxCalls = maxCalls;
        this.period = periodInSeconds * 1000; // convert to milliseconds
        this.callTimestamps = new LinkedList<>();
        this.callQueue = new LinkedList<>();
    }

    public synchronized void callApi(Runnable apiCall) {
        callQueue.offer(apiCall);
        processQueue();
    }

    // Insert request in a queue
    private void processQueue() {
        long currentTime = System.currentTimeMillis();

        // Remove calls that are outside the time period
        while (!callTimestamps.isEmpty() && currentTime - callTimestamps.peek() > period) {
            callTimestamps.poll();
        }

        // Fetching request from the call queue and then removing the request
        // from the queue. And add the current time in the logs (callTimestamps queue)
        while (!callQueue.isEmpty() && callTimestamps.size() < maxCalls) {
            Runnable apiCall = callQueue.poll();
            callTimestamps.offer(currentTime);
            apiCall.run();
            currentTime = System.currentTimeMillis();
        }

        // If callQueue is not empty and rate limit exceeded, show message that the 
        // rate limit exceeded. 
        // In actual scenario show response code 429 - Too many requests
        if (!callQueue.isEmpty()) {
            long waitTime = period - (currentTime - callTimestamps.peek());
            System.out.println("Rate limit exceeded. Waiting for " + waitTime + " milliseconds.");
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            processQueue();
        }
    }

    public static void main(String[] args) {
        EnhancedRateLimiter rateLimiter = new EnhancedRateLimiter(15, 60);

        for (int i = 0; i < 20; i++) {
            final int callNumber = i;
            rateLimiter.callApi(() -> callMe("input_" + callNumber));
        }
    }

    public static void callMe(String input) {
        System.out.println("API called with input: " + input);
    }
}
