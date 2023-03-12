<?php
if(isset($_PUT["id"])){
    $testPost = $_PUT["id"];
} else {
    $testPost = 0;
}

require "dashboard-page.php"
?>