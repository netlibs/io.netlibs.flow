package io.netlibs.flow.ipfix;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class DataRecord implements Record
{
  private TemplateRecordSet template;
  @Singular  
  private List<DataRecordField> fields;
}
