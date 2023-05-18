package com.mesh.web.service.impl;

import com.mesh.web.service.ParseStrategyService;
import com.mesh.web.service.StrategyContextService;
import com.mesh.web.util.OperatorUtil;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class JsonStrategyContextServiceImpl implements StrategyContextService {


  @Autowired
  @Lazy
  private List<ParseStrategyService> parseStrategyServiceList;

  @Autowired
  @Lazy
  private EmptyParseServiceImpl emptyParseService;
  @Override
  public String parse(JsonObject json) {
    // 如果json对象为空，返回空字符串
    if (json == null || json.isEmpty()) {
      //TODO: throws an exception
      return "";
    }
    // 如果json对象只有一个键值对，根据键的类型进行处理
    if (json.size() == 1) {
      String key = json.fieldNames().iterator().next();
      Object value = json.getValue(key);
      return getStrategy(key).parse(value);
    }
    // 如果json对象有多个键值对，递归解析每个键值对，并用空格连接
    StringBuilder sql = new StringBuilder();
    JsonObject sortedJson = OperatorUtil.sort(json);
    for (String key : sortedJson.fieldNames()) {
      Object value = sortedJson.getValue(key);
      sql.append(parse(new JsonObject().put(key, value))).append(" ");
    }
    // 返回最终的sql语句
    return sql.toString().trim();
  }

  @Override
  public ParseStrategyService getStrategy(String type) {
    return parseStrategyServiceList.stream().filter(bean -> StringUtils.equals(type, bean.type())).findFirst()
      .orElse(emptyParseService);
  }

}
