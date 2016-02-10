package io.netlibs.flow.ipfix;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;

import io.netlibs.flow.collector.ProcessingContext;
import io.netlibs.ipaddr.IPv4Address;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InfoExportRegistry
{

  private interface ParserFactory
  {

    TemplateFieldParser apply(int type, int fieldLength);

  }

  @Value
  @Builder
  private static class Entry
  {
    private final String name;
    private final ParserFactory parserFactory;
  }

  private static Entry address(String name)
  {
    return Entry.builder().name(name).parserFactory((type, len) -> (record, ctx) -> new Ip4AddressValue(IPv4Address.of(record.readUnsignedInt()))).build();
  }

  private static Entry uint8(String name)
  {
    return Entry.builder().name(name).parserFactory((type, len) -> integerField(type, len)).build();
  }

  private static Entry uint16(String name)
  {
    return Entry.builder().name(name).parserFactory((type, len) -> integerField(type, len)).build();
  }

  private static Entry uint32(String name)
  {
    return Entry.builder().name(name).parserFactory((type, len) -> integerField(type, len)).build();
  }

  private static Entry uint64(String name)
  {
    return Entry.builder().name(name).parserFactory((type, len) -> integerField(type, len)).build();
  }

  private static final Map<Integer, Entry> entries = Collections.unmodifiableMap(Stream.of(

      Maps.immutableEntry(1, uint64("octetDeltaCount")),
      Maps.immutableEntry(2, uint64("packetDeltaCount")),
      Maps.immutableEntry(3, uint64("deltaFlowCount")),
      Maps.immutableEntry(4, uint8("protocolIdentifier")),
      Maps.immutableEntry(5, uint8("ipClassOfService")),
      Maps.immutableEntry(6, uint16("tcpControlBits")),
      Maps.immutableEntry(7, uint16("sourceTransportPort")),

      Maps.immutableEntry(8, address("sourceIPv4Address")),
      Maps.immutableEntry(9, uint8("sourceIPv4PrefixLength")),
      Maps.immutableEntry(10, uint32("ingressInterface")),

      Maps.immutableEntry(11, uint16("destinationTransportPort")),
      Maps.immutableEntry(12, address("destinationIPv4Address")),
      Maps.immutableEntry(13, uint8("destinationIPv4PrefixLength")),
      Maps.immutableEntry(14, uint32("egressInterface")),
      Maps.immutableEntry(15, address("ipNextHopIPv4Address")),
      Maps.immutableEntry(16, uint32("bgpSourceAsNumber")),
      Maps.immutableEntry(17, uint32("bgpDestinationAsNumber")),

      Maps.immutableEntry(21, Entry.builder().name("flowEndSysUpTime").parserFactory((type, len) -> integerField(type, len)).build()),
      Maps.immutableEntry(22, Entry.builder().name("flowStartSysUpTime").parserFactory((type, len) -> integerField(type, len)).build()),

      Maps.immutableEntry(48, uint8("samplerId")),

      //
      Maps.immutableEntry(456, Entry.builder().name("mobileMSISDN").build())

  ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));

  @Value
  public class UnknownFieldTemplate implements TemplateFieldParser
  {

    private int fieldLength;

    @Override
    public DataField read(ByteBuf records, ProcessingContext ctx)
    {
      byte[] data = new byte[fieldLength];
      records.readBytes(data);
      return new UnknownDataField(data);
    }

  }

  public interface IntegerTemplateField extends TemplateFieldParser
  {

  }

  public TemplateField get(int type, int fieldLength)
  {

    Entry e = entries.get(type);

    if (e == null)
    {
      return new TemplateField(type, e.getName(), new UnknownFieldTemplate(fieldLength));
    }

    return new TemplateField(type, e.getName(), e.getParserFactory().apply(type, fieldLength));

  }

  private static TemplateFieldParser integerField(int type, int fieldLength)
  {
    switch (fieldLength)
    {
      case 1:
        return (record, ctx) -> new IntValue(record.readUnsignedByte());
      case 2:
        return (record, ctx) -> new IntValue(record.readUnsignedShort());
      case 4:
        return (record, ctx) -> new IntValue(record.readUnsignedInt());
      case 8:
        return (record, ctx) -> new IntValue(record.readLong());
      default:
        throw new RuntimeException(String.format("invalid length for int field: %d", fieldLength));
    }
  }


}
