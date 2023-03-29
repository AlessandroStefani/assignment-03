<?php

//POST
if (isset($_POST["tapparelle"])) { // es tapparelle:57
    if ($_POST["tapparelle"] >= 0 && $_POST["tapparelle"] <= 100) { //da vedere se funziona
        echo post("tapparelle");
    } else {
        echo "comando tapparelle non riconosciuto";
    }
} else if (isset($_POST["luci"])) { // es luci:on TODO la data/ora
    if ($_POST["luci"] == "on" || $_POST["luci"] == "off"){
        aggDatiTabella($_POST["luci"], time());
        echo post("luci");
    } else {        
        echo "comando luci non riconosciuto";
    }
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
    echo json_encode(get("storico"));
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

function aggDatiTabella($statoL, $time) {
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    $storico = $data["storico"];
    $len = sizeof($storico);
    if ($statoL == "on") {
        $storico += array($len=>array("in"=>$time, "out"=>"/"));
    } else if ($statoL == "off"){
        $storico[$len - 1]["out"] = $time;
    }
    $data["storico"] = $storico;
    $newJsonString = json_encode($data);
    file_put_contents('data.json', $newJsonString);
}

?>