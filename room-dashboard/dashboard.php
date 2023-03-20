<?php
//POST
if (isset($_POST["tapparelle"])) { // es tapparelle:57
    echo post("tapparelle");
} else if (isset($_POST["luci"])) { // es luci:on TODO la data/ora
    echo post("luci");
} else if (isset($_POST["comando"])) {
    echo post("comando");
}

//GET
if (isset($_GET["comando"])) {
    echo get("comando");
} else if (isset($_GET["tapparelle"])) {
    echo get("tapparelle");
} else if (isset($_GET["luci"])) {
    echo get("luci");
} else if (isset($_GET["storico"])) {
    echo get("storico");
}

function post($dataType){
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    $data[$dataType] = $_POST[$dataType];
    $newJsonString = json_encode($data);
    file_put_contents('data.json', $newJsonString);
    return $newJsonString;
}

function get($dataType) {
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    $return = $data[$dataType];
    return $return;
}

?>