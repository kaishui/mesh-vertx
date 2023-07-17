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
    StringBuilder sb = new StringBuilder();
    sb.append("from ");

    if (value instanceof String) {
      sb.append(value);
    }
    // 如果值是一个数组，用逗号连接每个元素，并返回表名列表
    if (value instanceof JsonArray array) {
      for (int i = 0; i < array.size(); i++) {
        sb.append(array.getString(i));
        if (i < array.size() - 1) {
          sb.append(", ");
        }
      }
    }
    // 子查询
    if (value instanceof JsonObject json) {
      log.info("sub query: {}", value);
      //意味着是一个子查询
      if (((JsonObject) value).containsKey("$project")) {
        sb.append("(").append(strategyContextService.parse((JsonObject) value)).append(") ");
      } else {
        for (String k : json.fieldNames()) {
          Object valObj = json.getValue(k);
          // enhance from
          if (valObj instanceof JsonObject) {
            sb.append("(").append(strategyContextService.parse((JsonObject) valObj)).append(") ").append(" as ").append(k).append(" ");
          } else {
            //for string
            sb.append(valObj).append(" as ").append(k).append(" ");
          }

        }
      }
    }
    log.info("from parse result: {}", sb);
    return sb.toString();
  }

  @Override
  public String type() {
    return "$from";
  }
}
