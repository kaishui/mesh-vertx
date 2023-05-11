package com.mesh.web.controller;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonToSql {

  public static void main(String[] args) {

    String jsonString = """
      {
        "$project": {
          "column1": 1,
          "column2": 1,
          "summary": 1
        },
        "$from": {
          "$project": {
            "column1": 1,
            "column2": 1,
            "sum": {
              "$sum": [
                "column3",
                "column4"
              ]
            },
            "summary": {
              "$switch": {
                "branches": [
                  {
                    "case": {
                      "$gte": [
                        {
                          "$avg": "$scores"
                        },
                        90
                      ]
                    },
                    "then": "Doing great!"
                  },
                  {
                    "case": { "$and" : [ { "$gte" : [ { "$avg" : "$scores" }, 80 ] },
                      { "$lt" : [ { "$avg" : "$scores" }, 90 ] } ] },
                    "then": "Doing pretty well."
                  }
                ],
                "default": "No scores found."
              }
            }
          },
          "$from": [
            "table1"
          ],
          "$lookup": [
            {
              "from": "table2",
              "type": "left",
              "localField": "table1.id",
              "foreignField": "table2.id"
            }
          ],
          "$match": {
            "$or": [
              {
                "score": {
                  "$gt": 70,
                  "$lt": 90
                }
              },
              {
                "views": {
                  "$gte": 1000
                }
              }
            ]
          }
        },
        "$group": {
          "_id": [
            "column1",
            "column2"
          ],
          "ct": {
            "$count": {}
          }
        },
        "$sort": {
          "column1": 1,
          "column2": -1
        },
        "$limit": 100,
        "$skip": 100
      }

      """;

    // 测试用例
    JsonObject json = new JsonObject(jsonString);
    String sql = parseJson(json);
    String expectedSql = """
      select column1, column2, count(1) as ct from (
      select column1, column2, (column3 + column4) as sum,
      case scores when avg(scores) >= 90 then "Doing great!"
      			when avg(scores) >= 80 and avg(scores) < 90  then "Doing pretty well."
      			else "No scores found."
       from table1 left join table2 on table1.id = table2.id where (score > 70 and score < 90 ) or views >= 1000 )
       group by column1, column2 order by column1 asc, column2 desc limit 100 offset 100
      """;
    log.info("generated sql: {}" ,sql);
    log.info("equals: {}", expectedSql.equals(sql));

  }

  // 解析json对象并返回sql语句
  public static String parseJson(JsonObject json) {
    // 如果json对象为空，返回空字符串
    if (json == null || json.isEmpty()) {
      return "";
    }
    // 如果json对象只有一个键值对，根据键的类型进行处理
    if (json.size() == 1) {
      String key = json.fieldNames().iterator().next();
      Object value = json.getValue(key);
      return switch (key) {
        case "$from" ->
          // 如果键是$from，返回from子句
          parseFrom(value);
        case "$lookup" ->
          // 如果键是$lookup，返回join子句
          parseLookup(value);
        case "$match" ->
          // 如果键是$match，返回where子句
          parseMatch(value);
        case "$project" ->
          // 如果键是$project，返回select子句
          parseProject(value);
        case "$group" ->
          // 如果键是$group，返回group by子句
          parseGroup(value);
        case "$sort" ->
          // 如果键是$sort，返回order by子句
          parseSort(value);
        case "$limit" ->
          // 如果键是$limit，返回limit子句
          parseLimit(value);
        case "$skip" ->
          // 如果键是$skip，返回offset子句
          parseSkip(value);
        default ->
          // 如果键是其他类型，返回空字符串
          "";
      };
    }
    // 如果json对象有多个键值对，递归解析每个键值对，并用空格连接
    StringBuilder sb = new StringBuilder();
    for (String key : json.fieldNames()) {
      Object value = json.getValue(key);
      sb.append(parseJson(new JsonObject().put(key, value))).append(" ");
    }
    // 返回最终的sql语句
    return sb.toString().trim();
  }

  // 解析$from操作符的值，并返回from子句
  public static String parseFrom(Object value) {
    // 如果值是一个字符串，直接返回表名
    if (value instanceof String) {
      return "from " + value;
    }
    // 如果值是一个数组，用逗号连接每个元素，并返回表名列表
    if (value instanceof JsonArray array) {
      StringBuilder sb = new StringBuilder();
      sb.append("from ");
      for (int i = 0; i < array.size(); i++) {
        sb.append(array.getString(i));
        if (i < array.size() - 1) {
          sb.append(", ");
        }
      }
      return sb.toString();
    }
    // 子查询
    if (value instanceof JsonObject) {
      return "from ( " +
             parseJson((JsonObject) value) + " ) ";
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  // 解析$lookup操作符的值，并返回join子句
  public static String parseLookup(Object value) {
    // 如果值是一个对象，解析对象中的属性，并返回join子句
    if (value instanceof JsonObject object) {
      String from = object.getString("from");
      String type = object.getString("type");
      String localField = object.getString("localField");
      String foreignField = object.getString("foreignField");
      if (from != null && type != null && localField != null && foreignField != null) {
        return type + " join " + from + " on " + localField + " = " + foreignField;
      }
    }
    // 如果值是一个数组，递归解析每个元素，并用空格连接
    if (value instanceof JsonArray array) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < array.size(); i++) {
        sb.append(parseLookup(array.getValue(i))).append(" ");
      }
      return sb.toString().trim();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  // 解析$match操作符的值，并返回where子句
/*
  public static String parseMatch(Object value) {
    // 如果值是一个对象，解析对象中的属性，并返回where子句
    if (value instanceof JsonObject object) {
      StringBuilder sb = new StringBuilder();
      sb.append("where ");
      // 遍历对象中的每个键值对
      for (String key : object.fieldNames()) {
        Object val = object.getValue(key);
        // 如果键是一个逻辑操作符，如$or, $and, $not等，用相应的sql关键字表示，并用括号包围
        if (isLogicalOperator(key)) {
          sb.append("(").append(parseLogicalOperator(key, val)).append(")");
        } else {
          // 如果键是一个字段名，根据值的类型进行处理
          if (val instanceof JsonObject obj) {
            // 如果值是一个对象，表示有比较操作符，如$gt, $lt, $eq等，用相应的sql符号表示
            for (String k : obj.fieldNames()) {
              Object v = obj.getValue(k);
              if (isComparisonOperator(k)) {
                sb.append(key).append(parseComparisonOperator(k, v));
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
*/

  // 解析$project操作符的值，并返回select子句
  public static String parseProject(Object value) {
    // 如果值是一个对象，解析对象中的属性，并返回select子句
    if (value instanceof JsonObject object) {
      StringBuilder sb = new StringBuilder();
      sb.append("select ");
      // 遍历对象中的每个键值对
      for (String key : object.fieldNames()) {
        Object val = object.getValue(key);
        // 如果键是一个字段名，根据值的类型进行处理
        if (val instanceof Number num) {
          // 如果值是一个数字，表示是否包含该字段，1表示包含，0表示不包含
          if (num.intValue() == 1) {
            sb.append(key).append(", ");
          }
        } else if (val instanceof JsonObject obj) {
          // 如果值是一个对象，表示有聚合操作符，如$sum, $avg, $count等，用相应的sql函数表示，并给结果起别名为键名
          for (String k : obj.fieldNames()) {
            Object v = obj.getValue(k);
            if (isAggregateOperator(k)) {
              sb.append(parseAggregateOperator(k, v)).append(" as ").append(key).append(", ");
            } else if (k.equals("$switch")) {
              sb.append(parseSwitch((JsonObject) v)).append(" as ").append(key).append(", ");
            }
          }
        } else if (val instanceof String str) {
          // 如果值是一个字符串，表示有别名操作符，如$as, $alias等，用as关键字表示，并给结果起别名为值
          if (isAliasOperator(str)) {
            sb.append(parseAliasOperator(str, key)).append(", ");
          }
        } else {
          // 如果值是其他类型，忽略该键值对
          continue;
        }
      }
      // 去掉最后多余的逗号
      sb.setLength(sb.length() - 2);
      return sb.toString();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  // 解析$switch操作符，并返回case when语句
  public static String parseSwitch(JsonObject object) {
    StringBuilder sb = new StringBuilder();
    sb.append("case ");
    // 遍历对象中的每个键值对
    for (String key : object.fieldNames()) {
      Object val = object.getValue(key);
      if (key.equals("branches")) {        // 如果键是branches，表示有多个分支条件，根据值的类型进行处理
        if (val instanceof JsonArray array) {
          // 遍历数组中的每个元素
          for (int i = 0; i < array.size(); i++) {
            Object elem = array.getValue(i);
            // 如果元素是一个对象，解析对象中的属性，并返回when和then子句
            if (elem instanceof JsonObject obj) {
              Object caseVal = obj.getValue("case");
              Object thenVal = obj.getValue("then");
              if (caseVal != null && thenVal != null) {
                sb.append("when ").append(parseJson((JsonObject) caseVal)).append(" then ").append(thenVal).append(" ");
              }
            }
          }
        }
      } else if (key.equals("default")) {
        // 如果键是default，表示有默认值，根据值的类型进行处理
        sb.append("else ").append(val).append(" ");
      } else {
        // 如果键是其他类型，忽略该键值对
        continue;
      }
    }
    sb.append("end");
    // 返回case when语句
    return sb.toString();
  }


  // 解析$group操作符的值，并返回group by子句
  public static String parseGroup(Object value) {
    // 如果值是一个对象，解析对象中的属性，并返回group by子句
    if (value instanceof JsonObject object) {
      StringBuilder sb = new StringBuilder();
      sb.append("group by ");
      // 遍历对象中的每个键值对
      for (String key : object.fieldNames()) {
        Object val = object.getValue(key);
        // 如果键是_id，表示分组字段，根据值的类型进行处理
        if (key.equals("_id")) {
          if (val instanceof String) {
            // 如果值是一个字符串，直接返回字段名
            sb.append(val).append(", ");
          } else if (val instanceof JsonArray array) {
            // 如果值是一个数组，用逗号连接每个元素，并返回字段名列表
            for (int i = 0; i < array.size(); i++) {
              sb.append(array.getString(i)).append(", ");
            }
          }  // 如果值是其他类型，忽略该键值对

        }
      }
      // 去掉最后多余的逗号
      sb.setLength(sb.length() - 2);
      return sb.toString();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  // 解析$sort操作符的值，并返回order by子句
  public static String parseSort(Object value) {
    // 如果值是一个对象，解析对象中的属性，并返回order by子句
    if (value instanceof JsonObject object) {
      StringBuilder sb = new StringBuilder();
      sb.append("order by ");
      // 遍历对象中的每个键值对
      for (String key : object.fieldNames()) {
        Object val = object.getValue(key);
        // 如果键是一个字段名，根据值的类型进行处理
        if (val instanceof Number num) {
          // 如果值是一个数字，表示排序顺序，1表示升序，-1表示降序
          if (num.intValue() == 1) {
            sb.append(key).append(" asc, ");
          } else if (num.intValue() == -1) {
            sb.append(key).append(" desc, ");
          } else {
            // 如果值是其他数字，忽略该键值对
            continue;
          }
        } else {
          // 如果值是其他类型，忽略该键值对
          continue;
        }
      }
      // 去掉最后多余的逗号
      sb.setLength(sb.length() - 2);
      return sb.toString();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  // 解析$limit操作符的值，并返回limit子句
  public static String parseLimit(Object value) {
    // 如果值是一个数字，直接返回limit子句
    if (value instanceof Number num) {
      return "limit " + num.intValue();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  // 解析$skip操作符的值，并返回offset子句
  public static String parseSkip(Object value) {
    // 如果值是一个数字，直接返回offset子句
    if (value instanceof Number num) {
      return "offset " + num.intValue();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  // 判断是否是逻辑操作符
  public static boolean isLogicalOperator(String key) {
    return key.equals("$or") || key.equals("$and") || key.equals("$not");
  }

  // 解析逻辑操作符，并返回相应的sql关键字
  public static String parseLogicalOperator(String key, Object value) {
    return switch (key) {
      case "$or" ->
        // 如果是$or操作符，用or连接每个元素，并用括号包围
        parseArrayOperator("or", value);
      case "$and" ->
        // 如果是$and操作符，用and连接每个元素，并用括号包围
        parseArrayOperator("and", value);
      case "$not" ->
        // 如果是$not操作符，用not关键字表示，并用括号包围
        "not (" + parseJson((JsonObject) value) + ")";
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
  }

  // 解析数组类型的操作符，并返回相应的sql关键字
//  public static String parseArrayOperator(String op, Object value) {
//    // 如果值是一个数组，用指定的操作符连接每个元素，并用括号包围
//    if (value instanceof JsonArray) {
//      JsonArray array = (JsonArray) value;
//      StringBuilder sb = new StringBuilder();
//      sb.append("(");
//      for (int i = 0; i < array.size(); i++) {
//        sb.append(parseJson(array.getJsonObject(i)));
//        if (i < array.size() - 1) {
//          sb.append(" ").append(op).append(" ");
//        }
//      }
//      sb.append(")");
//      return sb.toString();
//    }
//    // 如果值是其他类型，返回空字符串
//    return "";
//  }

  // 判断是否是比较操作符
  public static boolean isComparisonOperator(String key) {
    return key.equals("$gt") || key.equals("$lt") || key.equals("$eq") || key.equals("$gte") || key.equals("$lte") || key.equals("$ne");
  }

  // 解析比较操作符，并返回相应的sql符号
  public static String parseComparisonOperator(String key, Object value) {
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

  // 判断是否是聚合操作符
  public static boolean isAggregateOperator(String key) {
    return key.equals("$sum") || key.equals("$avg") || key.equals("$count") || key.equals("$min") || key.equals("$max");
  }

  // 解析聚合操作符，并返回相应的sql函数
  public static String parseAggregateOperator(String key, Object value) {
    return switch (key) {
      case "$sum" ->
        // 如果是$sum操作符，用sum函数表示，并根据值的类型进行处理
        parseFunctionOperator("sum", value);
      case "$avg" ->
        // 如果是$avg操作符，用avg函数表示，并根据值的类型进行处理
        parseFunctionOperator("avg", value);
      case "$count" ->
        // 如果是$count操作符，用count函数表示，并根据值的类型进行处理
        parseFunctionOperator("count", value);
      case "$min" ->
        // 如果是$min操作符，用min函数表示，并根据值的类型进行处理
        parseFunctionOperator("min", value);
      case "$max" ->
        // 如果是$max操作符，用max函数表示，并根据值的类型进行处理
        parseFunctionOperator("max", value);
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
  }

  // 解析函数类型的操作符，并返回相应的sql函数
  public static String parseFunctionOperator(String func, Object value) {
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

  // 判断是否是别名操作符
  public static boolean isAliasOperator(String key) {
    return key.equals("$as") || key.equals("$alias");
  }

  // 解析别名操作符，并返回相应的sql关键字
  public static String parseAliasOperator(String key, Object value) {
    return switch (key) {
      case "$as" ->
        // 如果是$as操作符，用as关键字表示，并返回字段名和别名
        value + " as " + key;
      case "$alias" ->
        // 如果是$alias操作符，用as关键字表示，并返回字段名和别名
        value + " as " + key;
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
  }

  // 解析$match操作符的值，并返回where子句
  public static String parseMatch(Object value) {
    // 如果值是一个对象，解析对象中的属性，并返回where子句
    if (value instanceof JsonObject object) {
      StringBuilder sb = new StringBuilder();
      sb.append("where ");
      // 遍历对象中的每个键值对
      for (String key : object.fieldNames()) {
        Object val = object.getValue(key);
        // 如果键是一个逻辑操作符，如$or, $and, $not等，用相应的sql关键字表示，并用括号包围
        if (isLogicalOperator(key)) {
          sb.append("(").append(parseLogicalOperator(key, val)).append(")");
        } else {
          // 如果键是一个字段名，根据值的类型进行处理
          if (val instanceof JsonObject obj) {
            // 如果值是一个对象，表示有比较操作符，如$gt, $lt, $eq等，用相应的sql符号表示
            for (String k : obj.fieldNames()) {
              Object v = obj.getValue(k);
              if (isComparisonOperator(k)) {
                sb.append(key).append(parseComparisonOperator(k, v));
              }
            }
          } else if (val instanceof JsonArray array) {
            // 如果值是一个数组，表示有in或者not in操作符，用相应的sql关键字表示，并用括号包围
            if (array.size() > 0) {
              Object elem = array.getValue(0);
              if (elem instanceof String && isNegationOperator((String) elem)) {
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

  // 解析数组类型的操作符，并返回相应的sql关键字
  public static String parseArrayOperator(String op, Object value) {
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
  public static String parseCondition(JsonObject condition) {
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

  // 解析数组，并返回用逗号和括号包围的字符串
  public static String parseArray(JsonArray array) {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (int i = 0; i < array.size(); i++) {
      Object elem = array.getValue(i);
      if (elem instanceof JsonObject) {
        sb.append(parseJson((JsonObject) elem));
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

  // 判断是否是否定操作符
  public static boolean isNegationOperator(String key) {
    return key.equals("$nin") || key.equals("$notIn");
  }
}

