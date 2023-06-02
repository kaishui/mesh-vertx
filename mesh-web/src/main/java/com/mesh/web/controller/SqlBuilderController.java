package com.mesh.web.controller;

import com.mesh.web.core.controller.RouterInterface;
import com.mesh.web.service.StrategyContextService;
import com.mesh.web.util.ParamConverter;
import com.mesh.web.util.ParamParser;
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
    router.get("/run/sql").handler(this::parseSql);
  }

  private void parseSql(RoutingContext routingContext) {
    log.info("parse sql, parameter: {}", routingContext.body().asJsonObject());
    String requestParam = routingContext.request().getParam("requestParam");

    // 创建解析器和生成器
    ParamParser parser = new ParamParser(requestParam);
    // 解析参数
    ParamConverter generator = new ParamConverter(parser);

    log.info("requestParam: {}", requestParam);
    boolean isValidJson = true;
    if (isValidJson) {
      // Use event bus to replace this
      String sql = strategyContextService.parse(generator.getOutput());
      log.info("sql: {}", sql);
      // call jdbc query
//      callJdbcQuery(sql);
      routingContext.response().setStatusCode(200).end(sql);
    } else {
      routingContext.response().setStatusCode(400).end("Invalid json structure");
    }
  }

  private void runSql(RoutingContext routingContext) {
    log.info("run sql, parameter: {}", routingContext.body().asJsonObject());

    JsonObject jsonObject = routingContext.body().asJsonObject();

    //TODO: Use event bus to replace this

    // Validate json structure and sorting strategy - $project $from $match $group
//    boolean isValidJson = validateJson(jsonObject);
    boolean isValidJson = true;
    if (isValidJson) {
      // Use event bus to replace this
      String sql = strategyContextService.parse(jsonObject);
      log.info("sql: {}", sql);
      // call jdbc query
//      callJdbcQuery(sql);
      routingContext.response().setStatusCode(200).end(sql);
    } else {
      routingContext.response().setStatusCode(400).end("Invalid json structure");
    }
  }

  private void generateSql(RoutingContext routingContext) {
    log.info("generate sql, parameter: {}", routingContext.body().asJsonObject());

    JsonObject jsonObject = routingContext.body().asJsonObject();

    // Validate json structure and sorting strategy - $project $from $match $group
//    boolean isValidJson = validateJson(jsonObject);
    boolean isValidJson = true;

    if (isValidJson) {
      String sql = strategyContextService.parse(jsonObject);
      log.info("sql: {}", sql);
      // call bigquery to validate the sql
//      boolean isValidSql = callBigqueryValidate(sql);
      boolean isValidSql = true;
      if (isValidSql) {
        routingContext.response().setStatusCode(200).end(sql);
      } else {
        routingContext.response().setStatusCode(400).end("Invalid sql");
      }
    } else {
      routingContext.response().setStatusCode(400).end("Invalid json structure");
    }
  }
}
