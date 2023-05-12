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
public class LogicalOperatorServiceImpl implements OperationService {
  @Autowired
  private StrategyContextService strategyContextService;
  @Override
  public String doOperation(String key, Object value) {
    return switch (key) {
      case "$or" ->
        // 如果是$or操作符，用or连接每个元素，并用括号包围
        parseArrayOperator("or", value);
      case "$and" ->
        // 如果是$and操作符，用and连接每个元素，并用括号包围
        parseArrayOperator("and", value);
      case "$not" ->
        // 如果是$not操作符，用not关键字表示，并用括号包围
        "not (" + strategyContextService.parse((JsonObject) value) + ")";
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
  }

  // 解析数组类型的操作符，并返回相应的sql关键字
  public String parseArrayOperator(String op, Object value) {
    // 如果值是一个数组，用指定的操作符连接每个元素，并用括号包围
    if (value instanceof JsonArray array) {
      StringBuilder sb = new StringBuilder();
      sb.append("(");
      for (int i = 0; i < array.size(); i++) {
        Object elem = array.getValue(i);
        if (elem instanceof JsonObject) {
          sb.append(parseCondition((JsonObject) elem));
        } else if (elem instanceof JsonArray) {
          sb.append(parseArrayOperator(op, elem));
        } else {
          sb.append(elem);
        }
        if (i < array.size() - 1) {
          sb.append(" ").append(op).append(" ");
        }
      }
      sb.append(")");
      return sb.toString();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }


  // 定义一个辅助方法，用于解析json对象表示的条件，并转化为sql语句中的条件
  private String parseCondition(JsonObject condition) {
    // 初始化一个字符串缓冲区，用于拼接条件语句
    StringBuilder sb = new StringBuilder();
    // 遍历条件对象的键集合
    for (String key : condition.fieldNames()) {
      // 获取键对应的值，它可能是一个整数，一个字符串数组，一个json对象或者一个对象数组，表示不同的比较或者逻辑操作
      Object value = condition.getValue(key);
      // 根据不同的键，拼接相应的条件语句
      switch (key) {
        case "$or" -> {
          // 拼接或操作，如(score > 70 and score < 90 ) or views >= 1000
          sb.append("(");
          JsonArray orArray = (JsonArray) value;
          for (int i = 0; i < orArray.size(); i++) {
            JsonObject orObj = orArray.getJsonObject(i);
            sb.append(parseCondition(orObj)).append(" or ");
          }
          sb.delete(sb.length() - 4, sb.length());
          sb.append(")");
        }
        case "$and" -> {
          // 拼接与操作，如avg(scores) >= 80 and avg(scores) < 90
          sb.append("(");
          JsonArray andArray = (JsonArray) value;
          for (int i = 0; i < andArray.size(); i++) {
            JsonObject andObj = andArray.getJsonObject(i);
            sb.append(parseCondition(andObj)).append(" and ");
          }
          sb.delete(sb.length() - 5, sb.length());
          sb.append(")");
        }
        case "$gt" -> {
          // 拼接大于操作，如score > 70
          int gtValue = (Integer) value;
          sb.append(key).append(" > ").append(gtValue);
        }
        case "$lt" -> {
          // 拼接小于操作，如score < 90
          int ltValue = (Integer) value;
          sb.append(key).append(" < ").append(ltValue);
        }
        case "$gte" -> {
          // 拼接大于等于操作，如views >= 1000
          int gteValue = (Integer) value;
          sb.append(key).append(" >= ").append(gteValue);
        }
        case "$avg" -> {
          // 拼接平均操作，如avg(scores)
          String avgColumn = (String) value;
          sb.append("avg(").append(avgColumn).append(")");
        }
        default -> {
          // 如果键不是以上的特殊操作符，表示是一个列名，如score或views
          // 获取值对应的json对象，表示要进行的比较操作，如{ $gt: 70, $lt: 90 }
          JsonObject compareObj = (JsonObject) value;
          // 遍历比较对象的键集合
          for (String compareKey : compareObj.fieldNames()) {
            // 获取键对应的值，它是一个整数，表示要比较的数值，如70或90
            int compareValue = compareObj.getInteger(compareKey);
            // 根据不同的比较键，拼接相应的条件语句
            switch (compareKey) {
              case "$gt" ->
                // 拼接大于操作，如score > 70
                sb.append(key).append(" > ").append(compareValue);
              case "$lt" ->
                // 拼接小于操作，如score < 90
                sb.append(key).append(" < ").append(compareValue);
              case "$gte" ->
                // 拼接大于等于操作，如views >= 1000
                sb.append(key).append(" >= ").append(compareValue);
              default -> {
              }
            }
            // 拼接逻辑与操作符，因为同一个列名可能有多个比较条件，如score > 70 and score < 90
            sb.append(" and ");
          }
          // 删除最后一个多余的逻辑与操作符和空格
          sb.delete(sb.length() - 5, sb.length());
        }
      }
      // 拼接逻辑与操作符，因为不同的列名可能有不同的比较条件，如(score > 70 and score < 90 ) and views >= 1000
      sb.append(" and ");
    }
    // 删除最后一个多余的逻辑与操作符和空格
    sb.delete(sb.length() - 5, sb.length());
    // 返回条件语句字符串
    return sb.toString();
  }


  @Override
  public String type() {
    return CommonConstants.LOGICAL;
  }
}
