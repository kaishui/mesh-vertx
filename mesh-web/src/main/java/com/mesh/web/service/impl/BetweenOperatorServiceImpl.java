package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationContextService;
import com.mesh.web.service.OperationService;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BetweenOperatorServiceImpl implements OperationService {

  @Autowired
  private OperationContextService operationContextService;


  @Override
  public String doOperation(String key, Object value) {
    log.info("key:{}, value:{}", key, value);
    StringBuilder sb = new StringBuilder(); // sb是一个StringBuilder对象
    if(value instanceof JsonObject between) {
      Object from = between.getValue("from"); // from是一个字符串，值为"2013-01-01"
      Object to = between.getValue("to"); // to是一个字符串，值为"2013-12-01"
      if(null == from || null == to) {
        return "";
      }

      String keyword = getBetweenNotBetween(key);
      sb.append(keyword);
      // 拼接from between
      if (from instanceof JsonObject) {
        sb.append(" ").append(operationContextService.parse((JsonObject) from)); // 拼接column1 between '2013-01-01'
      } else if (from instanceof String) {
        sb.append(" '").append(from).append("'"); // 拼接column1 between '2013-01-01'
      }
      sb.append(" and "); // 拼接and
      // 拼接to
      if (to instanceof JsonObject) {
        sb.append(operationContextService.parse((JsonObject) to)); // 拼接and column1 between '2013-12-01'
      }else {
        sb.append(" '").append(to).append("'");
      }

      return sb.toString();

    }


    return sb.toString(); // result是一个字符串，值为"column1 between '2013-01-01' and '2013-12-01'"

  }

  private String getBetweenNotBetween(String key) {
    return switch (key) {
      case "$notBetween" ->
        // 如果是$notBetween操作符
        " not between ";
      case "$between" ->
        // 如果是$lt操作符，用<号表示
        "  between ";
      default -> "";
    };
  }


  @Override
  public String type() {
    return CommonConstants.BETWEEN_NOT_BETWEEN;
  }
}
