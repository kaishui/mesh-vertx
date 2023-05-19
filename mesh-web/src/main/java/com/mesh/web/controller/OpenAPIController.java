package com.mesh.web.controller;

import com.mesh.web.core.controller.RouterInterface;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class OpenAPIController implements RouterInterface {
  @Override
  public void router(Router router) {
    router.get("/docs").handler(this::generateOpenApi);
  }

  /**
   * redirect to static file with openapi
   * @param routingContext
   */
  private void generateOpenApi(RoutingContext routingContext) {
    //TODO : redirect to openapi
    routingContext.response().setStatusCode(200).sendFile("index.html");
  }
}
