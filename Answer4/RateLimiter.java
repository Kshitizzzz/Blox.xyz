import java.util.Deque;
import java.util.LinkedList;

public class RateLimiter {
    // Max API calls in a given timestamp.
    private final int maxCalls;
    // Timestamp in milliseconds
    private final long period;
    // Maintaining a queue to log timestamps.
    private final Deque<Long> callTimestamps;

    public RateLimiter(int maxCalls, long periodInSeconds) {
        this.maxCalls = maxCalls;
        this.period = periodInSeconds * 1000; // convert to milliseconds
        this.callTimestamps = new LinkedList<>();
    }

    public synchronized void callApi(Runnable apiCall) {
        long currentTime = System.currentTimeMillis();

        // Remove calls that are outside the time period
        while (!callTimestamps.isEmpty() && currentTime - callTimestamps.peek() > period) {
            callTimestamps.poll();
        }

        // if the limit not reached its threshold, insert the timestamp in the queue.
        if (callTimestamps.size() < maxCalls) {
            callTimestamps.offer(currentTime);
            apiCall.run();
        } 
        // cooldown period by not accepting any request further if the limit reaches the threshold
        else {
            long waitTime = period - (currentTime - callTimestamps.peek());
            System.out.println("Rate limit exceeded. Waiting for " + waitTime + " milliseconds.");
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            callApi(apiCall);
        }
    }

    public static void main(String[] args) {
        // Setting the rate limit and time
        RateLimiter rateLimiter = new RateLimiter(15, 60);

        for (int i = 0; i < 20; i++) {
            final int callNumber = i;
            rateLimiter.callApi(() -> call_me("input_" + callNumber));
        }
    }

    public static void call_me(String input) {
        System.out.println("API called with input: " + input);
    }
}
