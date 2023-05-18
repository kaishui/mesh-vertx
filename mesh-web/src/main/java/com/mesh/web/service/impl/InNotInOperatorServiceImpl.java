package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationService;
import com.mesh.web.service.StrategyContextService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InNotInOperatorServiceImpl implements OperationService {

  @Autowired
  private StrategyContextService strategyContextService;

  @Override
  public String doOperation(String key, Object value) {
    log.info("key:{}, value:{}", key, value);
    if (null == value) {
      return "";
    }

    return switch (key) {
      case "$in" ->
        // 如果是$in操作符，in ['a','b','c'] ，返回用逗号分隔的字符串
        " in " + parseArray(value);
      case "$nin", "$notIn" ->
        // 如果是$nin操作符，not in ['a','b','c'] ，返回用逗号分隔的字符串
        " not in " + parseArray(value);
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
  }

  private String parseArray(Object value) {
    // 解析数组，并返回用逗号和括号包围的字符串
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    if (value instanceof JsonArray array) {
      for (int i = 0; i < array.size(); i++) {
        Object elem = array.getValue(i);
        if (elem instanceof JsonObject) {
          sb.append(strategyContextService.parse((JsonObject) elem));
        } else if (elem instanceof JsonArray) {
          sb.append(parseArray((JsonArray) elem));
        } else if (elem instanceof String) {
          sb.append("'").append(elem).append("'");
        }else {
          sb.append(elem);
        }
        if (i < array.size() - 1) {
          sb.append(", ");
        }
      }
    } else {
      sb.append(value);
    }
    sb.append(")");
    return sb.toString();

  }


  @Override
  public String type() {
    return CommonConstants.IN_NOT_IN_OPERATOR;
  }
}
