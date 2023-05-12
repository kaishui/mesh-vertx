package com.mesh.web.service.impl;

import com.mesh.web.service.ParseStrategyService;
import com.mesh.web.service.StrategyContextService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * support $from element
 */
@Service
@Slf4j
public class SwitchParseServiceImpl implements ParseStrategyService {

  @Autowired
  private StrategyContextService strategyContextService;

  @Override
  public String parse(Object value) {
    JsonObject object = (JsonObject) value;
    StringBuilder sb = new StringBuilder();
    sb.append("case ");
    // 遍历对象中的每个键值对
    for (String key : object.fieldNames()) {
      Object val = object.getValue(key);
      if (key.equals("branches")) {        // 如果键是branches，表示有多个分支条件，根据值的类型进行处理
        if (val instanceof JsonArray array) {
          // 遍历数组中的每个元素
          for (int i = 0; i < array.size(); i++) {
            Object elem = array.getValue(i);
            // 如果元素是一个对象，解析对象中的属性，并返回when和then子句
            if (elem instanceof JsonObject obj) {
              Object caseVal = obj.getValue("case");
              Object thenVal = obj.getValue("then");
              if (caseVal != null && thenVal != null) {
                sb.append("when ").append(strategyContextService.parse((JsonObject) caseVal)).append(" then ").append(thenVal).append(" ");
              }
            }
          }
        }
      } else if (key.equals("default")) {
        // 如果键是default，表示有默认值，根据值的类型进行处理
        sb.append("else ").append(val).append(" ");
      }
    }
    sb.append("end");
    // 返回case when语句
    return sb.toString();
  }

  @Override
  public String type() {
    return "$switch";
  }
}
