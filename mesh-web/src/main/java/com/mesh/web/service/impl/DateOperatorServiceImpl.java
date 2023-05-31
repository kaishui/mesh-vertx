package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationService;
import com.mesh.web.util.OperatorUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DateOperatorServiceImpl implements OperationService {
  @Override
  public String doOperation(String key, Object value) {

    String func = getMappingFunctionName(key);

    StringBuilder sb = new StringBuilder();
    sb.append(func).append("(");
    // 如果值是一个字符串，直接返回函数名和字段名
    if (value instanceof String) {
      sb.append(value);
    } else if (value instanceof JsonArray array) {//如果值是一个数组，用逗号连接每个元素，并返回函数名和字段名列表
      for (int i = 0; i < array.size(); i++) {
        sb.append(OperatorUtil.toStr(array.getString(i)));
        if (i < array.size() - 1) {
          sb.append(", ");
        }
      }
    } else if (value instanceof JsonObject dataJson) {
      // json format : {"date": "column", "format": "%Y%m%d"} ，格式是bigquery format
      if (dataJson.containsKey("date") && dataJson.containsKey("format")) {
        sb.append(OperatorUtil.toStr(dataJson.getString("format"))).append(", ")
          .append(OperatorUtil.toStr(dataJson.getString("date")));
      }else {
        // todo: throws an exception
      }
    }

    sb.append(")");
    return sb.toString();
  }


  /**
   * parameter operation mapping to sql element
   *
   * @param key eg: $FORMAT_DATE
   * @return sql func eg: return FORMAT_DATE
   */
  private String getMappingFunctionName(String key) {
    return switch (key) {
      case "$PARSE_DATE", "$parseDate" ->
        // 如果是$PARSE_DATE操作符，用parseDate函数表示
        "PARSE_DATE";
      case "$FORMAT_DATE", "$formatDate" ->
        // 如果是$FORMAT_DATE操作符，用formatDate函数表示
        "FORMAT_DATE";
      case "$CURRENT_DATE", "$currentDate" ->
        // 如果是$CURRENT_DATE操作符，用currentDate函数表示
        "CURRENT_DATE";
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
  }

  @Override
  public String type() {
    return CommonConstants.DATE_FUNCTION;
  }
}
