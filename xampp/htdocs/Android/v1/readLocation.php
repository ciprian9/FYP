<?php

require_once '../includes/DbOperations.php';
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(isset($_POST['userid'])){
		$db = new DbOperation();
		$location = $db->getLocation($_POST['userid']);
		$response['error'] = false;
		$response['message'] = "Read Succesfull";
		$response['home'] = $location['home'];
		$response['work'] = $location['work'];
	}else{
		$response['error'] = true;
		$response['message'] = "Required fields are missing";
	}
}


echo json_encode($response);