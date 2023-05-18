package com.mesh.web.service.impl;

import com.mesh.web.service.ParseStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * support $from element
 */
@Service
@Slf4j
public class EmptyParseServiceImpl implements ParseStrategyService {

  @Override
  public String parse(Object value) {

    // 如果值是其他类型，返回空字符串
    return "";
  }

  @Override
  public String type() {
    return "default";
  }
}
