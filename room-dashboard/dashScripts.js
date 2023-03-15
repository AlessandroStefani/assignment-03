
const button = document.getElementById("onoff");


function onOff() {
    switch (button.innerHTML) {
        case "ON": //immagino aggiungerò un comando al dashboard.php
            button.innerHTML = "OFF";
            button.style.color = "white";
            button.style.backgroundColor = "black"
            break;
        case "OFF":
            button.innerHTML = "ON";
            button.style.color = "black";
            button.style.backgroundColor = "yellow"
            break;
        default:
            break;
    }
}