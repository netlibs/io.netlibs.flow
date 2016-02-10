package io.netlibs.flow.ipfix;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OptionTemplateRecordSet implements SetHeader
{

  public static OptionTemplateRecordSet read(ByteBuf val)
  {
    return builder().build();
  }

}
