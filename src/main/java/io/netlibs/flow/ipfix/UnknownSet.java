package io.netlibs.flow.ipfix;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UnknownSet implements SetHeader
{

  private int id;
  private byte[] data;

  public static SetHeader read(int id, ByteBuf val)
  {
    byte[] data = new byte[val.readableBytes()];
    val.readBytes(data);
    return builder().id(id).data(data).build();
  }

  @Override
  public String toString()
  {
    return String.format("UnknownSet(id=%d, len=%d)", id, data.length);
  }

}
