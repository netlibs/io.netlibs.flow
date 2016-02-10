package io.netlibs.flow.collector;

import java.util.List;

import io.netlibs.flow.ipfix.DataRecord;

public interface ProcessingContextListener
{

  void data(ProcessingContext ctx, List<DataRecord> data);
  
}
