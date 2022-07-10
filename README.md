- <h3>picture_processing_service</h3>
  Аn asynchronous microservice that converts user photos to black and white. Written on Spring Boot. To provide fault-tolerance, a request queue was manually implemented. <br>
  <h5> proprety file settings: </h5>

  - spring.servlet.multipart.max-file-size - maximum allowed file size (2MB by default)
  - spring.cache.caffeine.spec=maximumSize - maximum allowed cache size (10 pictures by default; It is recommended to disable caching during stress testing - just set this value = 0)
  - core-pool-size - thread count in thread pool (8 threads by default)
  - task-queue-capacity - maximum allowed task queue capacity (300 requests by default)

  <h5> endpoint - picture/convert : </h5>

  - method type - POST
  - param name - inputImage
  - param type - ".jpg" or ".jpeg" or ".jpe" file
  
- <h3>picture_sending_service</h3>
  А simple microservice for stress testing. Sends asynchronous requests to the first service.

    <h5> proprety file settings: </h5>

    - test_image - image to be sent (/test_image.jpg by default)
    - request_count - count of requests to be sent (300 requests by default)
    - interval_between_requests - interval between sending (0ms by default)

