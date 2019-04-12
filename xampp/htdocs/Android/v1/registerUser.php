<?php

require_once '../includes/DbOperations.php';
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(
		isset($_POST['username']) and 
	         isset($_POST['password']) and 
	              isset($_POST['email']) and
	              	isset($_POST['secretQuestion']) and
	              	  isset($_POST['secretAnswer']))
	    {
		
		$db = new DbOperation();

		$result = $db->createUser(
			 $_POST['username'],
			 $_POST['password'],
			 $_POST['email'],
			 $_POST['secretQuestion'],
			 $_POST['secretAnswer']
			 );

		if($result == 1){
			$response['error'] = false;
			$response['message'] = "User Registered successfully";
		}elseif($result == 2){
			$response['error'] = true;
			$response['message'] = "Some Error occurred please try again";
		}elseif($result == 0){
			$response['test'] = 'test';
			$response['error'] = true;
			$response['message'] = "Username or Email already used.";
			$response['name'] = $_POST['username'];
			$response['email'] = $_POST['email'];
		}
	

	}else{
		$response['error'] = true;
		$response['message'] = "Required fields are missing";
	}
}else{
	$response['error'] = true;
	$response['message'] = "Invalid Request";
}

	$response['name'] = $_POST['username'];
	$response['email'] = $_POST['email'];

echo json_encode($response);