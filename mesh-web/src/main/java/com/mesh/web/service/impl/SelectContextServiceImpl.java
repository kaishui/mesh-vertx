package com.mesh.web.service.impl;

import com.mesh.web.service.OperationService;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SelectContextServiceImpl {

  @Autowired
  @Lazy
  private List<OperationService> operationServices;

  @Lazy
  @Autowired
  private DefaultSelectOperationService defaultSelectOperationService;

  public String parse(JsonObject condition) {
    StringBuilder sb = new StringBuilder();
    // 遍历条件对象的键集合
    for (String key : condition.fieldNames()) {
      // 获取键对应的值，它可能是一个整数，一个字符串数组，一个json对象或者一个对象数组，表示不同的比较或者逻辑操作
      Object value = condition.getValue(key);

      sb.append(getOperation(key).doOperation(key, value));
    }
    return sb.toString();
  }

  public OperationService getOperation(String type) {
    log.info("search operation with type : {}", type);
    return operationServices.stream().filter(bean -> StringUtils.contains(bean.type(), type + ","))
      .findFirst().orElse(defaultSelectOperationService);
  }
}
