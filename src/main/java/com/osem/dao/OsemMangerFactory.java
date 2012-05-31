package com.osem.dao;

import java.util.HashMap;

import org.elasticsearch.client.Client;

import com.osem.common.ElasticClient;
import com.osem.common.OsemStores;
import com.osem.exceptions.OsemException;
import com.osem.util.JSON;

public class OsemMangerFactory {
	private HashMap<String, Object> osemStore;
	Client client;
	OsemManager osem;
	ElasticClient elastic;
	
	public OsemMangerFactory() {
		osemStore = OsemStores.getDefaultOsemStore();
	}
	
	public OsemMangerFactory(String storeName) {
		osemStore = OsemStores.findOsemStore(storeName);
	}
	
	public OsemMangerFactory(String storeName, String jsonConfig) {
		HashMap<String,Object> jsonStore = JSON.deserialize(jsonConfig);
		
		osemStore = OsemStores.findOsemStore(storeName);
		
		if (osemStore==null) {
			osemStore = jsonStore;
		}
	}
	
	public OsemManager createOsemManager() {
		elastic = new ElasticClient();
		client = elastic.createClient(osemStore);
		
		if (client != null)
			return osem = new OsemManager(client);
		
		throw new OsemException("Build ElastciSearch client failed");
	}
	
	public void close() {
		elastic.close();
	}

}
