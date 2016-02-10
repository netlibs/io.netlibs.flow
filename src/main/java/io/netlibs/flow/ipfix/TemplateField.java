package io.netlibs.flow.ipfix;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TemplateField
{
  private int type;
  private String name;
  private TemplateFieldParser parser;
}
