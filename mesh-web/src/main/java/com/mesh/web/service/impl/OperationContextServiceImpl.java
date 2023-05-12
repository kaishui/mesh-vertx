package com.mesh.web.service.impl;

import com.mesh.web.service.OperationContextService;
import com.mesh.web.service.OperationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OperationContextServiceImpl implements OperationContextService {

  @Autowired
  private List<OperationService> operationServices;
  @Override
  public OperationService getOperation(String type) {
    log.info("search operation with type : {}", type);
    return operationServices.stream().filter(bean -> StringUtils.equals(type, bean.type()))
      .findFirst().orElseThrow();
  }
}
