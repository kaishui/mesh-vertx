package com.mesh.web.util;

import com.mesh.web.constant.CommonConstants;
import io.vertx.core.json.JsonObject;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@UtilityClass
public class OperatorUtil {
  // 判断是否是逻辑操作符
  public boolean isLogicalOperator(String key) {
    return key.equals("$or") || key.equals("$and") || key.equals("$not");
  }


  /**
   * 判断是否是比较操作符
   */
  public boolean isComparisonOperator(String key) {
    return key.equals("$gt") || key.equals("$lt") || key.equals("$eq") || key.equals("$gte") || key.equals("$lte") || key.equals("$ne");
  }

  public boolean isInNotInOperator(String key) {
    return CommonConstants.IN_NOT_IN_OPERATOR.contains(key + ",");
  }

  // 判断是否是聚合操作符
  public boolean isAggregateOperator(String key) {
    return key.equals("$sum") || key.equals("$avg") || key.equals("$count") || key.equals("$min") || key.equals("$max");
  }

  /**
   * 四则运算
   * @param key
   * @return
   */
  public boolean isArithmeticOperator(String key) {
    return CommonConstants.ARITHMETIC_OPERATOR.contains(key + ",");
  }

  /**
   *  判断是否是日期操作符
   * @param key $FORMAT_DATE
   * @return boolean
   */
  public boolean isDateFunctionOperator(String key) {
    return CommonConstants.DATE_FUNCTION.contains(key + ",");
  }

  // 判断是否是别名操作符
  public boolean isAliasOperator(String key) {
    return key.equals("$as") || key.equals("$alias");
  }

  /**
   * sort by fixed order
   * @param json
   * @return json
   */
  public JsonObject sort(JsonObject json) {
    // don't need to sort
    if (!json.containsKey("$project")) {
      return json;
    }

    String[] order = {"$project", "$from", "$lookup", "$match", "$group", "$sort", "$limit", "$skip"};
    JsonObject sortedJson = new JsonObject();

    for (String key : order) {
      if (json.containsKey(key)) {
        sortedJson.put(key, json.getValue(key));
      }
    }
    return sortedJson;
  }

  /**
   * convert to string
   * @param value value
   * @return string
   */
  public Object toStr(Object value) {
    if (value instanceof String) {
      if(StringUtils.isNoneEmpty((String) value)) {
         value = "'" + value + "'";
      }
    }
    return value;
  }

  /**
   * 判断是否是distinct操作符
   * @param k
   * @return boolean
   */
  public boolean isDistinctOperator(String k) {
    return CommonConstants.DISTINCT.contains(k + ",");
  }

  /**
   * 判断是否是tuple操作符
   * @param key
   * @return
   */
  public static boolean isTupleOperator(String key) {
    return CommonConstants.TUPLE.contains(key + ",");
  }
}
