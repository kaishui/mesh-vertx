package com.mesh.web.core.exceptions;

import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RouterExceptionHandler {

  public void handler(RoutingContext routingContext) {
    log.error("RouterExceptionHandler", routingContext.failure());

    Throwable failure = routingContext.failure();
    //Check if the failure is an instance of an exception class
    if (failure instanceof Exception) {
        routingContext.response().setStatusCode(500)
            .end("server internal error " + failure.getMessage());
    }
    //If the failure is not an instance of an exception class, then log the error
    else {
        log.error("Unexpected error occured", routingContext.failure());
    }
  }
}
