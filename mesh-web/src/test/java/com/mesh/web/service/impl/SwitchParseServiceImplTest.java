package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SwitchParseServiceImplTest extends BaseTest {

  @Autowired
  private SwitchParseServiceImpl switchParseService;
  @Test
  public void testSwitchParseServiceImpl_parse() {
    // given
    String jsonStr = """
      {
        "branches": [
          {
            "case": {
              "$eq": [
                "scores",
                90
              ]
            },
            "then": "100 !"
          },
          {
            "case": {
              "$gte": [
                {
                  "$avg": "scores"
                },
                90
              ]
            },
            "then": "Doing great!"
          },
          {
            "case": {
              "$and": [
                {
                  "$gte": [
                    {
                      "$avg": "scores"
                    },
                    80
                  ]
                },
                {
                  "$lt": [
                    {
                      "$avg": "scores"
                    },
                    90
                  ]
                }
              ]
            },
            "then": "Doing pretty well."
          }
        ],
        "default": "No scores found."
      }
      """;

    JsonObject object1 = new JsonObject(jsonStr);

    // when
    String caseWhenStatement1 = switchParseService.parse(object1);

    String expected = "case when scores  =  90 then '100 !' when avg(scores)  >=  90 then 'Doing great!' when ( ( avg(scores)  >=  80 )  and  ( avg(scores)  <  90 ) ) then 'Doing pretty well.' else 'No scores found.' end";
    // then
    assertEquals(expected, caseWhenStatement1);

    // given
    String jsonStr2 = """
      {
        "branches": [
          {
            "case": {
              "$eq": [
                "scores",
                100
              ]
            },
            "then": "100 !"
          },
          {
            "case": {
              "$and": [
                {
                  "$gte": [
                    "scores",
                    80
                  ]
                },
                {
                  "$lt": [
                    "scores",
                    90
                  ]
                }
              ]
            },
            "then": "Doing pretty well."
          }
        ],
        "default": "No scores found."
      }
      """;
    JsonObject object2 = new JsonObject(jsonStr2);

    // when
    String caseWhenStatement2 = switchParseService.parse(object2);

    // then
    String expected2 = "case when scores  =  100 then '100 !' when ( ( scores  >=  80 )  and  ( scores  <  90 ) ) then 'Doing pretty well.' else 'No scores found.' end";
    assertEquals(expected2, caseWhenStatement2);
  }

  @Test
  public void testSwitchParseServiceImpl_type() {
    // given
    // when
    String type = switchParseService.type();

    // then
    assertEquals("$switch", type);
  }
}
