# spring-playground-redis
Spring Boot Application using Lettuce to create a pubsub channel

## Running
`mvn package spring-boot:run`

## Testing
All methods are GETs so you can use the browser.

* `GET /channels` - Returns the subscribed channels
* `GET /messages` - Returns the messages received on each channel
* `GET /subscribe/{channel}` - Subscribe to a channel
* `GET /publish/{channel}/{message}` - Publish a message to some channel
