<?php

require_once '../includes/DbOperations.php';
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(
		isset($_POST['accountid']) and 
	         isset($_POST['policyid']) and 
	              isset($_POST['name']))	
	    {
			$db = new DbOperation();

			$setting = $db->getSettingByFields($_POST['accountid'], $_POST['policyid'], $_POST['name']);
			$response['error'] = false;
			$response['message'] = "Read Succesfull";
			$response['status'] = $setting['flag'];
			// $response['email'] = $user['email'];
			// $response['username'] = $user['username'];
		}else{
			$response['error'] = true;
			$response['message'] = "Required fields are missing";
		}

}

echo json_encode($response);