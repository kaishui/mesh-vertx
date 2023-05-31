package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationService;
import com.mesh.web.util.OperatorUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DistinctOperatorServiceImpl implements OperationService {
  @Override
  public String doOperation(String key, Object value) {
    //ignore value
    return getMappingFunctionName(key);
  }


  /**
   * parameter operation mapping to sql element
   *
   * @param key eg: $distinct
   * @return sql func eg: DISTINCT
   */
  private String getMappingFunctionName(String key) {
    return switch (key) {
      case "$distinct", "$DISTINCT" ->
        // 如果是$distinct，返回DISTINCT
        "distinct";
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
  }

  @Override
  public String type() {
    return CommonConstants.DISTINCT;
  }
}
