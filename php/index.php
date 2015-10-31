<?php
require 'vendor/autoload.php';
 
use Parse\ParseClient;
 
ParseClient::initialize('4n6JU0CQuBv0LzLoV3dSXDLZTPtPj5ZPd7wSlcRm', 'b8R8Dyh5UTSsJt8WGLWikf93JWDoYxixP9Py9CKF', 'G1ZkAFkBH3QAlruSd8WZ9jvOq20clO3EzMoEBjGZ');
use Parse\ParseObject;
 
$testObject = ParseObject::create("Farmer");
$testObject->set("Farmer_Name", "Lalit123");
$testObject->save();

echo "Hello World!";

?>
