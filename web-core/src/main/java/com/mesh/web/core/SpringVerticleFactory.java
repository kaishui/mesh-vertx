package com.mesh.web.core;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class SpringVerticleFactory implements VerticleFactory {

  @Autowired
  private ApplicationContext context;

  public void setContext(ApplicationContext context) {
    this.context = context;
  }

  @Override
  public void init(Vertx vertx) {
    // do any initialization work here
  }


  @Override
  public void close() {
    // do any cleanup work here
  }

  @Override
  public String prefix() {
    return "mesh-prefix";
  }

  @Override
  public void createVerticle(String verticleName, ClassLoader classLoader, Promise<Callable<Verticle>> promise) {
//    String clazz =
    //verticleFactory load class
    String clazz = VerticleFactory.removePrefix(verticleName);
    promise.complete(() ->
      (Verticle) context.getBean(Class.forName(clazz))
    );
  }
}
