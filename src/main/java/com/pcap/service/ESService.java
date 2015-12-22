package com.pcap.service;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.pcap.util.ConstantsConfig;
import com.pcap.webapp.ESData;

public class ESService {
	public  ArrayList getData(HttpServletRequest request){
		ArrayList es_data=new ArrayList();
		try{
            String fromTimeStamp=null;
			String toTimeStamp=null;
			int from=0;
			int to=25;
			
			if(request.getParameter("navigate")!=null){
				String navigate=(String)request.getParameter("navigate");
				
				if(navigate.equals("next")){
					from=Integer.parseInt((String)request.getParameter("to"))+1;
					to=from+25;
				}else{
					to=Integer.parseInt((String)request.getParameter("from"))-1;
					from=to=25;
				}
			}
			if(request.getParameter("fromTimestamp")!=null && !request.getParameter("fromTimestamp").isEmpty()){
				 fromTimeStamp=(String)request.getParameter("fromTimestamp");
			}
			
			if(request.getParameter("toTimestamp")!=null && !request.getParameter("toTimestamp").isEmpty()){
				toTimeStamp=(String)request.getParameter("toTimestamp");
			}
			
			
			Client client = new TransportClient()
			        .addTransportAddress(new InetSocketTransportAddress(ConstantsConfig.ES_IP, ConstantsConfig.ES_PORT));
					//Search the Index 
			SearchResponse response = client.prepareSearch(ConstantsConfig.ES_INDEX)
					           .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					            .setFrom(from).setSize(to).setExplain(true)
					            .setPostFilter(FilterBuilders.rangeFilter("@timestamp").from(fromTimeStamp).to(toTimeStamp))
					            .execute()
					           .actionGet();//Heave to mention no of records I want to fetch .size(100)
					
					if(response.getHits().getHits().length>0){
						
						for(SearchHit searchData:response.getHits().getHits()){
							
							JSONObject value=new JSONObject(searchData.getSource());
							
							Gson gson = new Gson();  
							ESData es=gson.fromJson(value.toString(),ESData.class);
							es.setTimeStamp(value.getString("@timestamp"));
							es_data.add(es);
						}
					}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return es_data;
	}

}
