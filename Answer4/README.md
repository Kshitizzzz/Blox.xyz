# 4. There is an API that one must call to get data. The trouble is it will not let you cross the limit of call - say 15 calls per minute. If you cross the limit, the system penalizes you by one additional minute of penalty where you can not make any call. Here is how the API looks like: function string call_me(string input).
 

## 4.1. You would be able to use the API within the safe limit.

To use API within the safe limit we can use the Sliding window log algorithm (a rate limiter algorithm).
Implementation :

**Tracking Timestamps:**

 - Maintain a log of timestamps for each API request.
 - The log can be stored in a cache or in-memory data structure (e.g., a sorted set in Redis or an array).

**Handling New Requests:**
 
 - When a new request is made, record the current timestamp.
 - Remove outdated timestamps from the log. Outdated timestamps are those older than the start of the current time window (1 minute ago).

**Checking the Rate Limit:**

 - After updating the log, check the number of timestamps.
 - If the count is less than or equal to the allowed limit (15), the request is allowed.
 - If the count exceeds the limit, the request is dropped.

NOTE - Rate limiting implements by this algorithm is very accurate and will not exceed the rate limit.


## 4.2 What happens if you are supposed to call the API 20 times per minute? Is there any way to accomplish this?

**Queue Implementation:**

 - Use a queue to hold the incoming requests that exceed the rate limit.
 - Process the requests in the queue as soon as the rate limiter allows more requests.

**Request Throttling:**

 - Distribute the requests evenly over time to ensure you don’t exceed the rate limit.
    - Instead of making all 20 requests in a burst, spread them out evenly.
    - If you can only make 15 requests per minute, spread them to one request every 4 seconds.
    - For the remaining 5 requests, wait until the next minute.

**Batching Requests:**

 - If the API supports batching multiple requests into a single call, use this feature.
    - Combine multiple smaller requests into a single larger request if the API allows it.
    - This reduces the number of API calls.

**Handling High Traffic:**

 - In cases of high traffic, ensure the queue does not grow indefinitely by implementing a maximum queue size or using a priority queue.
 - Consider handling older requests first (FIFO - First In, First Out) to maintain fairness.

**Drawbacks:**

 - Increased memory usage due to the queue.
 - Potential delays in processing requests, especially if the queue becomes large.

## 4.3 If you were the API designer, what would you do to implement this behavior? 

To implement this rate limiter behaviour on server side 

**Define Rate Limit Parameters**

  - Limit: Maximum number of requests allowed.
  - Window: Time window within which the limit is applicable (e.g., 100 requests per minute).

**Tracking Requests:**

 - Maintain a record of API calls made by each client within a rolling time window.
 - Use a data store like Redis to track timestamps or counts of requests for each client.

**Check Request:**

  - When a new request is made, remove timestamps that are outside the current window.
  - Check if the remaining timestamps plus the new request exceed the rate limit.

**Returning Rate Limiting Status:**

 - When the limit is exceeded, respond with an appropriate HTTP status code (429 Too Many Requests).
 - Include headers in the response indicating the rate limit status and retry information (e.g., Retry-After header).

**Implementing a Penalty Period:**

 - Introduce a cooldown period if the client exceeds the rate limit. During this period, all incoming requests from the client are rejected.
 - This can be implemented by setting a flag or a timeout in the data store when the limit is exceeded.

**Implement Rate Limiting Logic**

 - Initialize the Log : When a request is made, check if the log exists for the user. If not, initialize it.
 - Prune old Request : Remove entries from the log that are outside the current time window. This keeps the log size manageable and relevant.
 - Count Requests : Count the number of requests in the current time window.
 - Compare with Limit : Compare the count with the defined limit.
 - Handle Concurrency - Ensure atomic operations for adding and pruning log entries to handle concurrent requests efficiently.
