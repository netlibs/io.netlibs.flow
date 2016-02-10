package io.netlibs.flow.ipfix;

import java.util.List;

import com.google.common.collect.Lists;

import io.netlibs.flow.collector.ProcessingContext;
import io.netlibs.flow.ipfix.DataRecord.DataRecordBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class TemplateRecordSet implements SetHeader
{

  private static final InfoExportRegistry registry = new InfoExportRegistry();

  private final int templateId;

  @Singular
  private List<TemplateField> fields;

  private int minimumLength;

  public static TemplateRecordSet read(ByteBuf val)
  {

    TemplateRecordSetBuilder b = builder();

    b.templateId(val.readUnsignedShort());

    int fieldCount = val.readUnsignedShort();

    int min = 0;

    for (int i = 0; i < fieldCount; ++i)
    {

      int type = val.readUnsignedShort();
      int fieldLength = val.readUnsignedShort();

      min += fieldLength;

      b.field(registry.get(type, fieldLength));

    }

    b.minimumLength(min);

    return b.build();

  }

  /**
   * decode a group of records in the specified buffer, that are supposedly encoded using this template set.
   * 
   * @return
   */

  public List<DataRecord> decode(ByteBuf records, ProcessingContext ctx)
  {

    List<DataRecord> results = Lists.newArrayList();

    while (records.isReadable(getMinimumLength()))
    {

      DataRecordBuilder b = DataRecord.builder();

      b.template(this);

      for (TemplateField tf : fields)
      {
        DataField df = tf.getParser().read(records, ctx);
        b.field(new DataRecordField(tf, df));
      }

      results.add(b.build());

    }

    return results;

  }

  private int getMinimumLength()
  {
    return minimumLength;
  }

}
