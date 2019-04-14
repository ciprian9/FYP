<?php

require_once '../includes/DbOperations.php';
$response = array();

if($_SERVER['REQUEST_METHOD']=='POST'){

	if(
		isset($_POST['username']) and 
	        isset($_POST['secretQues']) and
	            isset($_POST['secretAns']))	
	    {
		$db = new DbOperation();

		$response = $db->forgotPassword(
			$_POST['username'],
			$_POST['secretQues'],
			$_POST['secretAns']
			);

		$response['error'] = false;
		$response['message'] = "Recieved Password";

	}else{
		$response['error'] = true;
		$response['message'] = "Required fields are missing";
	}
}else{
	$response['error'] = true;
	$response['message'] = "Invalid Request";
}

echo json_encode($response);