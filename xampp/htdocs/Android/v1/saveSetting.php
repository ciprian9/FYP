<?php

require_once '../includes/DbOperations.php';
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(
		isset($_POST['accountid']) and 
	         isset($_POST['policyid']) and 
	              isset($_POST['name']) and
	           		   isset($_POST['status']))	
	    {
		
		$db = new DbOperation();

		$result = $db->createSetting(
			 $_POST['accountid'],
			 $_POST['policyid'],
			 $_POST['name'],
			 $_POST['status']
			 );

		if($result == 1){
			$response['error'] = false;
			$response['message'] = "User Registered successfully";
		}elseif($result == 2){
			$response['error'] = true;
			$response['message'] = "Some Error occurred please try again";
		}elseif($result == 0){
			$response['error'] = true;
			$response['message'] = "Username or Email already used.";
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