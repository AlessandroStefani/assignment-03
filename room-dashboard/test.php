<?php
$testJ = file_get_contents('data.json');
$testD = json_decode($testJ, true);
$storico = $testD["storico"];
$len = sizeof($storico);
$storico += array($len=>array("in"=>time(), "out"=>"/"));
var_dump($storico);
var_dump(json_encode($storico, true));
?>