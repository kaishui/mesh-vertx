package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class DistinctOperatorServiceImplTest extends BaseTest {

  @Autowired
  private DistinctOperatorServiceImpl service;

  @Test
  public void testType() {
    assertEquals(CommonConstants.DISTINCT, service.type());
  }

  @Test
  public void testDistinct() {
    assertEquals("distinct", service.doOperation("$distinct", new JsonObject()));
  }

}
