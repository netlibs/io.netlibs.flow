package io.netlibs.flow.collector;

import java.util.List;

import io.netlibs.flow.ipfix.DataRecord;
import io.netlibs.ipaddr.IPv4Address;

public interface CollectorListener
{

  void data(ProcessingContext ctx, IPv4Address source, List<DataRecord> data);

}
