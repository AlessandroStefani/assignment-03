<?php
if(isset($_POST["tapparelle"])){ //tapparelle:57
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    $data["tapparelle"] = $_POST["tapparelle"];
    $newJsonString = json_encode($data);
    file_put_contents('jsonFile.json', $newJsonString);
} else if (isset($_POST["luci"])) { //luci:on TODO la data/ora
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    $data["luci"] = $_POST["luci"];
    $newJsonString = json_encode($data);
    file_put_contents('jsonFile.json', $newJsonString);
} 

require "data.json"
?>