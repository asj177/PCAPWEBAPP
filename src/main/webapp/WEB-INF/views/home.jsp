<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<title>PCAP</title>
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/pcap.js"></script>


<script type="text/javascript">

function callOnClick(index,ip){
	$("#index").val(index);
	$("#ip").val(ip);
	var form=document.getElementById('pcap_form');
	form.setAttribute("target", "_blank");
	form.submit();
    return false;
	
	/* $.ajax({
		type:"POST",
		url:"/webapp/pcap?index="+index+"&ip="+ip,
		success:function(data){
			
			$('#main').html(data);
		}
		
		
	}) */
	
	
}


function callNext(){
	
	$("#navigate").attr("value","next")
	$.ajax({
		url:'/pcap',
		type:'GET',
		success:function(data){
			$("#main").html(data)
		}
		
		
		
	}) 
}


function callPrev(){
	
	$("#navigate").attr("value","prev")
	$.ajax({
		url:'/pcap',
		type:'GET',
		success:function(data){
			$("#main").html(data)
		}
		
		
		
	}) 
	
}
function applyFilter(){
	//location.reload();
	var toTimestamp=$("#toTimestamp").val();
	var fromTimestamp=$("#fromTimestamp").val();
	$.ajax({
	url:'/pcap',
	type:'GET',
	data:{
		'fromTimestamp':fromTimestamp,
		'toTimestamp':toTimestamp
	},
	success:function(data){
		$("#main").html(data)
	}
	
	
	
}) 
}




</script>

</head>

<body>
	<div id="main">


	
		<table border="1">
<form name="pcap_form" id="pcap_form" method="get" action="pcap">
<input type="hidden" value="next" id="navigate" name="navigate"/>
<input type="hidden" value="${to}" id="to"/>
 <input type="hidden" value="${from}" id="from"/>
<b>Timestamp filters</b>
<br/>
From &nbsp; <input type="text" id="fromTimestamp" name="fromTimestamp"></input>  &nbsp;  &nbsp; To &nbsp;<input type="text" id="toTimestamp" name="toTimestamp"></input>
<br/>
<input type="button" value="search" id="search" name="search" onclick="applyFilter()"></input>
				<b>Total Number of records fetched :${records}</b>
				<c:forEach var="esData" items="${es_data}" varStatus="indexClicked">


			<tr>
				<td><b>Timestamp</b>:${esData.timeStamp}</br>
				<b>Appliance</b>:${esData.appliance}</br>
				<b>Flow_id</b>:${esData.flow_id}</br>
				<b>Create_time</b>:${esData.create_time}</br>
			<b>Last_update_time</b>:${esData.last_update_time}</br>

			<b>IP A</b>:${esData.ip_a}</br>
				<b>l4_port_a</b>:${esData.l4_port_a}</br>
				<b>IP B</b>:${esData.ip_b}</br>
				<b>l4_port_b</b>:${esData.l4_port_b}</br>
				<b>Service_id</b>:${esData.service_id}</br>
				<b>TM Rule ID</b>:${esData.tm_rule_id}</br>
				<b>Service_name</b>:${esData.service_name}</br>
				<b>Service_group_name</b>:${esData.service_group_name}</br>
			<b>uri</b>:10.0.0.189</br>
			<input type="hidden" id="ip" name="ip"
							value="10.0.0.191"></input>
				<b>Web Host</b>:${esData.web_host}</br>
			<b>User Agent</b>:${esData.user_agent}</br>
				<b>Packets</b>:${esData.pkts}</br>
			<b>Bytes</b>:${esData.bytes}</br>

			<b>flow_closure_flagv</b>:${esData.flow_closure_flag}</br>
				<b>avg_tcp_win_sz_a_to_b</b>:${esData.avg_tcp_win_sz_a_to_b}</br>
				<b>avg_tcp_win_sz_b_to_a</b>:${esData.avg_tcp_win_sz_b_to_a}</br>
				<b>productivity_index</b>:${productivity_index}</br>
				<b>risk_index</b>:${risk_index}</td>
				<td><input type="button" value="PREPARE PCAP"
							onclick=callOnClick(${indexClicked.index},"10.0.0.191")></input>
							<input type="hidden" value="${indexClicked.index}" name="index"
							id="index"></input></td>
							</c:forEach>
			</tr>
			<c:if test="${from ne 0 }">
			<input type="button" value="prev" onclick="callPrev()"></input>
			</c:if>
			
			<input type="button" value="next" onclick="callNext()"></input>
		</table>
	</div>

	<div id="pcap"></div>
	</form>