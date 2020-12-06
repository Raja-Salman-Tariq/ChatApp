<?php 

include "conn.php";

  
if (isset($_POST['id'])){

	$id=$_POST['id'];

	$q="SELECT number FROM devices WHERE `id` = '$id'";

	$res=mysqli_query($con,$q);

	if (mysqli_num_rows($res)>0){
		// echo "truee";

		while ($row = $res->fetch_row()){
			echo $row[0];
			exit();
		}

		// echo "boi";

	}

	// else{
	// 	echo "false";


	// 	while ($row = $res->fetch_row());
	// 		echo $row[0];
	// 	// mysqli_num_rows($res);
	// }

}
else{

	// echo "Incomplete request";

}

// echo json_encode("post was empty");


?>