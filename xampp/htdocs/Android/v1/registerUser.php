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

		$result = $db->createUser(
			 $_POST['username'],
			 $_POST['password'],
			 $_POST['email']
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