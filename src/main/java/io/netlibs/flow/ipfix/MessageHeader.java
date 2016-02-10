package io.netlibs.flow.ipfix;

import java.util.List;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * <pre>
 * 
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |       Version Number          |            Length             |   
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |                           Export Time                         |
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |                       Sequence Number                         |
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *  |                    Observation Domain ID                      |
 *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * </pre>
 * 
 */

@Builder
@Value
public class MessageHeader
{

  public static final int FIXED_SIZE = 16;

  /**
   * Note that unlike the packet on the wire, the length field contains the PAYLOAD LENGTH.
   */
  
  private int length;
  private long exportTime;
  private long sequenceNumber;
  private long observationDomain;

  public static MessageHeader read(ByteBuf in)
  {

    MessageHeaderBuilder b = MessageHeader.builder();

    int version = in.readUnsignedShort();

    if (version != 10)
    {
      throw new RuntimeException("Invalid IPFIX version: " + Integer.toString(version));
    }

    b.length(in.readUnsignedShort() - FIXED_SIZE);
    b.exportTime(in.readUnsignedInt());
    b.sequenceNumber(in.readUnsignedInt());
    b.observationDomain(in.readUnsignedInt());

    return b.build();

  }

}
