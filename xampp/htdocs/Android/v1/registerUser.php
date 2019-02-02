<?php

require_once '../includes/DbOperations.php';
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(
		isset($_POST['username']) and 
	         isset($_POST['password']) and 
	              isset($_POST['email']))	
	    {
		
		$db = new DbOperation();

		if($db->createUser(
			 $_POST['username'],
			 $_POST['email'],
			 $_POST['password'])){
			$response['error'] = false;
			$response['message'] = "User Registered successfully";
		}else{
			$response['error'] = true;
			$response['message'] = "Some Error occurred please try again";
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