package chronicle.test;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.openhft.chronicle.hash.replication.TcpTransportAndNetworkConfig;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

public class ChronicleServer {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ChronicleServer.class);

    final static String STR = 
            //"This is just a long string, which causes sink to fail for some reason.";
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    
    private final static File MAP_FILE_A = new File("/tmp/map.a");
    final static File MAP_FILE_B = new File("/tmp/map.b");
    
    public static void main(String[] args) throws Exception {
            
            MAP_FILE_A.delete();
            MAP_FILE_B.delete();
            
            ChronicleMapBuilder<String, Long> cityPostalCodesMapBuilder =
                    ChronicleMapBuilder.of(String.class, Long.class)
                        //.averageKeySize(100)
                        .averageKey(STR)
                        .replication((byte) 1, 
                                TcpTransportAndNetworkConfig.of(8076, new InetSocketAddress("localhost", 8077))
                                .heartBeatInterval(1, TimeUnit.SECONDS))
                        .entries(50_000);
            
            ChronicleMap<String, Long> cityPostalCodes = cityPostalCodesMapBuilder.createPersistedTo(MAP_FILE_A);

            for(int i = 0; i < 100; i++ ) {
                cityPostalCodes.put(STR + i, Long.valueOf(i));
            }
            
            LOGGER.info("Map created");
            Thread.sleep(10000);
            System.exit(0);
        
    }
    
}
