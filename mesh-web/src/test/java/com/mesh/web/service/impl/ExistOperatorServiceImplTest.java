package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ExistOperatorServiceImplTest extends BaseTest {

  @Autowired
  private ExistOperatorServiceImpl existOperatorService;

  @Test
  public void givenFlagIsTrue_whenDoOperationIsCalled_thenReturnIsNullString() {
    Boolean flag = true;

    String result = existOperatorService.doOperation("$exist", flag);

    assertEquals(" is null", result);
  }

  @Test
  public void givenFlagIsFalse_whenDoOperationIsCalled_thenReturnIsNotNullString() {
    Boolean flag = false;

    String result = new ExistOperatorServiceImpl().doOperation("$exist", flag);

    assertEquals(" is not null", result);
  }

  @Test
  public void givenValueIsNull_whenDoOperationIsCalled_thenReturnEmptyString() {
    Object value = null;

    String result = new ExistOperatorServiceImpl().doOperation("$exist", value);

    assertEquals("", result);
  }

}
