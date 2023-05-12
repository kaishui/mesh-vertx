package com.mesh.web.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

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

  public boolean isNegationOperator(String key) {
    return key.equals("$nin") || key.equals("$notIn");
  }

  // 判断是否是聚合操作符
  public boolean isAggregateOperator(String key) {
    return key.equals("$sum") || key.equals("$avg") || key.equals("$count") || key.equals("$min") || key.equals("$max");
  }


  // 判断是否是别名操作符
  public boolean isAliasOperator(String key) {
    return key.equals("$as") || key.equals("$alias");
  }
}
