package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationContextService;
import com.mesh.web.service.ParseStrategyService;
import com.mesh.web.service.StrategyContextService;
import com.mesh.web.util.OperatorUtil;
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
public class MatchParseServiceImpl implements ParseStrategyService {

  @Autowired
  private StrategyContextService strategyContextService;
  @Autowired
  private OperationContextService operationContextService;

  @Override
  public String parse(Object value) {
    // 如果值是一个对象，解析对象中的属性，并返回where子句
    if (value instanceof JsonObject object) {
      StringBuilder sb = new StringBuilder();
      sb.append("where ");
      // 遍历对象中的每个键值对
      for (String key : object.fieldNames()) {
        Object val = object.getValue(key);
        // 如果键是一个逻辑操作符，如$or, $and, $not等，用相应的sql关键字表示，并用括号包围
        if (OperatorUtil.isLogicalOperator(key)) {
          sb.append("(").append(operationContextService.getOperation(CommonConstants.LOGICAL).doOperation(key, val)).append(")");
        } else {
          // 如果键是一个字段名，根据值的类型进行处理
          if (val instanceof JsonObject obj) {
            // 如果值是一个对象，表示有比较操作符，如$gt, $lt, $eq等，用相应的sql符号表示
            for (String k : obj.fieldNames()) {
              Object v = obj.getValue(k);
              if (OperatorUtil.isComparisonOperator(k)) {
                sb.append(key).append(operationContextService.getOperation(CommonConstants.COMPARISON).doOperation(k, v));
              }
            }
          } else if (val instanceof JsonArray array) {
            // 如果值是一个数组，表示有in或者not in操作符，用相应的sql关键字表示，并用括号包围
            if (array.size() > 0) {
              Object elem = array.getValue(0);
              if (elem instanceof String && OperatorUtil.isNegationOperator((String) elem)) {
                sb.append(key).append(" not in ").append(parseArray((JsonArray) array.copy().remove(0)));
              } else {
                sb.append(key).append(" in ").append(parseArray(array));
              }
            }
          } else {
            // 如果值是其他类型，表示等于操作，用=号表示
            sb.append(key).append(" = ").append(val);
          }
        }
        // 在每个键值对之间用and连接
        sb.append(" and ");
      }
      // 去掉最后多余的and
      sb.setLength(sb.length() - 5);
      return sb.toString();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }


  // 解析数组，并返回用逗号和括号包围的字符串
  private String parseArray(JsonArray array) {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (int i = 0; i < array.size(); i++) {
      Object elem = array.getValue(i);
      if (elem instanceof JsonObject) {
        sb.append(strategyContextService.parse((JsonObject) elem));
      } else if (elem instanceof JsonArray) {
        sb.append(parseArray((JsonArray) elem));
      } else {
        sb.append(elem);
      }
      if (i < array.size() - 1) {
        sb.append(", ");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  @Override
  public String type() {
    return "$match";
  }
}
