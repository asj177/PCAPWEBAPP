package com.pcap.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.annotation.PreDestroy;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

import com.pcap.service.ESService;
import com.pcap.service.PcapMiningService;
import com.pcap.util.ConstantsConfig;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static HashMap ipApplianceMap=new HashMap();
	private static ArrayList es_data=new ArrayList();
	HttpSession session=null;
	PcapMiningService miningService=new PcapMiningService();
	static{
		
		ipApplianceMap.put("A1B456", "10.0.0.191");
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession session)
            {
                // ip address of the service URL(like.23.28.244.244)
                if (hostname.equals(ConstantsConfig.REST_IP))
                    return true;
                return false;
            }
        });
	}
	
	
	
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model,HttpServletRequest request,HttpServletResponse response) {
		ESService es_service=new ESService();
		es_data=es_service.getData(request);
		

		 int from=0;
		 int to=25;
		if(request.getParameter("navigate")!=null){
			String navigate=(String)request.getParameter("navigate");
			
			if(navigate.equals("next")){
				from=Integer.parseInt((String)request.getParameter("to"))+1;
				to=from+25;
			}
		}
		
		model.addAttribute("es_data", es_data);
		model.addAttribute("records", es_data.size());
		model.addAttribute("from", from);
		model.addAttribute("to", to);
		logger.info("Welcome to homepage and its logger .", locale);
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			
			return "home";
	}
	
	

	/**
	 * Start the Mining Process + Get The status of mining
	 * @param request
	 * @param model		
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pcap", method = RequestMethod.GET)
	public String getPcapInfo(HttpServletRequest request, Model model,HttpServletResponse response) {
		try{
		
		
		int index=Integer.parseInt(request.getParameter("index"));
		
		//System.out.println("Index is*********** "+);
		logger.info("Index is*********** ", index);
		ESData pcap=(ESData)es_data.get(index);
		PcapParameters pcap_params = new PcapParameters();
		mapESToPcap(pcap,pcap_params);
		model.addAttribute("ip_a",pcap_params.getIp_a());
		model.addAttribute("flow_time",pcap_params.getFlow_id());
		model.addAttribute("flow_id",pcap_params.getFlow_id());
		
	  if(!checkPrevious(pcap_params.getFlow_id())){
		
		JSONObject json=miningService.startMining(pcap_params);
		model.addAttribute("path",json.get("path"));
		}
	
		
		//Getting the status 
		
		JSONObject json_status=miningService.getMiningStatus(pcap_params);
		logger.info("Getting status ***** ", json_status);
		System.out.println("Getting status *****"+json_status);
		
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
		
		JSONObject json_mining=miningService.getMiningStats(pcap_params);
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
	
	private boolean checkPrevious(String flowId){
		System.out.println("Call to rest service***************");
		logger.info("Call to rest service ***** ", flowId);
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept","application/json");
		HttpEntity statusEntity=new HttpEntity(headers);
		String uri_status=new String("http://"+ConstantsConfig.REST_IP+":8080/checkFile/"+flowId);
		ResponseEntity<Boolean>resp_status=rt.exchange(uri_status, HttpMethod.GET, statusEntity, Boolean.class);
		System.out.println("************resp *****"+resp_status.getBody());
		return resp_status.getBody();
	}
	
	/**
	 * Map the ES Data to the PCAP Parameters 
	 * @param es_data
	 * @param pcap_params
	 */
	private void  mapESToPcap(ESData es_data,PcapParameters pcap_params){
		pcap_params.setIp_a(es_data.getIp_a());
		pcap_params.setIp_b(es_data.getIp_b());
		pcap_params.setFlow_id(es_data.getFlow_id());
		pcap_params.setTime_start(es_data.getCreate_time());
		pcap_params.setTime_end(es_data.getLast_update_time());
		pcap_params.setPort_a(es_data.getL4_port_a());
		pcap_params.setPort_b(es_data.getL4_port_b());
		pcap_params.setService_id(es_data.getService_id());
		pcap_params.setMask_a("255.255.255.0");//Needs to be calculated 
		pcap_params.setMask_b("255.255.255.0");
		pcap_params.setExpression_id("123");
		
		

		
	}
    

	/**
	 * Open the Processed file 
	 * @param request
	 * @param model
	 * @param response
	 * @return
	 */
	
	@RequestMapping(value = "/operation", method = RequestMethod.GET)
	public String getPcapFile(HttpServletRequest request,Model model,HttpServletResponse response){
		try{
		
		
		String fileName=request.getParameter("flow_id")+".pcap";
		
		String fileNameClient=ConstantsConfig.CLIENT_STORE+fileName;
		
		miningService.getPcapFile(fileName,fileNameClient);
        
        InputStream inputStream = new FileInputStream(fileNameClient);
     
        System.out.println("File path in controller is  "+fileNameClient);
        
        byte[] reportBytes =new byte[32768];
        String contentDisposition="inline; filename=\""+fileName+"\"";
        System.out.println("File path in content disposition is  "+contentDisposition);
		response.setHeader("Content-Disposition", contentDisposition);
        response.setDateHeader("Expires", -1);
        response.setContentType("application/octet-stream");

       
        OutputStream os=response.getOutputStream();
        int read=0;
		while((read=inputStream.read(reportBytes))!=-1){
			os.write(reportBytes,0,read);
		}
		os.close();
		inputStream.close();

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "operation";
	}
	
	
	@PreDestroy
	public void cleanUp() throws Exception {
		System.out.println("clean up code ");
	 session.invalidate();
	 session.removeAttribute("session");
	}
	
}
