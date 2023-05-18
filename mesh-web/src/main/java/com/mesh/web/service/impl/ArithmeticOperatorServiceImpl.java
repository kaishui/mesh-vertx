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
public class ArithmeticOperatorServiceImpl implements OperationService {

  @Autowired
  private OperationContextService operationContextService;

  @Override
  public String doOperation(String key, Object value) {

    String func = getMappingArithmetic(key);

    // 如果值是一个数组，用逗号连接每个元素，并返回函数名和字段名列表
    if (value instanceof JsonArray array) {
      StringBuilder sb = new StringBuilder("(");
      for (int i = 0; i < array.size(); i++) {
        Object target = array.getValue(i);
        //subArithmetic
        if (target instanceof JsonObject subArithmetic) {
          sb.append(operationContextService.parse(subArithmetic));
        } else {
          sb.append(target); // 递归地转换每个参数为字符串
        }

        if (i < array.size() - 1) {
          sb.append(func); // 在每个参数之间 加上函数名
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
   * @param key eg: @add
   * @return sql func eg: return +
   */
  private String getMappingArithmetic(String key) {
    return switch (key) {
      case "$add" -> " + ";
      case "$subtract", "$sub" -> " - ";
      case "$multiply", "$mul" -> " * ";
      case "$divide", "$div" -> " / ";
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
  }

  @Override
  public String type() {
    return CommonConstants.ARITHMETIC_OPERATOR;
  }
}
