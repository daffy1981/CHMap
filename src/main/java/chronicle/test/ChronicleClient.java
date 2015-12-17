package chronicle.test;

import java.net.InetSocketAddress;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.openhft.chronicle.hash.replication.TcpTransportAndNetworkConfig;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

public class ChronicleClient {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ChronicleClient.class);
    
    public static void main(String[] args) throws Exception {
    
        try {
            
            ChronicleMapBuilder<String, Long> cityPostalCodesMapBuilder =
                    ChronicleMapBuilder.of(String.class, Long.class)
                        //.averageKeySize(100)
                        .averageKey(ChronicleServer.STR)
                        .replication((byte) 2, 
                                TcpTransportAndNetworkConfig.of(8077, new InetSocketAddress("localhost", 8076))
                                .heartBeatInterval(1, TimeUnit.SECONDS))
                        .entries(50_000);
            
            ChronicleMap<String, Long> map = cityPostalCodesMapBuilder.createPersistedTo(ChronicleServer.MAP_FILE_B);

            LOGGER.info("Starting");
            Thread.sleep(3000);
            
            for( Entry<String, Long> entry : map.entrySet()) {
                LOGGER.info("{} : {}", entry.getKey(),entry.getValue());
            }
            
            System.exit(0);
            
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
