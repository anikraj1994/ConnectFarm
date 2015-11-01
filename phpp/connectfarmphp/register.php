<?php
require 'vendor/autoload.php';
 
use Parse\ParseClient;
 
ParseClient::initialize('4n6JU0CQuBv0LzLoV3dSXDLZTPtPj5ZPd7wSlcRm', 'b8R8Dyh5UTSsJt8WGLWikf93JWDoYxixP9Py9CKF', 'G1ZkAFkBH3QAlruSd8WZ9jvOq20clO3EzMoEBjGZ');
use Parse\ParseObject;
use Parse\ParseGeoPoint;
$farmerObject = ParseObject::create("Farmer");
$point = new ParseGeoPoint((int)$_POST["Lat"], (int)$_POST["Log"]);
$farmerObject->set("Farmer_Name", $_POST["Name"]);
$farmerObject->set("Contact_No", $_POST["ContactNo"]);
$farmerObject->set("Farmer_Location", $point);
$farmerObject->set("Farmer_Address", $_POST["Address"]);
$farmerObject->set("Language", $_POST["Language"]);
  $farmerObject->save();
  try {
  $post_data = array(
    // 'From' doesn't matter; For transactional, this will be replaced with your SenderId;
    // For promotional, this will be ignored by the SMS gateway
    'From'   => '08039511969',
    'To'    => array('07772823928', $_POST["ContactNo"]),
    'Body'  => 'Thanks For registration '.$_POST["Name"].'! Call 08039511969 for selling your product. Thankyou.'
);
 
$exotel_sid = "recursion"; // Your Exotel SID - Get it from here: http://my.exotel.in/Exotel/settings/site#api-settings
$exotel_token = "6ce7e6c1450d7017e7694827e109011d0895d09f"; // Your exotel token - Get it from here: http://my.exotel.in/Exotel/settings/site#api-settings
 
$url = "https://".$exotel_sid.":".$exotel_token."@twilix.exotel.in/v1/Accounts/".$exotel_sid."/Sms/send";
 
$ch = curl_init();
curl_setopt($ch, CURLOPT_VERBOSE, 1);
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_FAILONERROR, 0);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($post_data));
 
$http_result = curl_exec($ch);
$error = curl_error($ch);
$http_code = curl_getinfo($ch ,CURLINFO_HTTP_CODE);
 
curl_close($ch);
}
catch(Exception $e)
  {
    echo 0;
  }; 

  echo 0;
?>
