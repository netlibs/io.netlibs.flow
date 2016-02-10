package io.netlibs.flow.collector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netlibs.flow.ipfix.DataRecord;
import io.netlibs.flow.ipfix.MessageHeader;
import io.netlibs.flow.ipfix.OptionTemplateRecordSet;
import io.netlibs.flow.ipfix.TemplateRecordSet;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The defining characteristic of IPFIX is that sampling can't be parsed until we've received a template specification. This processing
 * context keeps track of the state between messages.
 * 
 * Note that the processing context requires messages are passed in order. Make sure you buffer and reorder messages before passing them in!
 * 
 * @author theo
 *
 */

@Slf4j
public class ProcessingContext
{

  private ProcessingContextListener listener;

  @Getter
  private long systemInitTimeMilliseconds = 0;

  public ProcessingContext(ProcessingContextListener listener)
  {
    this.listener = listener;
  }

  public void parse(ByteBuf in)
  {

    MessageHeader hdr = MessageHeader.read(in);

    ByteBuf content = in.readSlice(hdr.getLength());

    while (content.isReadable(4))
    {

      int type = content.readUnsignedShort();

      if (type == 0)
      {
        break;
      }

      int length = content.readUnsignedShort();

      ByteBuf records = content.readSlice(length - 4);

      switch (type)
      {

        case 2:

          // 2 -> template sets;
          template(TemplateRecordSet.read(records));
          break;

        case 3:
          // 3 -> template option sets
          options(OptionTemplateRecordSet.read(records));
          break;

        default:

          if (type < 256)
          {
            // Ignore it. Perhaps log?
            log.debug("Unsupported set type {}", type);
          }
          else
          {
            // > 256 -> data record;
            data(type, records);
          }

      }

    }

  }

  private Map<Integer, TemplateRecordSet> templates = new HashMap<>();

  /**
   * Template definition received.
   */

  private void template(TemplateRecordSet template)
  {

    TemplateRecordSet old = templates.put(template.getTemplateId(), template);

    if (old == null)
    {
      log.debug("Template defined: {}", template);
    }
    else
    {
      log.debug("Template updated: {}", template);
    }

  }

  private void options(OptionTemplateRecordSet read)
  {
  }

  private void data(int type, ByteBuf records)
  {

    TemplateRecordSet template = templates.get(type);

    if (template == null)
    {
      log.debug("Unknown template ID for context {}", type);
      return;
    }

    // we can now decode it properly.
    data(template, records);

  }

  /**
   * records for a known template.
   * 
   * @param template
   * @param records
   */

  private void data(TemplateRecordSet template, ByteBuf records)
  {
    List<DataRecord> res = template.decode(records, this);
    listener.data(this, res);
  }

}
