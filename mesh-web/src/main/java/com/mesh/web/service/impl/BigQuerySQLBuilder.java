package com.mesh.web.service.impl;

import com.mesh.web.entity.SQL;
import com.mesh.web.instance.CommonInstance;
import com.mesh.web.service.SQLBuilder;
import org.springframework.stereotype.Service;

// 具体建造者类-ConcreteSQLBuilder
@Service
public class BigQuerySQLBuilder implements SQLBuilder {
  private SQL sql;

  public BigQuerySQLBuilder() {
    sql = new SQL();
  }

  public SQLBuilder select(String... columns) {
    sql.setSelect("select " + String.join(",", columns));
    return this;
  }

  public SQLBuilder from(String table) {
    sql.setFrom("from " + table);
    return this;
  }

  // 修改where方法，支持嵌套子查询
  public SQLBuilder where(String condition) {
    // 如果条件中包含子查询，使用括号包裹
    if (condition.contains("select")) {
      condition = "(" + condition + ")";
    }
    sql.setWhere("where " + condition);
    return this;
  }

  public SQLBuilder groupBy(String... columns) {
    sql.setGroupBy("group by " + String.join(",", columns));
    return this;
  }

  public SQLBuilder orderBy(String... columns) {
    sql.setOrderBy("order by " + String.join(",", columns));
    return this;
  }

  // 实现join方法，支持多种连接类型
  public SQLBuilder join(String type, String table, String condition) {
    // 拼接from子句，添加连接类型、表名和连接条件
    sql.setFrom(sql.getFrom() + " " + type + " join " + table + " on " + condition);
    return this;
  }

  public SQL build() {
    return sql;
  }

  @Override
  public String type() {
    return CommonInstance.BIG_QUERY;
  }
}
