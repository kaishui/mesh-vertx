package com.mesh.web.core;

import com.mesh.web.core.components.Configurations;
import com.mesh.web.core.controller.RouterInterface;
import com.mesh.web.core.exceptions.RouterExceptionHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MainVerticle extends AbstractVerticle {

  @Autowired
  private List<RouterInterface> routerInterfaces;

  @Autowired
  private RouterExceptionHandler routerExceptionHandler;

  @Autowired
  private Configurations configurations;


  @Override
  public void start(Promise<Void> startPromise) throws Exception {


    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

//    router.route("/*").handler(StaticHandler.create());
    router.route().handler(StaticHandler.create());
    router.route().handler(BodyHandler.create()).failureHandler(routerExceptionHandler::handler);

    routerInterfaces.forEach(routerInterface -> routerInterface.router(router));


    configurations.getConfig().onSuccess(config -> {
      int port = config.getJsonObject("server").getInteger("port");

      log.info("server port: {}", port);
      server.requestHandler(router).listen(port, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port " + port);
        } else {
          startPromise.fail(http.cause());
        }
      });
    });


  }
}
