package com.pcap.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.pcap.util.ConstantsConfig;
import com.pcap.webapp.PcapParameters;

public class PcapMiningService {
	String ip=ConstantsConfig.REST_IP;
	RestTemplate rt = new RestTemplate();
	
	public JSONObject startMining(PcapParameters pcap_params){
		System.out.println("Mining service loaded");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept","application/json");
	    headers.set("Cookie", "session="+pcap_params.getFlow_id());
	    HttpEntity headentity = new HttpEntity(pcap_params,headers);
	    String uri=new String("http://"+ip+":8080/pcap");
		ResponseEntity<String> resp=rt.exchange(uri, HttpMethod.POST, headentity, String.class);
		return new JSONObject(resp.getBody());
	}
	
	
	public JSONObject getMiningStatus(PcapParameters pcap_params){
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept","application/json");
	    headers.set("Cookie", "session="+pcap_params.getFlow_id());
	    HttpEntity statusEntity=new HttpEntity(headers);
		String uri_status=new String("http://"+ip+":8080/pcap/status");
		//Get the Percentage  status of completion 
		ResponseEntity<String>resp_status=rt.exchange(uri_status, HttpMethod.GET, statusEntity, String.class);
		return new JSONObject(resp_status.getBody());
	}
	
	
	public JSONObject getMiningStats(PcapParameters pcap_params){
		String uri_mining=new String("http://"+ip+":8080/pcap/miningStat");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept","application/json");
	    headers.set("Cookie", "session="+pcap_params.getFlow_id());
	    HttpEntity headentity = new HttpEntity(pcap_params,headers);
		ResponseEntity<String>resp_mining=rt.exchange(uri_mining, HttpMethod.POST, headentity, String.class);
		return new JSONObject(resp_mining.getBody());
	}
	
	/*
	 * ServerX gets t
	 */
	
	public void getPcapFile(String fileName,String filePath){
		System.out.println("File path is "+filePath);
		File file = new File(filePath);//File object is created on sever X 
		URL url;
		try {
			url = new URL("http://"+ip+":8080/pcap/file?fileName="+fileName);//REST URL 
			long start = System.currentTimeMillis();
	        System.out.println("Downloading....");
	        FileUtils.copyURLToFile(url, file);//Copy bytes received from the REST  to the File object and store the file at the file path location  
	        
	        long end = System.currentTimeMillis();
	        System.out.println("Completed....in ms : " + (end - start));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
