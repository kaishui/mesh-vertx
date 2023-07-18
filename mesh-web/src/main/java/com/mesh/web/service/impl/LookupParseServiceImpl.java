package com.mesh.web.service.impl;

import com.mesh.web.service.ParseStrategyService;
import com.mesh.web.service.StrategyContextService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * support $lookup element
 */
@Service
@Slf4j
public class LookupParseServiceImpl implements ParseStrategyService {

  @Autowired
  private StrategyContextService strategyContextService;


  @Override
  public String parse(Object value) {
    log.info("lookup input parameter: {}", value);
    // 如果值是一个字符串，直接返回表名
    StringBuilder sb = new StringBuilder();
    // 如果值是一个数组，用逗号连接每个元素，并返回表名列表
    if (value instanceof JsonArray array) {
      for (int i = 0; i < array.size(); i++) {
        sb.append(parseJson(array.getValue(i)));
      }
    }else{
      sb.append(parseJson(value));
    }

    log.info("lookup parse result: {}", sb);
    return sb.toString();
  }

  private String parseJson(Object value) {
    StringBuilder sb = new StringBuilder();
    log.info("handle object value: {}", value);
    if (value instanceof JsonObject json) {
      log.info("sub query: {}", value);
      Object from = json.getValue("from");
      String type = json.getString("type");
      Object foreign = json.getValue("foreign");
      // 确定join type
      sb.append(" ").append(convertTypeToJion(type)).append(" ");
      // 针对不同情况的 from : table / from: {jsonObject}
      if (from instanceof String) {
        sb.append(from).append(" ");
      } else if (from instanceof JsonObject fromJson) {
        //use $from to handle
        String fromPart = strategyContextService.getStrategy("$from").parse(fromJson);
        //remove "from" prefix
        fromPart = fromPart.substring(5);
        sb.append(fromPart);
      }
      sb.append("on (");
      // foreign : {"joinTable.column": "primary.column"}
      if (foreign instanceof JsonObject foreignJson) {
        for (String key : foreignJson.fieldNames()) {
          String column = foreignJson.getString(key);
          sb.append(key).append("=").append(column).append(", ");
        }
        sb.setLength(sb.length() - 2);
      }else if( foreign instanceof JsonArray fArr){
        // special handle foreign: [{l.c1 = t.c1}, {t.c2 = l.c2}]
        for(Object item : fArr){
          if(item instanceof JsonObject elem){
            for (String key : elem.fieldNames()) {
              String column = elem.getString(key);
              sb.append(key).append("=").append(column).append(", ");
            }
          } else {
            // todo: throws an exception
          }
        }
        sb.setLength(sb.length() -2);
      }
      sb.append(") ");
    }
    return sb.toString();
  }

  private String convertTypeToJion(String type) {
    type = Optional.ofNullable(type).filter(StringUtils::isNotBlank).map(String::toLowerCase).orElse("");
    return switch (type) {
      case "left" -> "left join";
      case "right" -> "right join";
      case "inner" -> "inner join";
      case "full" -> "full join";
      default ->
        //TODO: Throw an exception
        "";
    };
  }

  @Override
  public String type() {
    return "$lookup";
  }
}
