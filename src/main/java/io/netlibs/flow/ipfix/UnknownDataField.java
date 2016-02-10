package io.netlibs.flow.ipfix;

import lombok.Value;

@Value
public class UnknownDataField implements DataField
{  
  private byte[] data;
}
