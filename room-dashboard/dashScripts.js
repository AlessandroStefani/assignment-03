
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
}

function goOn() {
    button.innerHTML = "ON";
    button.style.color = "black";
    button.style.backgroundColor = "yellow"
}