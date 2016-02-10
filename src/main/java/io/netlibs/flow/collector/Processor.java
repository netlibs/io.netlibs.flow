package io.netlibs.flow.collector;

import java.io.FileInputStream;
import java.nio.file.Paths;

import io.netlibs.flow.ipfix.MessageHeader;
import io.netlibs.flow.ipfix.SetHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Processor
{

  public static void main(String[] args) throws Exception
  {

    if (args.length != 1)
    {
      System.err.println("Usage: processor <file>");
      System.exit(1);
    }

    ByteBuf in = read(new FileInputStream(Paths.get(args[0]).toFile()));

    System.err.println(in.readableBytes());

    ProcessingContext ctx = new ProcessingContext((pctx, records) -> {
      // records.forEach(record -> System.err.println(record.getFields()));
    });

    while (in.isReadable(MessageHeader.FIXED_SIZE))
    {
      ctx.parse(in);
    }

  }

  private static ByteBuf read(FileInputStream in) throws Exception
  {

    byte[] data = new byte[8192 * 1024];

    ByteBuf buf = Unpooled.buffer();

    while (true)
    {
      int len = in.read(data);
      if (len <= 0)
      {
        break;
      }
      buf.writeBytes(data, 0, len);
    }

    buf.readerIndex(0);

    return buf;

  }

}
