<?php
require 'vendor/autoload.php';
use Parse\ParseClient;
ParseClient::initialize('4n6JU0CQuBv0LzLoV3dSXDLZTPtPj5ZPd7wSlcRm', 'b8R8Dyh5UTSsJt8WGLWikf93JWDoYxixP9Py9CKF', 'G1ZkAFkBH3QAlruSd8WZ9jvOq20clO3EzMoEBjGZ');
use Parse\ParseObject;

use Parse\ParseQuery;

try {
$AdObject = ParseObject::create("Ads");

$AdObject->set("CallSid",$_GET["CallSid"]);

$query = new ParseQuery("Farmer");

$Contact_No=substr( $_GET['CallFrom'],1);

$query->equalTo("Contact_No", $Contact_No);

$object = $query->first();

$objectId=$object->getObjectId();

$AdObject->set('Farmer_Ids',$objectId);

$quantity=$_GET["digits"];
$quantity=trim($quantity, '"');
$AdObject->set('Max',$quantity);
$AdObject->set('Max_Quantity_Available',(int)$quantity);

$AdObject->save();


echo 0;
}
catch(execption $e)
{
	echo 1;
}
?>