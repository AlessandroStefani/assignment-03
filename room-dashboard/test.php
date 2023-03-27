<?php
$testJ = file_get_contents('data.json');
$testD = json_decode($testJ, true);
var_dump(getdate()[0]);
?>