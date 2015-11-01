<?php
require 'vendor/autoload.php';
 
use Parse\ParseClient;
ParseClient::initialize('4n6JU0CQuBv0LzLoV3dSXDLZTPtPj5ZPd7wSlcRm', 'b8R8Dyh5UTSsJt8WGLWikf93JWDoYxixP9Py9CKF', 'G1ZkAFkBH3QAlruSd8WZ9jvOq20clO3EzMoEBjGZ');
use Parse\ParseObject;
use Parse\ParseQuery; 
try
{
$query=new ParseQuery("Ads");
$query->equalTo("CallSid", $_GET["CallSid"]);
$object = $query->first();


$quantity=$_GET["digits"];
$quantity=trim($quantity, '"');
$object->set('Rate_Per_Kg',(int)$quantity);
$object->save();


echo 0;
}
catch(execption $e)
{
	echo 1;
}
?>