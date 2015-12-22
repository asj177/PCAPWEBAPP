<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<!-- <meta http-equiv="refresh" content="5"> -->
<title>PCAP</title>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/pcap.js"></script>
	
	
	<script type="text/javascript">
	function reload(){
		var percentage=$("#percentage").val();
		if(percentage<100){
			
		
		setTimeout('window.location.reload();', 5000);
		}
	}
	
	
	</script>

<h1>PCAP Retrival Status</h1>
</head>

<body onload="reload()">


	<table border="1">
		<tr>
			<td>
				<table border="1">
					<tr>


						<td>Ip and port</td>

						<td>${ip_a}</td>

					</tr>
					<tr>
						<td>Flow Start Time</td>

						<td>${flow_time}</td>


					</tr>
					<tr>
						<td>Retrival Status</td>

						<td>

							<div class="progress">
							<input type="hidden" value="${percent}" id="percentage" name="percentage"></input>
								<div class="progress-bar" role="progressbar" aria-valuenow="80"
									aria-valuemin="0" aria-valuemax="100"
									style="width:${percentage_complete}">
									${percentage_complete}</div>
							</div>

						</td>

					</tr>
					<tr>
					<td>Mining Stats</td>
					<td>PacketsSearched:${pkts_searched }  PacketsMatched:${pkts_matched }  Pages Searched:${pages_searched } Directories Searched:${directory_entries_searched}</td>
					
					
					</tr>

				</table>





			</td>


			<td>
				<form method="get" action="operation">
				<input type="hidden" name="flow_id" id="flow_id" value="${ flow_id}"></input>
					<input type="submit" name="cancel" id="cancel" value="cancel"></input>
					&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" name="delete"
						id="delete" value="delete"></input> <input type="hidden"
						name="path" id="path" value="${path }"></input> </br> </br>
					<c:if test="${percentage_complete_int=='greater'}">
						<input type="submit" name="open" id="open" value="open"></input>




					</c:if>
				</form>




			</td>


		</tr>




	</table>




</body>


</html>