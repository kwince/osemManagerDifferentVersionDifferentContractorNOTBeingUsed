package org.kwince.contribs.osem.common;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.kwince.contribs.osem.exceptions.OsemException;
import org.kwince.contribs.osem.util.JSON;

public class OsemStores {
	
	final static String JSON_RESOURCE = "META-INF/osem.json";
	
	public static InputStream getStoreResource() {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(JSON_RESOURCE);
		if (is==null) {
			throw new OsemException("No Osem Store Supplied or Configured");
		}
		return is;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<HashMap<String,Object>> loadOsemStores() {
		InputStream is = getStoreResource();
		HashMap<String, Object> jsonStore = JSON.deserialize(is);
		
		if (jsonStore.get("osemStores")==null) {
			throw new OsemException("No 'osemStores' array found in JSON configuration");
		}
		
		if (!(jsonStore.get("osemStores") instanceof ArrayList<?>)) {
			throw new OsemException("No osemStores found in 'osemStores'");
		}
		
		ArrayList<HashMap<String,Object>> list = (ArrayList<HashMap<String, Object>>) jsonStore.get("osemStores");
		
		if (list.size()==0) {
			throw new OsemException("No osemStores found in 'osemStores'");
		}
		
		return list;
	}
	
	public static HashMap<String, Object> findOsemStore(String storeName) {
		
		ArrayList<HashMap<String,Object>> list = loadOsemStores();
		HashMap<String, Object> osemStore = null;
		
		for(int i=0; i<list.size(); i++) {
			osemStore = list.get(i);
			System.out.println(osemStore.get("name"));
			if(osemStore.get("name").toString().trim().equals(storeName)) {
				return osemStore;
			}
		}
		
		throw new OsemException("Specified 'osemStore' not found in 'osemStores' array, " + storeName);
	}
	
	public static HashMap<String, Object> getDefaultOsemStore() {
		
		ArrayList<HashMap<String,Object>> list = loadOsemStores();
		
		if (!(list.get(0) instanceof HashMap<?,?>)) {
			throw new OsemException("Default Constructor Cannot Choose Between Mulitple OsemStores in Configuration");
		}
		
		HashMap<String, Object> defaultStore = (HashMap<String, Object>) list.get(0);
		
		if (defaultStore.get("name")==null || defaultStore.get("name").toString().trim().equals("")) {
			throw new OsemException("Invalid 'osemStore' found in JSON configuration - bad 'name' parameter");
		}
				
		if (defaultStore.get("vendor")==null || defaultStore.get("vendor").toString().trim().equals("") ) {
			throw new OsemException("Invalid 'osemStore' found in JSON configuration - bad 'vendor' parameter");
		}
		
		if (defaultStore.get("driverClass")==null || defaultStore.get("driverClass").toString().trim().equals("") ) {
			throw new OsemException("Invalid 'osemStore' found in JSON configuration - bad 'driverClass' parameter");
		}
		
		return defaultStore;
	}
}