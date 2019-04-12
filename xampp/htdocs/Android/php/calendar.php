<?php
	require __DIR__ . '/vendor/autoload.php';
	$client = NULL;
	$tokenPath = '';
	$exists = false;
	class eventsOperations{

		public function getToken($username){
			$client = $this->createClient();
			$tokenPath = $username;
			$tokenPath = $tokenPath.'/token.json';
		
    		$authUrl = $client->createAuthUrl();
    		//printf("Open the following link in your browser:\n%s\n", $authUrl);
    		//print 'Enter verification code: ';
    		//$authCode = trim(fgets(STDIN));
    		$response['url'] = $authUrl;
    		$response['message'] = 'need to create token';
            
   			return $response;
   		}

   		public function tokenExists($username){
   			$tokenPath = $username;
			$tokenPath = $tokenPath.'/token.json';
			$response['exist'] = file_exists($tokenPath);
			$response['path'] = $tokenPath;
			$response['name'] = $username;
   			return $response;
   		}

   		public function createToken($username, $tokenCode){
   			$client = $this->createClient();
			$tokenPath = $username;
			$tokenPath = $tokenPath.'/token.json';
			$exists = false;
			$client = $this->checkTheToken($client, $tokenPath);

       		if(!$exists){
       			$accessToken = $client->fetchAccessTokenWithAuthCode($tokenCode);
             	$client->setAccessToken($accessToken);
       		}

       		if (array_key_exists('error', $accessToken)) {
                throw new Exception(join(', ', $accessToken));
            }

            if (!file_exists(dirname($tokenPath))) {
	            mkdir(dirname($tokenPath), 0700, true);
	        }
	        file_put_contents($tokenPath, json_encode($client->getAccessToken()));
   		}

   		private function createClient(){
   			$client = new Google_Client();
			$client->setApplicationName('WebApp');
			$client->setScopes(Google_Service_Calendar::CALENDAR_READONLY);
			$client->setAuthConfig('client_secret.json');
			$client->setAccessType('offline');
			$client->setPrompt('select_account consent');
			return $client;
   		}

   		private function checkTheToken($client, $tokenPath){
   			if (file_exists($tokenPath)) {
				$accessToken = json_decode(file_get_contents($tokenPath), true);
				$client->setAccessToken($accessToken);
				$exists = true;
			}

			if ($client->isAccessTokenExpired()) {
        // Refresh the token if possible, else fetch a new one.
        		if ($client->getRefreshToken()) {
            		$client->fetchAccessTokenWithRefreshToken($client->getRefreshToken());
       			}
       		}
       		return $client;
   		}

   		public function getEvents($username){
   			$tokenPath = $username;
			$tokenPath = $tokenPath.'/token.json';
   			$client = $this->createClient();
   			$client = $this->checkTheToken($client, $tokenPath);
   			$service = new Google_Service_Calendar($client);

			$optParams = array(
			  'maxResults' => 100,
			  'orderBy' => 'startTime',
			  'singleEvents' => true,
			  'timeMin' => date('c', strtotime(date("Y-m-d 00:00:01"))),
			  'timeMax' => date('c', strtotime(date("Y-m-d 23:59:59"))),
			);
			$results = $service->events->listEvents("primary", $optParams);
			$array = [];
			$events = $results->getItems();
			if (empty($events)) {
			    
			} else {
			   
			    foreach ($events as $event) {
			        $start = $event->start->dateTime;
			        $end = $event->getEnd();
					if (empty($start)) {
			            $start = $event->start->date;
			        }
					$respose['summary'] = $event->getSummary();
					$respose['startTime'] = $start;
					$respose['endTime'] = $end['dateTime'];
					array_push($array, $respose);
			    }
				
				return $array;
   		}
	}
}