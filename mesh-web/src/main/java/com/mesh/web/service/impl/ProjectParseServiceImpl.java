package com.mesh.web.service.impl;

import com.mesh.web.service.OperationContextService;
import com.mesh.web.service.ParseStrategyService;
import com.mesh.web.service.StrategyContextService;
import com.mesh.web.util.OperatorUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * support $from element
 */
@Service
@Slf4j
public class ProjectParseServiceImpl implements ParseStrategyService {

  @Autowired
  private StrategyContextService strategyContextService;
  @Autowired
  private OperationContextService operationContextService;

  @Override
  public String parse(Object value) {
    StringBuilder sb = new StringBuilder();
    sb.append("select ");

    if (value instanceof String str) {// 如果值是一个字符串
      sb.append(str).append(", ");
    } else if (value instanceof JsonArray array) {    // Add check for value being an array
      Optional.of(array).filter(arr -> arr.size() > 0).ifPresent(arr -> {
        arr.forEach(item -> {
          if (item instanceof String str) {
            log.info("project item:{}", str);
            sb.append(str).append(", ");
          }else if(item instanceof JsonObject object){
            handleJsonObject(sb, object);
          }
        });
      });
    } else if (value instanceof JsonObject object) {// 如果值是一个对象，解析对象中的属性，并返回select子句
      handleJsonObject(sb, object);
    }
    // 去掉最后多余的逗号
    sb.setLength(sb.length() - 2);
    return sb.toString();
  }

  private void handleJsonObject(StringBuilder sb, JsonObject object) {
    // 遍历对象中的每个键值对
    for (String key : object.fieldNames()) {
      Object val = object.getValue(key);
      // 如果键是一个字段名，根据值的类型进行处理
      if (val instanceof Number num) {
        // 如果值是一个数字，表示是否包含该字段，1表示包含，0表示不包含
        if (num.intValue() == 1) {
          sb.append(key).append(", ");
        }
      } else if (val instanceof JsonObject obj) {
        // 如果值是一个对象，表示有聚合操作符，如$sum, $avg, $count等，用相应的sql函数表示，并给结果起别名为键名
        handleOperations(sb, key, obj);
      } else if (val instanceof String) {
        // 如果值是一个字符串，表示有别名操作符，并给结果起别名为键名
        sb.append(OperatorUtil.toStr(val)).append(" as ").append(key).append(", ");
      }
    }
  }

  /**
   * for json object
   *
   * @param sb
   * @param key
   * @param obj
   */
  private void handleOperations(StringBuilder sb, String key, JsonObject obj) {
    for (String k : obj.fieldNames()) {
      Object v = obj.getValue(k);
      if (OperatorUtil.isAggregateOperator(k) || OperatorUtil.isArithmeticOperator(k)
        || OperatorUtil.isDateFunctionOperator(k)) {
        sb.append(operationContextService.getOperation(k).doOperation(k, v))
          .append(" as ").append(key).append(", ");
      } else if (k.equals("$switch")) {
        sb.append(strategyContextService.getStrategy("$switch").parse((JsonObject) v))
          .append(" as ").append(key).append(", ");
      } else if (OperatorUtil.isAliasOperator(k)) {
        sb.append(operationContextService.getOperation(k).doOperation(k, v)).append(key).append(", ");
      } else if (OperatorUtil.isDistinctOperator(k)) {
        sb.append(operationContextService.getOperation(k).doOperation(k, v)).append(" ").append(key).append(", ");
      }
    }
  }

  @Override
  public String type() {
    return "$project";
  }
}
