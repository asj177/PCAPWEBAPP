package com.pcap.webapp;

import javax.servlet.http.Cookie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PcapParameters {
	String regex;
String ip_b;
	String port_a;
	String port_b;
	int query_id;
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	public String getIp_b() {
		return ip_b;
	}
	public void setIp_b(String ip_b) {
		this.ip_b = ip_b;
	}
	public String getPort_a() {
		return port_a;
	}
	public void setPort_a(String port_a) {
		this.port_a = port_a;
	}
	public String getPort_b() {
		return port_b;
	}
	public void setPort_b(String port_b) {
		this.port_b = port_b;
	}
	public int getQuery_id() {
		return query_id;
	}
	public void setQuery_id(int query_id) {
		this.query_id = query_id;
	}
	public Cookie[] getCookies() {
		return cookies;
	}
	public void setCookies(Cookie[] cookies) {
		this.cookies = cookies;
	}
	public String getTime_start() {
		return time_start;
	}
	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}
	public String getTime_end() {
		return time_end;
	}
	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}
	public String getFlow_id() {
		return flow_id;
	}
	public void setFlow_id(String flow_id) {
		this.flow_id = flow_id;
	}
	public String getIp_a() {
		return ip_a;
	}
	public void setIp_a(String ip_a) {
		this.ip_a = ip_a;
	}
	public String getMask_a() {
		return mask_a;
	}
	public void setMask_a(String mask_a) {
		this.mask_a = mask_a;
	}
	public String getService_id() {
		return service_id;
	}
	public void setService_id(String service_id) {
		this.service_id = service_id;
	}
	public String getExpression_id() {
		return expression_id;
	}
	public void setExpression_id(String expression_id) {
		this.expression_id = expression_id;
	}
	public String getMining_output_location() {
		return mining_output_location;
	}
	public void setMining_output_location(String mining_output_location) {
		this.mining_output_location = mining_output_location;
	}
	public String getPcap_out_file_fd() {
		return pcap_out_file_fd;
	}
	public void setPcap_out_file_fd(String pcap_out_file_fd) {
		this.pcap_out_file_fd = pcap_out_file_fd;
	}
	Cookie[]cookies;
    String time_start;
	String time_end;
	String flow_id;
	String ip_a;
	String mask_a;
	String mask_b;
	public String getMask_b() {
		return mask_b;
	}
	public void setMask_b(String mask_b) {
		this.mask_b = mask_b;
	}
	String service_id;
	String expression_id;
	String mining_output_location;
	String pcap_out_file_fd;
	
	
	
}
