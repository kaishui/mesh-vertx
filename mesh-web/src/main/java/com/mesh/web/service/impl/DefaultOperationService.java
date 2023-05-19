package com.mesh.web.service.impl;

import com.mesh.web.service.OperationContextService;
import com.mesh.web.service.OperationService;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultOperationService implements OperationService {

  @Autowired
  private OperationContextService operationContextService;

  @Override
  public String doOperation(String key, Object value) {
    log.info("call default operation key: {}, value: {}", key, value);
    // 没有命中关键字的操作符
    if (value instanceof JsonObject json) {
      StringBuilder sb = new StringBuilder();
      for (String k : json.fieldNames()) {
        sb.append(" ").append(key).append(" ").append(operationContextService.getOperation(k).doOperation(k, json.getValue(k)));
        sb.append(" and ");
      }
      //delete last " and "
      sb.delete(sb.length() - 5, sb.length());
      return sb.toString();
    }

    return "";
  }

  @Override
  public String type() {
    return "$default";
  }
}
