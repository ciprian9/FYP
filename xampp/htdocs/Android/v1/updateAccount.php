<?php

require_once '../includes/DbOperations.php';
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(
		isset($_POST['accountid']) and 
	        isset($_POST['gmail']))	
	    {
		
		$db = new DbOperation();

		$result = $db->updateAccount(
			$_POST['gmail'],
			$_POST['accountid']
			);

		if($result == 1){
			$response['error'] = false;
			$response['message'] = "Updated Succesfully";
		}elseif($result == 0){
			$response['error'] = true;
			$response['message'] = "Failed to update setting.";
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