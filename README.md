# IPFIX library/utils

Provides a simple java IPFIX library for parsing flow exports, and a basic collector that uses netty.  Don't feel like you need to use the collector itself, it's just a lightweight bunch of helpers around the parsing API.

The library currently only implements the features needed for another project, so if it's missing features you need, pull requests are gladly accepted!

## Status

Parsing code is well tested on cisco IOS-XE IPFIX exports with medium amount of flow throughput (100k+/sec), but no other vendors yet.  The APIs are entirely subject to change in a future version.

## Usage


```java

  // create a collector.
  Collector collector = new Collector(listener);
  
  // listen on port 12345
  collector.listen(12345);
  
```




