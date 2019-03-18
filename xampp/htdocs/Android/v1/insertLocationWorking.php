<?php

require_once '../includes/DbOperations.php';
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(
		isset($_POST['userid']) and 
	        isset($_POST['work']))	                                                       
	    {
		
		$db = new DbOperation();

		$result = $db->insertWork(
			 $_POST['userid'],                              
			 $_POST['work']
			 );

		if($result == 1){
			$response['error'] = false;
			$response['message'] = "Added Location";
		}elseif($result == 2){
			$response['error'] = true;
			$response['message'] = "Some Error occurred please try again";
		}elseif($result == 0){
			$response['error'] = true;
			$response['message'] = "Already Used.";
		}
	

	}else{
		$response['error'] = true;
		$response['message'] = "Required fields are missing";
	}
}else{
	$response['error'] = true;
	$response['message'] = "Invalid Request";
}

echo json_encode($response);