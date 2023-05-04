package com.mesh.web.service;

public abstract class BigQueryQueryBuilder {

  protected StringBuilder sqlQuery;

  public BigQueryQueryBuilder() {
    this.sqlQuery = new StringBuilder();
  }

  public void select(String columns) {
    sqlQuery.append("SELECT ").append(columns).append(" ");
  }

  public void from(String[] tables) {
    if (tables.length > 0) {
      sqlQuery.append("FROM ");
      for (int i = 0; i < tables.length; i++) {
        if (i > 0) {
          sqlQuery.append(",");
        }
        sqlQuery.append(tables[i]);
      }
      sqlQuery.append(" ");
    }
  }

  public void join(String[] tables, String[] conditions) {
    if (tables.length > 1) {
      sqlQuery.append("JOIN ");
      for (int i = 0; i < tables.length; i++) {
        if (i > 0) {
          sqlQuery.append(",");
        }
        sqlQuery.append(tables[i]).append(" ON (").append(conditions[i]).append(")");
      }
      sqlQuery.append(" ");
    }
  }

  public void leftJoin(String[] tables, String[] conditions) {
    if (tables.length > 1) {
      sqlQuery.append("LEFT JOIN ");
      for (int i = 0; i < tables.length; i++) {
        if (i > 0) {
          sqlQuery.append(",");
        }
        sqlQuery.append(tables[i]).append(" ON (").append(conditions[i]).append(")");
      }
      sqlQuery.append(" ");
    }
  }

  public void where(String condition) {
    if (!condition.isEmpty()) {
      sqlQuery.append("WHERE ").append(condition).append(" ");
    }
  }

  public abstract void groupBy(String columns);

  public abstract void sortBy(String columns);

  public abstract void distinct(boolean is_distinct);

  public String build(){
    return sqlQuery.toString();
  }
}

