<?php
//POST
if (isset($_POST["tapparelle"])) { // es tapparelle:57
    echo post("tapparelle");
    /*
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    $data["tapparelle"] = $_POST["tapparelle"];
    $newJsonString = json_encode($data);
    file_put_contents('data.json', $newJsonString);
    header("dashboard-page.html"); //refresh della pagina con i nuovi dati
    echo $newJsonString;
    */
} else if (isset($_POST["luci"])) { // es luci:on TODO la data/ora
    echo post("luci");
    /*
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    $data["luci"] = $_POST["luci"];
    $newJsonString = json_encode($data);
    file_put_contents('data.json', $newJsonString);
    header("dashboard-page.html"); //refresh della pagina con i nuovi dati
    echo $newJsonString;
    */
}

//GET
if (isset($_GET["tapparelle"])) {
    echo get("tapparelle");
    /*
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    echo $data["tapparelle"];
    */
} else if (isset($_GET["luci"])) {
    echo get("luci");
    /*
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    echo $data["luci"];
    */
} else if (isset($_GET["storico"])) {
    echo get("storico");
    /*
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    echo $data["storico"];
    */
}

function post($dataType){
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    $data[$dataType] = $_POST[$dataType];
    $newJsonString = json_encode($data);
    file_put_contents('data.json', $newJsonString);
    header("dashboard-page.html"); //refresh della pagina con i nuovi dati
    return $newJsonString;
}

function get($dataType) {
    $jsonString = file_get_contents('data.json');
    $data = json_decode($jsonString, true);
    return $data[$dataType];
}

?>