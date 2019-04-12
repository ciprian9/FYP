<?php

require_once 'calendar.php';

if($_SERVER['REQUEST_METHOD']=='POST'){

	if(isset($_POST['username'])){
		$eo = new eventsOperations();

		$response = $eo->tokenExists($_POST['username']);
		if ($response['exist']==false) {
			$response['error'] = true;
			$response['message'] = "File Not Found";
			$response['exists'] = false;
		} else {
			$response['message'] = 'OK';
			$response['error'] = false;
		}
	}else{
		$response['error'] = true;
		$response['message'] = "Required username are missing";
		$response['exists'] = false;
	}
	echo json_encode($response);
}


