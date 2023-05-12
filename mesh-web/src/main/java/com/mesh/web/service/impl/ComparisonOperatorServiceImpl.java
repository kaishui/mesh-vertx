package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationService;
import io.vertx.core.json.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ComparisonOperatorServiceImpl implements OperationService {
  @Override
  public String doOperation(String key, Object value) {
    return switch (key) {
      case "$gt" ->
        // 如果是$gt操作符，用>号表示
        " > " + value;
      case "$lt" ->
        // 如果是$lt操作符，用<号表示
        " < " + value;
      case "$eq" ->
        // 如果是$eq操作符，用=号表示
        " = " + value;
      case "$gte" ->
        // 如果是$gte操作符，用>=号表示
        " >= " + value;
      case "$lte" ->
        // 如果是$lte操作符，用<=号表示
        " <= " + value;
      case "$ne" ->
        // 如果是$ne操作符，用<>号表示
        " <> " + value;
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
