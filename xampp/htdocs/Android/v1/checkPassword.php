<?php

require_once '../includes/DbOperations.php';

if($_SERVER['REQUEST_METHOD']=='POST'){

	if(isset($_POST['username'])){
		$eo = new DbOperation();

		$response = $eo->checkPassword($_POST['username']);
		$response['message'] = 'OK';
		$response['error'] = false;
	}else{
		$response['error'] = true;
		$response['message'] = "Required username are missing";
		$response['exists'] = false;
	}
	echo json_encode($response);
}