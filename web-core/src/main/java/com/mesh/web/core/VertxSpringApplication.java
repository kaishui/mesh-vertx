package com.mesh.web.core;

import com.hazelcast.config.Config;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.spi.cluster.hazelcast.ConfigUtil;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

@ComponentScan("com.mesh")
@Configuration
@Slf4j
public class VertxSpringApplication {

  public static Vertx vertx;
  public static final int VERTICLE_COUNT = 20; // number of verticles, better two size of cpu


  public static void main(String[] args) {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
    System.setProperty("hazelcast.logging.type", "log4j2");
    System.setProperty("com.hazelcast.level", "DEBUG");

    Config config = ConfigUtil.loadConfig();
    config.setClusterName("mesh");

// 将 Spring 上下文与 Vertx Cluster 配置连接
    HazelcastClusterManager mgr = new HazelcastClusterManager(config);

// 让 Vertx 使用 HazelcastClusterManager，并启动集群
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx.clusteredVertx(options, ar -> {
      if (ar.succeeded()) {
        vertx = ar.result();

        // 创建 Spring 上下文
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(VertxSpringApplication.class);

        // 注册 Spring 上下文，并将其设置为可以创建 Vertx bean
        context.getBeanFactory().registerSingleton("vertx", vertx);
//        context.refresh();

        VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);
        vertx.registerVerticleFactory(verticleFactory);

        DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(1);
        for (int i = 0; i < VERTICLE_COUNT; i++) {
          vertx.deployVerticle(verticleFactory.prefix() + ":" + MainVerticle.class.getName(), deploymentOptions);
        }
      }
    });
  }


}
