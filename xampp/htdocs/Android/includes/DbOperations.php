<?php
	
	class DbOperation{

		private $con;

		function __construct(){

			require_once dirname(__FILE__).'/DbConnect.php';

			$db = new DbConnect();

			$this->con = $db->connect();
		}

		function createUser($username, $pass, $email){
			$password = md5($pass);
			$stmt = $this->con->prepare("INSERT INTO `Accounts` (`id`, `Username`, `Email`, `Password`) VALUES (NULL, ?, ?, ?);");
			$stmt->bind_param("sss", $username, $email, $password);
			if($stmt->execute()){
				return true;
			}else{
				return false;
			}

		}
	}