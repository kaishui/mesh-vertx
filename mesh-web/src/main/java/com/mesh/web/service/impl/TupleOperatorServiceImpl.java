package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationService;
import com.mesh.web.util.OperatorUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class TupleOperatorServiceImpl implements OperationService {
  @Override
  public String doOperation(String key, Object value) {
    StringBuilder sb = new StringBuilder();
    // 拼接字符串 (key1, key2, key3) in ((value1, value2, value3),...)
    if (value instanceof JsonArray arr) {
      sb.append(parseArray(arr));
    } else if (value instanceof JsonObject obj) { // 拼接字符串 (key1, key2, key3) in ((value1, value2, value3))
      sb.append(parseArray(new JsonArray().add(obj)));
    }
    return sb.toString();
  }

  private String parseArray(JsonArray arr) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < arr.size(); i++) {
      // 拼接字符串 (key1, key2, key3) in
      if (i == 0) {
        sb.append(parseKeyJson(arr.getJsonObject(i))).append(" in (");
      }
      // 拼接字符串 ((value1, value2, value3), (value4, value5, value6))
      sb.append(parseValueJson(arr.getJsonObject(i)));
      if (i < arr.size() - 1) {
        sb.append(", ");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  /**
   * 拼接字符串 (key1, key2, key3)
   *
   * @param object
   * @return
   */
  private String parseKeyJson(JsonObject object) {
    StringBuilder k = new StringBuilder();
    k.append(" (");
    Set<String> fields = object.fieldNames();
    for (int i = 0; i < fields.size(); i++) {
      k.append(fields.toArray(new String[0])[i]);
      if (i < object.fieldNames().size() - 1) {
        k.append(", ");
      }
    }
    k.append(")");
    return k.toString();
  }

  private String parseValueJson(JsonObject object) {
    StringBuilder v = new StringBuilder();
    v.append(" (");
    Set<String> fields = object.fieldNames();
    String[] keys = fields.toArray(new String[0]);
    // 获取每个字段的值
    for (int i = 0; i < fields.size(); i++) {
      v.append(OperatorUtil.toStr(object.getValue(keys[i])));
      if (i < object.fieldNames().size() - 1) {
        v.append(", ");
      }
    }
    v.append(")");
    // 拼接字符串 ((value1, value2, value3), (value4, value5, value6))
    return v.toString();
  }


  @Override
  public String type() {
    return CommonConstants.TUPLE;
  }
}
