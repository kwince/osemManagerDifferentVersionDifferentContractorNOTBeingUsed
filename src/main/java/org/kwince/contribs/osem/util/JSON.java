package org.kwince.contribs.osem.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import org.kwince.contribs.osem.exceptions.OsemException;

public class JSON {
	private static JsonFactory factory = new JsonFactory();
    private static ObjectMapper mapper = new ObjectMapper(factory);
    
    public static final String serialize(Object obj) {
		String res = null;
		try {
			res = mapper.writeValueAsString(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}
    
    public static final <T> T deserialize(String json, Class<T> classType) {
		T obj = null;
		
		try {
			obj = mapper.readValue(json, classType);
		} catch (JsonParseException e) {
			throw new OsemException("Invalid JSON in the configuration");
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	public static final <T> T deserialize(InputStream is, Class<T> classType) {
		T obj = null;
		
		try {
			obj = mapper.readValue(is, classType);
		} catch (JsonParseException e) {
			throw new OsemException("Invalid JSON in the configuration");
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T deserialize(String json) {
		T obj = (T) deserialize(json, HashMap.class);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T deserialize(InputStream is) {
		T obj = (T) deserialize(is, HashMap.class);
		return obj;
	}
	
	public static final void validate() {
		
	}

}
