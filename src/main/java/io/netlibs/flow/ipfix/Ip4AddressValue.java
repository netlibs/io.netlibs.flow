package io.netlibs.flow.ipfix;

import io.netlibs.ipaddr.IPv4Address;
import lombok.Getter;

public class Ip4AddressValue implements DataField
{

  @Getter
  private IPv4Address address;

  public Ip4AddressValue(IPv4Address addr)
  {
    this.address = addr;
  }

  @Override
  public String toString()
  {
    return address.toString();
  }

}
