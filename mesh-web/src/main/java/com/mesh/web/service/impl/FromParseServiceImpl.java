package com.mesh.web.service.impl;

import com.mesh.web.service.ParseStrategyService;
import com.mesh.web.service.StrategyContextService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * support $from element
 */
@Service
@Slf4j
public class FromParseServiceImpl implements ParseStrategyService {

  @Autowired
  private StrategyContextService strategyContextService;

  @Override
  public String parse(Object value) {
    // 如果值是一个字符串，直接返回表名
    if (value instanceof String) {
      return "from " + value;
    }
    // 如果值是一个数组，用逗号连接每个元素，并返回表名列表
    if (value instanceof JsonArray array) {
      StringBuilder sb = new StringBuilder();
      sb.append("from ");
      for (int i = 0; i < array.size(); i++) {
        sb.append(array.getString(i));
        if (i < array.size() - 1) {
          sb.append(", ");
        }
      }
      log.info("parse json array : {}", sb);
      return sb.toString();
    }
    // 子查询
    if (value instanceof JsonObject) {
      log.info("sub query: {}", value);
      String from = "from ( " +
                    strategyContextService.parse((JsonObject) value) + " ) ";
      log.info("from parse result: {}", from);
      return from;
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  @Override
  public String type() {
    return "$from";
  }
}
