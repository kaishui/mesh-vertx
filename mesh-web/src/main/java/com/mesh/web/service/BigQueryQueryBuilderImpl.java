package com.mesh.web.service;

import org.springframework.stereotype.Service;

@Service
public class BigQueryQueryBuilderImpl extends BigQueryQueryBuilder {

  @Override
  public void select(String columns) {
    sqlQuery.append("SELECT " + columns);
  }



  @Override
  public void where(String condition) {
    sqlQuery.append(" WHERE " + condition);
  }

  @Override
  public void groupBy(String columns) {
    sqlQuery.append(" GROUP BY " + columns);
  }

  @Override
  public void sortBy(String columns) {
    sqlQuery.append(" ORDER BY " + columns);
  }

  @Override
  public void distinct(boolean is_distinct) {
    sqlQuery.append(" DISTINCT " + is_distinct);
  }

  @Override
  public String build() {
    return sqlQuery.toString();
  }
}
