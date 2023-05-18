package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationService;
import io.vertx.core.json.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FunctionOperatorServiceImpl implements OperationService {
  @Override
  public String doOperation(String key, Object value) {

    String func = getMappingFunctionName(key);

    // 如果值是一个字符串，直接返回函数名和字段名
    if (value instanceof String) {
      return func + "(" + value + ")";
    }
    // 如果值是一个数组，用逗号连接每个元素，并返回函数名和字段名列表
    if (value instanceof JsonArray array) {
      StringBuilder sb = new StringBuilder();
      sb.append(func).append("(");
      for (int i = 0; i < array.size(); i++) {
        sb.append(array.getString(i));
        if (i < array.size() - 1) {
          sb.append(", ");
        }
      }
      sb.append(")");
      return sb.toString();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }


  /**
   * parameter operation mapping to sql element
   *
   * @param key eg: @vag
   * @return sql func eg: return avg
   */
  private String getMappingFunctionName(String key) {
    return switch (key) {
      case "$sum" ->
        // 如果是$sum操作符，用sum函数表示，并根据值的类型进行处理
        "sum";
      case "$avg" ->
        // 如果是$avg操作符，用avg函数表示，并根据值的类型进行处理
        "avg";
      case "$count" ->
        // 如果是$count操作符，用count函数表示，并根据值的类型进行处理
        "count";
      case "$min" ->
        // 如果是$min操作符，用min函数表示，并根据值的类型进行处理
        "min";
      case "$max" ->
        // 如果是$max操作符，用max函数表示，并根据值的类型进行处理
        "max";
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
  }

  @Override
  public String type() {
    return CommonConstants.FUNCTION;
  }
}
