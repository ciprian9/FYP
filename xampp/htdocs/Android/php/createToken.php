<?php

require_once 'calendar.php';

if($_SERVER['REQUEST_METHOD']=='POST'){

	if(isset($_POST['username']) and isset($_POST['tokenCode'])){
		$eo = new eventsOperations();

		$response = $eo->createToken($_POST['username'], $_POST['tokenCode']);
		$response['message'] = 'OK';
		$response['error'] = false;
	}else{
		$response['error'] = true;
		$response['message'] = "Required username are missing";
		$response['url'] = '';
	}
	echo json_encode($response);
}

