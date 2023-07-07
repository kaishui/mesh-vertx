package com.mesh.web.service.impl;

import com.mesh.web.service.OperationContextService;
import com.mesh.web.service.ParseStrategyService;
import com.mesh.web.util.OperatorUtil;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * support $pivot element
 */
@Service
@Slf4j
public class PivotParseServiceImpl implements ParseStrategyService {

  @Autowired
  private OperationContextService operationContextService;

  @Override
  public String parse(Object value) {
    // 如果值是一个对象，解析对象中的属性，并返回order by子句
    if (value instanceof JsonObject object) {
      StringBuilder sb = new StringBuilder();
      sb.append("PIVOT (");
      // 遍历对象中的每个键值对
      for (String key : object.fieldNames()) {
        Object val = object.getValue(key);

        if (StringUtils.equals(key, "$for")) {//support $for keyword
          sb.setLength(sb.length() - 2);
          sb.append(" FOR").append(operationContextService.parse((JsonObject) val));
        } else { // other operation
          if (OperatorUtil.isAggregateOperator(key)) {
            sb.append(operationContextService.getOperation(key).doOperation(key, value))
              .append(" as ").append(key).append(", ");
          }
        }
      }
      sb.append(") ");
      // 去掉最后多余的逗号
      return sb.toString();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  @Override
  public String type() {
    return "$pivot";
  }
}
