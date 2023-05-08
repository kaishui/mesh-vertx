package com.mesh.web.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.validation.RequestParameter;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MainVerticle extends AbstractVerticle {

  @Override

  public void start(Promise<Void> startPromise) throws Exception {

    Promise<Router> promise = Promise.promise();
    RouterBuilder.create(vertx, "openapi.yaml")
      .onSuccess(routerBuilder -> {
        routerBuilder
          .operation("listPets")
          .handler(routingContext -> {
            RequestParameters params =
              routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY);
            RequestParameter body = params.body();
            JsonObject jsonBody = body.getJsonObject();
            log.info("successful to load openapi.yaml, {}", jsonBody);
//            routingContext
//              .response()
//              .setStatusCode(200)
//              .setStatusMessage(jsonBody.toString())
//              .end();
          })
          .failureHandler(routingContext -> {
            // Handle failure
            log.error("failed", routingContext.failed());

          });

        Router router = routerBuilder.createRouter();

        promise.complete(router);
      })
      .onFailure(err -> {
        log.error("start failed", err);
      });

    promise.future().onSuccess(router -> {
      router.route("/*").handler(StaticHandler.create());

      HttpServer server = vertx.createHttpServer();
      server.requestHandler(router).listen(8888, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port 8888");
        } else {
          startPromise.fail(http.cause());
        }
      });
    });

  }
}
