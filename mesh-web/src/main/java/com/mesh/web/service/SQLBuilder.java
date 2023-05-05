package com.mesh.web.service;

import com.mesh.web.entity.SQL;

// 抽象建造者接口-SQLBuilder
public interface SQLBuilder {
  SQLBuilder select(String... columns);
  SQLBuilder from(String table);
  SQLBuilder where(String condition);
  SQLBuilder groupBy(String... columns);
  SQLBuilder orderBy(String... columns);
  // 添加join方法，支持多种连接类型
  SQLBuilder join(String type, String table, String condition);
  SQL build();
  String type();
}
