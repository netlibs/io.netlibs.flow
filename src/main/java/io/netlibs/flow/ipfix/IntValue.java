package io.netlibs.flow.ipfix;

public class IntValue implements DataField
{

  private long value;

  public IntValue(long value)
  {
    this.value = value;
  }

  public long value()
  {
    return this.value;
  }

  @Override
  public String toString()
  {
    return Long.toString(value());
  }

}
