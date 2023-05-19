package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AliasOperatorServiceImpl implements OperationService {
  @Override
  public String doOperation(String key, Object value) {
    return switch (key) {
      case "$as" ->
        // 如果是$as操作符，用as关键字表示，并返回字段名和别名
        value + " as ";
      case "$alias" ->
        // 如果是$alias操作符，用as关键字表示，并返回字段名和别名
        value + " as ";
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
    }



  @Override
  public String type() {
    return CommonConstants.ALIAS;
  }
}
