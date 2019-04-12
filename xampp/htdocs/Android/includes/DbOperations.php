<?php
	
	class DbOperation{

		private $con;

		function __construct(){

			require_once dirname(__FILE__).'/DbConnect.php';

			$db = new DbConnect();

			$this->con = $db->connect();
		}

		public function createUser($username, $pass, $email, $secQue, $secAns){
			if($this->isUserExist($username, $email)){
				return 0;
			}else{
				$password = md5($pass);
				$stmt = $this->con->prepare("INSERT INTO `accounts`(`id`, `Username`, `Email`, `Password`, `secretQuestion`, `secretAnswer`) VALUES (NULL, ?, ?, ?, ?, ?);");
				$stmt->bind_param("sssss", $username, $email, $password, $secQue, $secAns);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}

			}
		}

		public function createSetting($accountid, $policyid, $name, $flag){
			if($this->isSettingExist($accountid, $policyid, $name)){
				return 0;
			}else{
				$stmt = $this->con->prepare("INSERT INTO `Setting` (`id`, `AccountID`, `PolicyID`, `Name`, `flag`) VALUES (NULL, ?, ?, ?, ?);");
				$stmt->bind_param("ssss", $accountid, $policyid, $name, $flag);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}
			}
		}

		public function updateSetting($accountid, $policyid, $name, $status){
			$stmt = $this->con->prepare("UPDATE `setting` SET `flag`= ? WHERE accountid = ? AND policyid = ? AND name = ?");
			$stmt->bind_param("ssss", $status, $accountid, $policyid, $name);
			if($stmt->execute()){
				return 1;
			}else{
				return 0;
			}
		}

		public function updateAccount($accountid, $gmailAddress){
			$stmt = $this->con->prepare("UPDATE `accounts` SET `gmail`=? WHERE id = ?");
			if(empty($gmailAddress)){
				return 0;
			}
			$stmt->bind_param("ss", $accountid, $gmailAddress);
			if($stmt->execute()){
				return 1;
			}else{
				return 0;
			}
		}

		public function userLogin($username, $pass){
			$password = md5($pass);
			$stmt = $this->con->prepare("SELECT id FROM accounts WHERE username = ? AND password = ?");
			$stmt->bind_param("ss", $username, $password);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}

		public function getUserByUsername($username){
			$stmt = $this->con->prepare("SELECT * FROM accounts WHERE username = ?");
			$stmt->bind_param("s", $username);
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
		}

		public function getSettingByFields($accountid, $policyid, $name){
			$stmt = $this->con->prepare("SELECT * FROM setting WHERE accountid = ? AND policyid = ? AND Name = ?");
			$stmt->bind_param("sss", $accountid, $policyid, $name);
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
		}

		private function isUserExist($username, $email){
			$stmt = $this->con->prepare("Select id FROM accounts WHERE username = ? OR email = ?");
			$stmt->bind_param("ss", $username, $email);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;

		}

		private function isSettingExist($accountid, $policyid, $name){
			$stmt = $this->con->prepare("SELECT id FROM Setting WHERE accountid = ? AND policyid = ? AND name = ?");
			$stmt->bind_param("sss", $accountid, $policyid, $name);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}

		private function locationExists($accountid){
			$stmt = $this->con->prepare("SELECT id FROM locations WHERE userid = ?");
			$stmt->bind_param("s", $accountid);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}

		public function insertHome($userid, $home){
			if($this->locationExists($userid)){
				$stmt = $this->con->prepare("UPDATE `locations` SET `home`= ? WHERE userid = ?;");
				$stmt->bind_param("ss", $userid, $home);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
					}
				}
			else{
				$stmt = $this->con->prepare("INSERT INTO `locations` (`id`, `userid`, `home`) VALUES (NULL, ?, ?);");
				$stmt->bind_param("ss", $userid, $home);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}
			}
		}

		public function insertWork($userid, $work){
			if($this->locationExists($userid)){
				$stmt = $this->con->prepare("UPDATE `locations` SET `work`= ? WHERE userid = ?;");
				$stmt->bind_param("ss", $work, $userid);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
					}
				}
			else{
				$stmt = $this->con->prepare("INSERT INTO `locations` (`id`, `userid`, `work`) VALUES (NULL, ?, ?);");
				$stmt->bind_param("ss", $userid, $work);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}
			}
		}

		public function getLocation($userid){
			$stmt = $this->con->prepare("SELECT * FROM locations WHERE userid = ?");
			$stmt->bind_param("s", $userid);
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
		}

		public function checkPassword($userid){
			$stmt = $this->con->prepare("SELECT username FROM accounts WHERE username = ?");
			$stmt->bind_param("s", $userid);
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
		}

		public function updatePassword($userid, $newpassword){
			$stmt = $this->con->prepare("UPDATE `accounts` SET `password`=? WHERE username = ?");
			if(empty($newpassword)){
				return 0;
			}
			$password = md5($newpassword);
			$stmt->bind_param("ss", $password, $userid);
			if($stmt->execute()){
				return 1;
			}else{
				return 0;
			}
		}

		public function forgotPassword($username, $secQue, $secAns){
			$stmt = $this->con->prepare("SELECT password FROM accounts WHERE username = ? AND secretQuestion = ? AND secretAnswer = ?");
			$stmt->bind_param("sss", $username, $secQue, $secAns);
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
		}
	}