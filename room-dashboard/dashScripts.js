
const button = document.getElementById("onoff");

var xmlhttp = new XMLHttpRequest();
xmlhttp.onload = function () {
    if (this.responseText == "on") {
        goOn();
    } else if (this.responseText == "off") {
        goOff()
    }
};
xmlhttp.open("GET", "dashboard.php?luci", true);
xmlhttp.send();

function change() {
    switch (button.innerHTML) {
        case "ON": //immagino aggiungerò un comando al dashboard.php
            goOff();
            break;
        case "OFF":
            goOn();
            break;
        default:
            break;
    }
}

function goOff() {
    button.innerHTML = "OFF";
    button.style.color = "white";
    button.style.backgroundColor = "black"
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        postCommand("luci:off");
    };
    xmlhttp.open("POST", "dashboard.php", true);
    xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xmlhttp.send("luci=off");
}

function goOn() {
    button.innerHTML = "ON";
    button.style.color = "black";
    button.style.backgroundColor = "yellow"
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        postCommand("luci:on");
    };
    xmlhttp.open("POST", "dashboard.php", true);
    xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xmlhttp.send("luci=on");
}

function postCommand(comando) {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", "dashboard.php", true);
    xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xmlhttp.send(`comando=${comando}`);
}