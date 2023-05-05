package com.mesh.web.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class SQL {
  private String select;
  private String from;
  private String where;
  private String groupBy;
  private String orderBy;


  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(select).append(" ");
    sb.append(from).append(" ");
    if (where != null) {
      sb.append(where).append(" ");
    }
    if (groupBy != null) {
      sb.append(groupBy).append(" ");
    }
    if (orderBy != null) {
      sb.append(orderBy).append(" ");
    }
    return sb.toString().trim();
  }
}
