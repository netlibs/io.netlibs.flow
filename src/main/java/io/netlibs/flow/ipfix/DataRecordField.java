package io.netlibs.flow.ipfix;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class DataRecordField
{

  private final TemplateField field;
  private final DataField value;

  @Override
  public String toString()
  {
    return String.format("%s = %s", field.getName(), value);
  }

}
