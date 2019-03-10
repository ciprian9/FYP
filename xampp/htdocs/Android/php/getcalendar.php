<?php

require_once 'calendar.php';

if($_SERVER['REQUEST_METHOD']=='POST'){

	if(isset($_POST['username'])){
		$eo = new eventsOperations();

		$response = $eo->getToken($_POST['username']);
		$response['message'] = 'OK';
		$response['error'] = false;
	}else{
		$response['error'] = true;
		$response['message'] = "Required username are missing";
		$response['url'] = '';
	}
	echo json_encode($response);
}

