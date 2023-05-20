package com.mesh.web.core.controller;

import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class HealthCheckController implements RouterInterface{
  @Override
  public void router(Router router) {
    router.get("/health").handler(routingContext -> {
      log.info("health check");
      routingContext.response().setStatusCode(200).end("hello vertx!");
    });
  }
}
