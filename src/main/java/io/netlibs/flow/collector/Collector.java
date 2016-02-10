package io.netlibs.flow.collector;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netlibs.flow.ipfix.DataRecord;
import io.netlibs.ipaddr.IPv4Address;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Collector
{

  private final DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  private final NioEventLoopGroup group = new NioEventLoopGroup();
  private CollectorListener listener;

  public Collector(CollectorListener listener)
  {
    this.listener = listener;
  }

  public void listen(int port)
  {

    Bootstrap udpBootstrap = new Bootstrap();

    udpBootstrap
        .group(group)
        .channel(NioDatagramChannel.class)
        .option(ChannelOption.SO_REUSEADDR, true)
        .handler(new ChannelInitializer<DatagramChannel>() {
          @Override
          public void initChannel(DatagramChannel ch) throws Exception
          {
            ChannelPipeline p = ch.pipeline();
            p.addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
              @Override
              public void channelRead0(ChannelHandlerContext ctx, DatagramPacket pkt)
              {
                received(IPv4Address.fromAddress(pkt.sender().getAddress()), pkt.content());
              }
            });
          }
        });

    DatagramChannel datagramChannel = (DatagramChannel) udpBootstrap.bind(new InetSocketAddress(port)).syncUninterruptibly().channel();

    log.info("Listening for IPFIX data on {}", datagramChannel.localAddress());

    channelGroup.add(datagramChannel);

  }

  public void stop()
  {
    try
    {
      channelGroup.close().sync();
      group.shutdownGracefully().sync();
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private Map<IPv4Address, ProcessingContext> ctxs = new HashMap<>();

  void received(IPv4Address addr, ByteBuf in)
  {
    ProcessingContext ctx = ctxs.computeIfAbsent(addr, faddr -> new ProcessingContext((pctx, data) -> this.data(pctx, addr, data)));
    ctx.parse(in);
  }

  private void data(ProcessingContext ctx, IPv4Address source, List<DataRecord> data)
  {
    this.listener.data(ctx, source, data);
  }

}
