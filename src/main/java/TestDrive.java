import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class TestDrive {
    public static void main(String[] args) {
        startTransportClient();
    }

    private static void startTransportClient() {
        TransportClient client = null;
        try {
            Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.sniff", true).put("path.data", "C:/Users/CloudX/data").build();
            client = new TransportClient(settings);
            client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

//            System.out.println("Preparing mapping:");
//            String mappingSource = "{\"tweet\" : {"
//            + "\"properties\" : {"
//            + "    \"message\" : {\"type\" : \"string\"},"
//            + "    \"rank\" : {\"type\" : \"integer\"}"
//            + " }"
//            + " }}";
//            
//            PutMappingResponse putMappingResponse = client.admin().indices().preparePutMapping("twitter").setType("tweet").setSource(mappingSource)
//                    .execute().actionGet();
//            if (putMappingResponse.acknowledged()) {
//
//            }
//            
            Map<String, Object> source = new HashMap<String, Object>();
            source.put("message", "test message");
            source.put("rank", 123);
            source.put("rank2", null);
            IndexResponse result = client.prepareIndex("twitter", "tweet", "1").setSource(source).execute().actionGet();
            
//            IndexResponse result = client.prepareIndex("twitter", "tweet", "1").setSource(source).setOpType(OpType.CREATE).execute().actionGet();
//            System.out.println(result.getVersion());
//
            GetResponse response = client.prepareGet("twitter", "tweet", "1").execute().actionGet();
            System.out.println(response.getSourceAsString());
//            for(Object obj : response.getSource().values()) {
//                System.out.println(obj);
//            }
            source.put("rank2", "rank2");
            client.prepareIndex("twitter", "tweet", "1").setSource(source).execute().actionGet();
            response = client.prepareGet("twitter", "tweet", "1").execute().actionGet();
            System.out.println(response.getSourceAsString());
            
            // System.out.println("Search All Types");
            // SearchResponse response =
            // client.prepareSearch("twitter").execute().actionGet();
            // System.out.println(response);
//          
//            DeleteResponse response = client.prepareDelete("twitter", "tweet", "1").execute().actionGet();
//            System.out.println(response);
//
//            System.out.println("Delete all");
            client.admin().indices().prepareDeleteMapping("twitter").setType("tweet").execute().actionGet();

        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    private static void startNode() {
        Node node = null;
        try {
            node = NodeBuilder.nodeBuilder().node().start();

            Client client = node.client();
            Map<String, Object> source = new HashMap<String, Object>();
            source.put("user", "allan");
            IndexResponse result = client.prepareIndex("twitter", "tweet", "1").setSource(source).execute().actionGet();
            GetResponse response = client.prepareGet("twitter", "tweet", "1").execute().actionGet();
            System.out.println(response.getSource().get("user"));
        } finally {
            if (node != null) {
                node.close();
            }
        }
    }

}
