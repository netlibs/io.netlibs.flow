package io.netlibs.flow.ipfix;

import io.netlibs.flow.collector.ProcessingContext;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Value;

public interface TemplateFieldParser
{
  
  DataField read(ByteBuf records, ProcessingContext ctx);

}
