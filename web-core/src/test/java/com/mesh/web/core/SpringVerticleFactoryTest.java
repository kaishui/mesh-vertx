package com.mesh.web.core;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({VertxExtension.class})
public class SpringVerticleFactoryTest {

  Vertx vertx = Vertx.vertx();


  @Before
  public void setUp() throws Exception {
    // Initialize mocks created above
//    MockitoAnnotations.initMocks(context);    // Change behaviour of `resource`
  }

  @Test
  public void testInitVerticle() {
    SpringVerticleFactory factory = new SpringVerticleFactory();
    factory.init(vertx);
    // Assert that factory initialization is successful
  }

  @Test
  public void testCloseVerticle() {
    SpringVerticleFactory factory = new SpringVerticleFactory();
    factory.close();
    // Assert that factory is closed successfully
  }

  @Test
  public void testPrefix() {
    SpringVerticleFactory factory = new SpringVerticleFactory();
    String prefix = factory.prefix();
    // Assert that prefix has the expected value
    assertEquals(prefix, "mesh-prefix");
  }

  @Test
  public void testCreateVerticle() throws Exception {
    ApplicationContext context = Mockito.mock(ApplicationContext.class);

    String verticleName = MainVerticle.class.getName();
    ClassLoader classLoader = getClass().getClassLoader();
    SpringVerticleFactory factory = new SpringVerticleFactory();
    Promise<Callable<Verticle>> promise = Promise.promise();
    // create an instance of the verticle and register it
    Verticle verticleMock = mock(Verticle.class);

    when(context.getBean(ArgumentMatchers.any(Class.class))).thenReturn(verticleMock);
    factory.setContext(context);
    factory.createVerticle(verticleName, classLoader, promise);
    Callable<Verticle> callable = promise.future().result();
    // Assert that callable returns the expected verticle
    assertEquals(callable.call(), verticleMock);
  }


}
