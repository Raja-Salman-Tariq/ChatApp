<?php 

include "conn.php";

if(isset($_POST['number'], $_POST['pwd'], $_POST['id'])){

	$num=$_POST['number'];
	$pwd=$_POST["pwd"];
	$id=$_POST["id"];





	$q="SELECT * FROM user WHERE number = '$num' AND pwd = '$pwd'";

	$res=mysqli_query($con, $q);

	if ($res->num_rows>0){

		$qq="UPDATE `devices` SET number='$num' WHERE id='$id'";
		$resu=mysqli_query($con, $qq);

		if ($resu){
			echo "login successful !";
		}
	}
	else{
		echo "login failure !";
		echo $pwd;
	}
}


?>