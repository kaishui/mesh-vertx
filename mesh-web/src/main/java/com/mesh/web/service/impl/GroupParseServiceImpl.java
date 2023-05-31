package com.mesh.web.service.impl;

import com.mesh.web.service.OperationContextService;
import com.mesh.web.service.ParseStrategyService;
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
public class GroupParseServiceImpl implements ParseStrategyService {

  @Autowired
  private OperationContextService operationContextService;

  @Override
  public String parse(Object value) {
    // 如果值是一个对象，解析对象中的属性，并返回group by子句
    if (value instanceof JsonObject object) {
      StringBuilder sb = new StringBuilder();
      // 遍历对象中的每个键值对
      for (String key : object.fieldNames()) {
        Object val = object.getValue(key);
        // 如果键是_id，表示分组字段，根据值的类型进行处理
        if (key.equals("_id")) {
          sb.append("group by ");
          if (val instanceof String) {
            // 如果值是一个字符串，直接返回字段名
            sb.append(val);
          } else if (val instanceof JsonArray array) {
            // 如果值是一个数组，用逗号连接每个元素，并返回字段名列表
            for (int i = 0; i < array.size(); i++) {
              sb.append(array.getString(i)).append(", ");
              if (i == array.size() - 1) {
                sb.delete(sb.length() - 2, sb.length());
              }
            }
          }
        } else if (key.equals("having")) {
          if (val instanceof JsonObject) {
            sb.append(" having ").append(operationContextService.parse((JsonObject) val));
          }
        }
      }
      // 去掉最后多余的逗号
      return sb.toString();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  @Override
  public String type() {
    return "$group";
  }
}
