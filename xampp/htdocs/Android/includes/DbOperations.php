<?php
	
	class DbOperation{

		private $con;

		function __construct(){

			require_once dirname(__FILE__).'/DbConnect.php';

			$db = new DbConnect();

			$this->con = $db->connect();
		}

		public function createUser($username, $pass, $email){
			if($this->isUserExist($username, $email)){
				return 0;
			}else{
			$password = md5($pass);
			$stmt = $this->con->prepare("INSERT INTO `Accounts` (`id`, `Username`, `Email`, `Password`) VALUES (NULL, ?, ?, ?);");
			$stmt->bind_param("sss", $username, $email, $password);
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
		$stmt = $this->con->prepare("SELECT * FROM setting WHERE accountid = ? AND policyid = ? AND name = ?");
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
	}

