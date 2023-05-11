package com.mesh.web.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JsonToSqlVertx {

  public static void main(String[] args) {
    // 创建一个vertx实例
    Vertx vertx = Vertx.vertx();
    // 创建一个sql客户端
//    SQLClient client = ...; // 根据你的数据库类型和配置创建相应的客户端，如MySQLClient.createShared(vertx, config)
    // 创建一个json对象
    JsonObject json = new JsonObject();
    // 添加$project字段
    JsonObject project = new JsonObject();
    project.put("column1", 1);
    project.put("column2", 1);
    // 添加sum字段
    JsonObject sum = new JsonObject();
    sum.put("$sum", new String[]{"column3", "column4"});
    project.put("sum", sum);
    // 添加summary字段
    JsonObject summary = new JsonObject();
    JsonObject switchObj = new JsonObject();
    switchObj.put("branches", new JsonObject[] {
      new JsonObject().put("case", new JsonObject().put("$gte", new Object[] {new JsonObject().put("$avg", "$scores"), 90}))
        .put("then", "Doing great!"),
      new JsonObject().put("case", new JsonObject().put("$and", new Object[] {
        new JsonObject().put("$gte", new Object[] {new JsonObject().put("$avg", "$scores"), 80}),
        new JsonObject().put("$lt", new Object[] {new JsonObject().put("$avg", "$scores"), 90})
      }))
        .put("then", "Doing pretty well.")
    });
    switchObj.put("default", "No scores found.");
    summary.put("$switch", switchObj);
    project.put("summary", summary);
    json.put("$project", project);
    // 添加$from字段
    json.put("$from", new String[]{"table1"});
    // 添加$lookup字段
    json.put("$lookup", new JsonObject[] {
      new JsonObject().put("from", "table2")
        .put("type", "left")
        .put("localField", "table1.id")
        .put("foreignField", "table2.id")
    });
    // 添加$match字段
    json.put("$match", new JsonObject().put("$or", new Object[] {
      new JsonObject().put("score", new JsonObject().put("$gt", 70).put("$lt", 90)),
      new JsonObject().put("views", new JsonObject().put("$gte", 1000))
    }));

    // 调用方法转化为sql语句
    // 调用方法转化为sql语句
    String sql = jsonToSql(json);
    // 打印结果
    System.out.println(sql);

    // 使用sql客户端获取数据库连接
  }

  // 定义一个方法，将json对象转化为sql语句
  public static String jsonToSql(JsonObject json) {
    // 初始化一个字符串缓冲区，用于拼接sql语句
    StringBuilder sb = new StringBuilder();
    // 拼接select关键字
    sb.append("select ");
    // 获取$project字段的值，它是一个json对象
    JsonObject project = json.getJsonObject("$project");
    // 遍历project的键集合
    for (String key : project.fieldNames()) {
      // 获取键对应的值，它可能是一个整数或者一个json对象
      Object value = project.getValue(key);
      // 如果值是一个整数，表示要选择该列
      if (value instanceof Integer) {
        // 拼接列名和逗号
        sb.append(key).append(", ");
      }
      // 如果值是一个json对象，表示要进行一些计算或者聚合操作
      if (value instanceof JsonObject) {
        // 获取json对象的键，它表示要进行的操作，如$sum或$switch
        String operation = ((JsonObject) value).fieldNames().iterator().next();
        // 获取json对象的值，它可能是一个字符串数组或者一个json对象，表示要操作的列名或者分支条件，如["column3", "column4"]或{branches: [...], default: ...}
        Object subValue = ((JsonObject) value).getValue(operation);
        // 根据不同的操作，拼接相应的sql语句
        switch (operation) {
          case "$sum":
            // 拼接求和操作和别名，如(column3 + column4) as sum
            sb.append("(").append(((JsonArray) subValue).getString(0)).append(" + ").append(((JsonArray) subValue).getString(1)).append(")").append(" as ").append(key).append(", ");
            break;
          case "$switch":
            // 拼接case when操作和别名，如case scores when avg(scores) >= 90 then "Doing great!" ... else "No scores found." as summary
            sb.append("case ").append(key).append(" ");
            JsonArray branches = ((JsonObject) subValue).getJsonArray("branches");
            for (int i = 0; i < branches.size(); i++) {
              JsonObject branch = branches.getJsonObject(i);
              JsonObject caseObj = branch.getJsonObject("case");
              String thenStr = branch.getString("then");
              sb.append("when ").append(parseCondition(caseObj)).append(" then ").append("\"").append(thenStr).append("\"").append(" ");
            }
            String defaultStr = ((JsonObject) subValue).getString("default");
            sb.append("else ").append("\"").append(defaultStr).append("\"").append(" as ").append(key).append(", ");
            break;
          // 可以添加其他操作的逻辑，如$avg, $min, $max等
          default:
            break;
        }
      }
    }
    // 删除最后一个多余的逗号和空格
    sb.delete(sb.length() - 2, sb.length());
    // 拼接from关键字
    sb.append(" from ");
    // 获取$from字段
    // 获取$from字段的值，它是一个字符串数组，表示要查询的表名，如["table1"]
    JsonArray from = json.getJsonArray("$from");
    // 遍历表名数组，拼接表名和逗号
    for (int i = 0; i < from.size(); i++) {
      String table = from.getString(i);
      sb.append(table).append(", ");
    }
    // 删除最后一个多余的逗号和空格
    sb.delete(sb.length() - 2, sb.length());
    // 获取$lookup字段的值，它是一个json对象数组，表示要进行的连接操作，如[{"from": "table2", ...}]
    JsonArray lookup = json.getJsonArray("$lookup");
    // 遍历连接操作数组，拼接连接类型，表名和条件
    for (int i = 0; i < lookup.size(); i++) {
      JsonObject join = lookup.getJsonObject(i);
      String joinType = join.getString("type"); // 连接类型，如left
      String joinTable = join.getString("from"); // 连接表名，如table2
      String localField = join.getString("localField"); // 本地字段，如table1.id
      String foreignField = join.getString("foreignField"); // 外键字段，如table2.id
      // 拼接连接语句，如left join table2 on table1.id = table2.id
      sb.append(" ").append(joinType).append(" join ").append(joinTable).append(" on ").append(localField).append(" = ").append(foreignField);
    }
    // 获取$match字段的值，它是一个json对象，表示要进行的筛选条件，如{ $or: [ { score: { $gt: 70, $lt: 90 } }, { views: { $gte: 1000 } } ] }
    JsonObject match = json.getJsonObject("$match");
    // 拼接where关键字
    sb.append(" where ");
    // 解析筛选条件，并拼接到sql语句中
    sb.append(parseCondition(match));

    // 返回sql语句字符串
    return sb.toString();
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
        case "$or":
          // 拼接或操作，如(score > 70 and score < 90 ) or views >= 1000
          sb.append("(");
          JsonArray orArray = (JsonArray) value;
          for (int i = 0; i < orArray.size(); i++) {
            JsonObject orObj = orArray.getJsonObject(i);
            sb.append(parseCondition(orObj)).append(" or ");
          }
          sb.delete(sb.length() - 4, sb.length());
          sb.append(")");
          break;
        case "$and":
          // 拼接与操作，如avg(scores) >= 80 and avg(scores) < 90
          sb.append("(");
          JsonArray andArray = (JsonArray) value;
          for (int i = 0; i < andArray.size(); i++) {
            JsonObject andObj = andArray.getJsonObject(i);
            sb.append(parseCondition(andObj)).append(" and ");
          }
          sb.delete(sb.length() - 5, sb.length());
          sb.append(")");
          break;
        case "$gt":
          // 拼接大于操作，如score > 70
          int gtValue = (Integer) value;
          sb.append(key).append(" > ").append(gtValue);
          break;
        case "$lt":
          // 拼接小于操作，如score < 90
          int ltValue = (Integer) value;
          sb.append(key).append(" < ").append(ltValue);
          break;
        case "$gte":
          // 拼接大于等于操作，如views >= 1000
          int gteValue = (Integer) value;
          sb.append(key).append(" >= ").append(gteValue);
          break;
        case "$avg":
          // 拼接平均操作，如avg(scores)
          String avgColumn = (String) value;
          sb.append("avg(").append(avgColumn).append(")");
          break;
        default:
          // 如果键不是以上的特殊操作符，表示是一个列名，如score或views
          // 获取值对应的json对象，表示要进行的比较操作，如{ $gt: 70, $lt: 90 }
          JsonObject compareObj = (JsonObject) value;
          // 遍历比较对象的键集合
          for (String compareKey : compareObj.fieldNames()) {
            // 获取键对应的值，它是一个整数，表示要比较的数值，如70或90
            int compareValue = compareObj.getInteger(compareKey);
            // 根据不同的比较键，拼接相应的条件语句
            switch (compareKey) {
              case "$gt":
                // 拼接大于操作，如score > 70
                sb.append(key).append(" > ").append(compareValue);
                break;
              case "$lt":
                // 拼接小于操作，如score < 90
                sb.append(key).append(" < ").append(compareValue);
                break;
              case "$gte":
                // 拼接大于等于操作，如views >= 1000
                sb.append(key).append(" >= ").append(compareValue);
                break;
              default:
                break;
            }
            // 拼接逻辑与操作符，因为同一个列名可能有多个比较条件，如score > 70 and score < 90
            sb.append(" and ");
          }
          // 删除最后一个多余的逻辑与操作符和空格
          sb.delete(sb.length() - 5, sb.length());
          break;
      }
      // 拼接逻辑与操作符，因为不同的列名可能有不同的比较条件，如(score > 70 and score < 90 ) and views >= 1000
      sb.append(" and ");
    }
    // 删除最后一个多余的逻辑与操作符和空格
    sb.delete(sb.length() - 5, sb.length());
    // 返回条件语句字符串
    return sb.toString();
  }
}

