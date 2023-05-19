package com.mesh.web.controller;

import com.mesh.web.core.controller.RouterInterface;
import com.mesh.web.service.StrategyContextService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class SqlBuilderController implements RouterInterface {

  @Autowired
  private StrategyContextService strategyContextService;

  @Override
  public void router(Router router) {
    router.post("/generate/sql").handler(this::generateSql);
    router.post("/run/sql").handler(this::runSql);
  }

  private void runSql(RoutingContext routingContext) {
    log.info("run sql, parameter: {}", routingContext.body().asJsonObject());

    JsonObject jsonObject = routingContext.body().asJsonObject();

    //TODO: Validate json structure and sorting strategy - $project $from $match $group

    String sql = strategyContextService.parse(jsonObject);
    log.info("sql: {}", sql);
//    TODO: call jdbc query

    routingContext.response().setStatusCode(200).end(sql);
  }

  private void generateSql(RoutingContext routingContext) {
    log.info("generate sql, parameter: {}", routingContext.body().asJsonObject());

    JsonObject jsonObject = routingContext.body().asJsonObject();

    //TODO: Validate json structure and sorting strategy - $project $from $match $group

    String sql = strategyContextService.parse(jsonObject);
    log.info("sql: {}", sql);

    // todo: call bigquery to validate the sql

    routingContext.response().setStatusCode(200).end(sql);
  }
}
