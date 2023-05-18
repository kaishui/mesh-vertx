package com.mesh.web.constant;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public class CommonConstants {

  public static final String FUNCTION = "$sum,$avg,$count,$min,$max,";
  public static final String ALIAS = "$as,$alias,";
  public static final String LOGICAL = "$and,$or,$not,";
  public static final String COMPARISON = "$gt,$lt,$eq,$gte,$lte,$ne,";
  public static final String ARITHMETIC_OPERATOR = "$add,$subtract,$sub,$multiply,$mul,$divide,$div,";
  public static final String IN_NOT_IN_OPERATOR = "$in,$nin,$notIn,";
}
