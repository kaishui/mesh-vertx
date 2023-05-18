package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationContextService;
import com.mesh.web.service.OperationService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ComparisonOperatorServiceImpl implements OperationService {

  @Autowired
  private OperationContextService operationContextService;

  @Override
  public String doOperation(String key, Object value) {

    if (value instanceof JsonArray array) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < array.size(); i++) {
        Object elem = array.getValue(i);
        if (elem instanceof JsonObject) {
          sb.append(operationContextService.parse((JsonObject) elem));
        } else {
          sb.append(elem);
        }
        if (i < array.size() - 1) {
          sb.append(" ").append(getMappingComaprisonKey(key)).append(" ");
        }
      }
      return sb.toString();
    } else {
      return getMappingComaprisonKey(key) + value;
    }
  }

  private String getMappingComaprisonKey(String key) {
    return switch (key) {
      case "$gt" ->
        // 如果是$gt操作符，用>号表示
        " > ";
      case "$lt" ->
        // 如果是$lt操作符，用<号表示
        " < ";
      case "$eq" ->
        // 如果是$eq操作符，用=号表示
        " = ";
      case "$gte" ->
        // 如果是$gte操作符，用>=号表示
        " >= ";
      case "$lte" ->
        // 如果是$lte操作符，用<=号表示
        " <= ";
      case "$ne" ->
        // 如果是$ne操作符，用<>号表示
        " <> ";
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };

  }


  @Override
  public String type() {
    return CommonConstants.COMPARISON;
  }
}
