package com.pcap.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static HashMap ipApplianceMap=new HashMap();
	private static ArrayList es_data=new ArrayList();
	HttpSession session=null;
	static{
		
		ipApplianceMap.put("A1B456", "10.0.0.189");
	}
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model,HttpServletRequest request,HttpServletResponse response) {
		es_data=getData();
		
		model.addAttribute("es_data", es_data);
		 session=request.getSession();
		if(session.getAttribute("session")==null){
			session.putValue("session", Math.random());
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			return "home";
		}else{
			return "error";
		}
			
		
		
		
	}
	
	

	
	@RequestMapping(value = "/pcap", method = RequestMethod.GET)
	public String getPcapInfo(HttpServletRequest request, Model model,HttpServletResponse response) {
		try{
		String ip=request.getParameter("ip");
		int index=Integer.parseInt(request.getParameter("index"));
			HttpSession session=request.getSession();
		RestTemplate rt = new RestTemplate();
		String uri=new String("http://"+ip+":8080/pcap");
		ESData pcap=(ESData)es_data.get(index);
		
		PcapParameters pcap_params = new PcapParameters();
		mapESToPcap(pcap,pcap_params);
		session.putValue("back", false);
		
		model.addAttribute("ip_a",pcap_params.getIp_a());
		model.addAttribute("flow_time",pcap_params.getFlow_id());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept","application/json");
		headers.set("Cookie", "session="+session.getAttribute("session"));
		
		
		//Getting the pcap information
		HttpEntity headentity = new HttpEntity(pcap_params,headers);
		ResponseEntity<String> resp=rt.exchange(uri, HttpMethod.POST, headentity, String.class);
		JSONObject json=new JSONObject(resp.getBody());
		model.addAttribute("path",json.get("path"));
		
		
		//Getting the status 
		HttpEntity statusEntity=new HttpEntity(headers);
		String uri_status=new String("http://"+ip+":8080/pcap/status");
		ResponseEntity<String>resp_status=rt.exchange(uri_status, HttpMethod.GET, statusEntity, String.class);
		JSONObject json_status=new JSONObject(resp_status.getBody());
		System.out.println(json_status);
		
		int percent=(Integer)json_status.get("percentage_complete");
		model.addAttribute("percent",percent);
		if(percent>=100){
			model.addAttribute("percentage_complete_int","greater");
			model.addAttribute("percentage_complete","100%");
		}else{
			model.addAttribute("percentage_complete",json_status.get("percentage_complete")+"%");
			model.addAttribute("percentage_complete_int","less");
		}
		
		//model.addAttribute("percentage_complete","60%");
		model.addAttribute("status",json_status.get("status"));
		
		//Getting Mining Status
		String uri_mining=new String("http://"+ip+":8080/pcap/miningStat");
		ResponseEntity<String>resp_mining=rt.exchange(uri_mining, HttpMethod.POST, headentity, String.class);
		JSONObject json_mining=new JSONObject(resp_mining.getBody());
		JSONObject mine=json_mining.getJSONObject("mining_stats");
		model.addAttribute("pkts_matched",mine.get("pkts_matched"));
		model.addAttribute("pkts_searched",mine.get("pkts_searched"));
		model.addAttribute("pages_searched",mine.get("pages_searched"));
		model.addAttribute("directory_entries_searched",mine.get("directory_entries_searched"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		return "pcap_status";
	}
	
	
	private void  mapESToPcap(ESData es_data,PcapParameters pcap_params){
		pcap_params.setIp_a(es_data.getIp_a());
		pcap_params.setIp_b(es_data.getIp_b());
		pcap_params.setFlow_id(es_data.getFlow_id());
		pcap_params.setTime_start(es_data.getCreate_time());
		pcap_params.setTime_end(es_data.getLast_update_time());
		
	}
    

	
	
	@RequestMapping(value = "/operation", method = RequestMethod.GET)
	public String getPcapFile(HttpServletRequest request,Model model,HttpServletResponse response){
		try{
		
		String ip="10.0.0.189";
		
		
		
		File file = new File("/Users/joshia7/Documents/pcap_web/File8.pcap");
		URL url=new URL("http://"+ip+":8080/pcap/file?fileName=File8.pcap");
		
		long start = System.currentTimeMillis();
        System.out.println("Downloading....");
        FileUtils.copyURLToFile(url, file);
        long end = System.currentTimeMillis();
        System.out.println("Completed....in ms : " + (end - start));
        
        InputStream inputStream = new FileInputStream("/Users/joshia7/Documents/pcap_web/File8.pcap");
        String type=file.toURL().openConnection().guessContentTypeFromName("File8.pcap");
        
        
        byte[] reportBytes =new byte[32768];
        
		response.setHeader("Content-Disposition", "inline; filename=\"File8.pcap\"");
        response.setDateHeader("Expires", -1);
        response.setContentType("application/octet-stream");

       
        OutputStream os=response.getOutputStream();
        int read=0;
		while((read=inputStream.read(reportBytes))!=-1){
			os.write(reportBytes,0,read);
		}
        

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "operation";
	}
	
	private ArrayList getData(){
		ArrayList es_data=new ArrayList();
		try{
			Client client = new TransportClient()
			        .addTransportAddress(new InetSocketTransportAddress(ConstantsConfig.ES_IP, ConstantsConfig.ES_PORT));
					SearchResponse response = client.prepareSearch(ConstantsConfig.ES_INDEX)
					           .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					            .setFrom(0).setSize(10).setExplain(true)
					            .execute()
					           .actionGet();
					
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
	@PreDestroy
	public void cleanUp() throws Exception {
		System.out.println("clean up code ");
	 session.invalidate();
	 session.removeAttribute("session");
	}
	
}
