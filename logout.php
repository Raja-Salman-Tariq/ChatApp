<?php

include "conn.php";

if (isset($_POST["id"])){

	$id=$_POST["id"];
	$val="";
	$q="UPDATE `devices` SET number='$val' WHERE number='$id'";

	# here number and id both are the phone number of the person.
	# the id used here is NOT the uid id in the table which is the android id
	# this system works because the number is also supposed to be unique.

	$res=mysqli_query($con,$q);

	if ($res){
		echo "1";
	}

	else{
		echo "unable to logout";
	}

}

else{
	echo "hello";
}

?>