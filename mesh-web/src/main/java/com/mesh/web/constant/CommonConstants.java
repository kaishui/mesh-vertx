package com.mesh.web.constant;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public class CommonConstants {

  public static final String FUNCTION = "$sum,$avg,$count,$min,$max,";
  public static final String ALIAS = "$as,$alias,";
  public static final String LOGICAL = "$and,$or,$not,";
  public static final String COMPARISON = "$gt,$lt,$eq,$gte,$lte,$ne,";
}
