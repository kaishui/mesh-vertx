package com.mesh.web.service;

import com.mesh.web.entity.SQL;
import com.mesh.web.constant.CommonConstants;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SimpleBigQueryDirectorServiceImpl implements SQLDirectorService{

  @Autowired
  private List<SQLBuilder> builders;

  @Override
  public String construct(JsonObject sqlParamJson) {
    log.info("construct sql param json: {}", sqlParamJson);
    SQL sql = getSqlBuilder().select(sqlParamJson.getString("select"))
      .from(sqlParamJson.getString("from"))
      .join(sqlParamJson.getJsonObject("join").getString("type"),
        sqlParamJson.getJsonObject("join").getString("table"), sqlParamJson.getJsonObject("join").getString("condition"))
      .where(sqlParamJson.getString("where"))
      .groupBy(sqlParamJson.getString("groupBy"))
      .orderBy(sqlParamJson.getString("orderBy")).build();
    log.info("sql: {}", sql.toString());
    return sql.toString();
  }

  @Override
  public String type() {
    return CommonConstants.BIG_QUERY;
  }

  @Override
  public SQLBuilder getSqlBuilder() {
    return builders.stream().filter(builder -> builder.type().equals(type())).findFirst()
      .orElseThrow(() -> new RuntimeException("can not find a sqlBuilder bean"));
  }
}
