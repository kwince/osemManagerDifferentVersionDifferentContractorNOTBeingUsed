package com.osem.common;

import java.util.HashMap;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class ElasticClient {
	
	Client client = null;
	Node node = null;
	
	@SuppressWarnings("unchecked")
	public Client createClient(HashMap<String, Object> osemStore) {
		
		this.close();
				
		Builder settings = ImmutableSettings.settingsBuilder();
		HashMap<String, Object> nodeConf;
		
		if (osemStore.get("clientType")!=null) {
			if (osemStore.get("clientType").equals("node")) {
				nodeConf = (HashMap<String, Object>) osemStore.get("nodeClient");
				settings.put("node.client", (Boolean) nodeConf.get("node.client"));
				settings.put("cluster.name", (String) nodeConf.get("cluster.name"));
				settings.put("node.local", (Boolean) nodeConf.get("node.local"));
			}
			else if (osemStore.get("clientType").equals("transportClient")) {
				nodeConf = (HashMap<String, Object>) osemStore.get("nodeClient");
				settings.put("host", (String) nodeConf.get("host"));
				settings.put("port", (String) nodeConf.get("port"));
				settings.put("client.transport.sniff", (String) nodeConf.get("client.transport.sniff"));
				settings.put("cluster.name", (String) nodeConf.get("cluster.name"));
			}
		}
		
//		settings.put("es.max-open-files", true);
//		settings.put("index.number_of_shards", 16);
//		settings.put("index.number_of_replicas", 1);
		
		settings.build();

		NodeBuilder nb = NodeBuilder.nodeBuilder().settings(settings);
		node = nb.node().start();
		
		client = node.client();
		
		return client;
	}
	
	public void close() {
		while (node!=null && !node.isClosed()) { 
			node.close();
			System.out.println("Waiting for the node completely closes"); 
		}
	}
}
