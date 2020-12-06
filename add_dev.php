<?php 


include "conn.php";

$responce=array();



if(isset($_POST['id'], $_POST['number'])){

	$id=$_POST['id'];
	$num=$_POST['number'];

	
	$q="INSERT INTO `devices`(`id`, `number`) VALUES ('$id','$num')";

	$res = mysqli_query($con, $q);

	if ($res){
		echo "true";
	}

	else {
		echo "false";
	}

	echo "\n\nResult: \n";
	echo $res;


}

?>