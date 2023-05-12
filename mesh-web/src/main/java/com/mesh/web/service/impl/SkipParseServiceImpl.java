package com.mesh.web.service.impl;

import com.mesh.web.service.ParseStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * support $from element
 */
@Service
@Slf4j
public class SkipParseServiceImpl implements ParseStrategyService {

  @Override
  public String parse(Object value) {
    // 如果值是一个数字，直接返回offset子句
    if (value instanceof Number num) {
      return "offset " + num.intValue();
    }
    // 如果值是其他类型，返回空字符串
    return "";
  }

  @Override
  public String type() {
    return "$offset";
  }
}
