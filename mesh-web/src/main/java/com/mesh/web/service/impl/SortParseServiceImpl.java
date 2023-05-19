package com.mesh.web.service.impl;

import com.mesh.web.service.ParseStrategyService;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * support $from element
 */
@Service
@Slf4j
public class SortParseServiceImpl implements ParseStrategyService {

  @Override
  public String parse(Object value) {
    // 如果值是一个对象，解析对象中的属性，并返回order by子句
    if (value instanceof JsonObject object) {
      StringBuilder sb = new StringBuilder();
      sb.append("order by ");
      // 遍历对象中的每个键值对
      for (String key : object.fieldNames()) {
        Object val = object.getValue(key);
        // 如果键是一个字段名，根据值的类型进行处理
        if (val instanceof Number num) {
          // 如果值是一个数字，表示排序顺序，1表示升序，-1表示降序
          if (num.intValue() == 1) {
            sb.append(key).append(" asc, ");
          } else if (num.intValue() == -1) {
            sb.append(key).append(" desc, ");
          }
        }
      }
      // 去掉最后多余的逗号
      sb.setLength(sb.length() - 2);
      return sb.toString();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  @Override
  public String type() {
    return "$sort";
  }
}
