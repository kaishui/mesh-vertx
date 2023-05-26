package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LikeOperatorServiceImplTest extends BaseTest {

  @Autowired
  private LikeOperatorServiceImpl likeOperatorServiceImpl;

  @Test
  public void doOperationTest_withLikeOperator() {
    String key = "$like";
    Object value = "testValue";
    String result = likeOperatorServiceImpl.doOperation(key, value);
    assertEquals(result, " like 'testValue'");
  }

  @Test
  public void doOperationTest_withNotLikeOperator() {
    String key = "$notLike";
    Object value = "testValue";
    String result = likeOperatorServiceImpl.doOperation(key, value);
    assertEquals(result, " not like 'testValue'");
  }

  @Test
  public void doOperationTest_withOtherOperator() {
    String key = "SomethingElse";
    Object value = "testValue";
    String result = likeOperatorServiceImpl.doOperation(key, value);
    assertEquals(result, "");
  }


}
