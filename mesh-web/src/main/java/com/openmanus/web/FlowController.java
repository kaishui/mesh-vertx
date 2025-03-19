package com.openmanus.web;

import com.openmanus.flow.PlanningFlow;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FlowController {

  private final PlanningFlow planningFlow;
  private final Vertx vertx;

  @Autowired
  public FlowController(PlanningFlow planningFlow, Vertx vertx) {
    this.planningFlow = planningFlow;
    this.vertx = vertx;
  }

  public Handler<RoutingContext> handleFlow() {
    return routingContext -> {
      JsonObject requestBody = routingContext.body().asJsonObject();
      String message = requestBody.getString("message");

      if (message == null || message.trim().isEmpty()) {
        routingContext.response()
          .setStatusCode(400)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("error", "Message is required").encode());
        return;
      }

      log.info("Received message: {}", message);

      planningFlow.execute(message)
        .onSuccess(result -> {
          log.info("Flow completed successfully: {}", result);
          routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .end(new JsonObject().put("result", result).encode());
        })
        .onFailure(throwable -> {
          log.error("Flow failed: {}", throwable.getMessage(), throwable);
          routingContext.response()
            .setStatusCode(500)
            .putHeader("content-type", "application/json")
            .end(new JsonObject().put("error", "Flow execution failed: " + throwable.getMessage()).encode());
        });
    };
  }
}
