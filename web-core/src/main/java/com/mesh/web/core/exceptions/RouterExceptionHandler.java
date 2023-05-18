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
    //TODO: instanceof check

    routingContext.response().setStatusCode(500)
      .end("server internal error " + failure.getMessage() );

  }
}
