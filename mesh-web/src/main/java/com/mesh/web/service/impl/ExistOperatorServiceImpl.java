package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExistOperatorServiceImpl implements OperationService {


  @Override
  public String doOperation(String key, Object value) {
    log.info("key:{}, value:{}", key, value);
    if (null == value) {
      return "";
    }
    if (value instanceof Boolean flag) {
      return flag ? " is null" : " is not null";
    }
    return "";
  }


  @Override
  public String type() {
    return CommonConstants.EXISTS;
  }
}
