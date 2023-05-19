package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AliasOperatorServiceImplTest extends BaseTest {

  @Autowired
  private AliasOperatorServiceImpl aliasOperatorService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  // Test doOperation()
  @Test
  public void testDoOperation() {

    // Test $as operation
    String expectedResult1 = "value as ";
    String actualResult1 = aliasOperatorService.doOperation("$as", "value");
    assertEquals(expectedResult1, actualResult1);

    // Test $alias operation
    String expectedResult2 = "value as ";
    String actualResult2 = aliasOperatorService.doOperation("$alias", "value");
    assertEquals(expectedResult2, actualResult2);

    // Test other operations
    String expectedResult3 = "";
    String actualResult3 = aliasOperatorService.doOperation("someOtherOperation", "value");
    assertEquals(expectedResult3, actualResult3);
  }

  // Test type()
  @Test
  public void testType() {

    String expectedResult = CommonConstants.ALIAS;
    String actualResult = aliasOperatorService.type();
    assertEquals(expectedResult, actualResult);
  }


  @Test
  void type() {
  }
}
