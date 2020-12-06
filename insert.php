<?php 

include "conn.php";

// echo "RUning";	

$responce=array();


if(isset(
	$_POST['name'], $_POST['thumbnail'], $_POST['number'], 
	$_POST['status'], $_POST['img'], $_POST['pwd'], $_POST['dev']
	))
{

	$responce["status"]="succeeding...";

	// echo "Naaaaa";
	$name=$_POST['name'];
	$thumbnail=$_POST["thumbnail"];
	$number=$_POST["number"];
	$status=$_POST["status"];
	$img=$_POST["img"];
	$pwd=$_POST["pwd"];
	$dev=$_POST['dev'];

	$q2="SELECT * FROM devices WHERE id='$dev'";
	$res=mysqli_query($con, $q2);

	if (mysqli_num_rows($res)<1){
		$q2="INSERT INTO `devices`(`id`, `number`) VALUES ('$dev','$number')";
		mysqli_query($con, $q2);
	}

	else{
		$q2="UPDATE `devices` SET number='$number' WHERE id='$dev'";
		mysqli_query($con, $q2);
	}


	$q="INSERT INTO `user`(`name`, `thumbnail`, `status`, `number`, `img`, `pwd`) VALUES ('$name','$thumbnail','$status','$number','$img','$pwd')";

	$res=mysqli_query($con, $q);

	if ($res){
		echo "registration successful !";
		exit();
	}
	else{
		echo "registration failure !";
		exit();
	}
}

else
{
	// echo "Daaaa";
	$responce["id"]="NA";
	$responce["req_msg"]="Incomplete Req";
	$responce["req_code"]="0";
	// $responce['name']=$_POST['name'];
	// $responce["thumbnail"]=$_POST["thumbnail"];
	// $responce["number"]=$_POST["number"];
	// $responce["status"]=$_POST["status"];
	// $responce["image"]=$_POST["image"];
}

// $x="hI";

$x=json_encode($responce);
echo $x;

?>