package com.osem.dao;

import java.util.LinkedList;
import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.osem.util.JSON;

public abstract class PersistenceService<E> {
	
	protected Client client;
	protected Class<?> clazz;
	
	protected E mapping(String id, E entity) throws Exception {
		IndexResponse result =  getClient().prepareIndex(getIndexName(), getTypeName(), id)
		.setSource(JSON.serialize(entity))
		.execute().actionGet();
		if (result.getId()==null || result.getId().isEmpty()) {
			return null;
		}
		return entity;
	}
	
	protected E read(String id) throws Exception {
		GetResponse result = getClient().prepareGet(getIndexName(), getTypeName(), id).execute().actionGet();
        if (!result.isExists()) {
        	return null;
        }
		String json = JSON.serialize(result.getSource());
		@SuppressWarnings("unchecked")
		E entity = (E) JSON.deserialize(json, clazz);
		return entity;
	}
	
	protected boolean delete(String id) throws Exception {
		DeleteResponse result = getClient().prepareDelete(getIndexName(), getTypeName(), id).execute().actionGet();
		return (!result.isNotFound());
	}
	
	protected E update(String id, E entity) throws Exception {
		boolean result = delete(id);
		if (!result) {
			return null;
		}
		mapping(id, entity);
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	protected List<E> query(String query) throws Exception {
		
		WrapperQueryBuilder wrapper = new WrapperQueryBuilder(query);
	    SearchResponse searchResponse = client.prepareSearch()
	    		.setIndices(getIndexName())
	    		.setTypes(getTypeName())
	    		.setQuery(wrapper)
	    		.execute().actionGet();
	    
		SearchHit[] hits = searchResponse.hits().getHits();
		
		LinkedList<E> list = new LinkedList<E>();
		for(SearchHit hit : hits) {
			String json = JSON.serialize(hit.getSource());
			list.add((E) JSON.deserialize(json, this.clazz));
		}
		return list;
	}
	
	public boolean checkIndex() {
		boolean exists = getClient().admin().indices().prepareExists(getIndexName()).execute().actionGet().exists();
		return exists;
	}
	
	public Client getClient(){
		return this.client;
	}

	public String getIndexName() {
		return getTypeName();
	}

	public String getTypeName() {
		return clazz.getName().toLowerCase().trim().replace('.', '_');
	}
}