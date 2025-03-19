package com.mesh.web.core.components;

import com.mesh.web.core.VertxSpringApplication;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * get system / resource properties
 */

@Component
@Slf4j
public class Configurations {
  //Adding a private static final variable for ConfigRetriever
  private static ConfigRetriever configRetriever;

  @PostConstruct
  public void init() {
    ConfigStoreOptions systemProperties = new ConfigStoreOptions().setType("sys");
    ConfigStoreOptions envProperties = new ConfigStoreOptions().setType("env");
    ConfigStoreOptions yamlProperties = new ConfigStoreOptions().setType("file")
        .setFormat("yaml").setConfig(new JsonObject().put("path", "application.yaml"));

    ConfigStoreOptions propertiesProperties = new ConfigStoreOptions().setType("file")
        .setFormat("properties").setConfig(new JsonObject().put("path", "application.properties"));


    ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(propertiesProperties).addStore(yamlProperties)
      .addStore(systemProperties).addStore(envProperties);

    //Assigning the initialized ConfigRetriever to the static final variable
    configRetriever = ConfigRetriever.create(VertxSpringApplication.vertx, options);
  }

  public Future<JsonObject> getConfig() {
    return configRetriever.getConfig();
  }
}
