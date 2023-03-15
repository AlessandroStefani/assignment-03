


function onOff() {
    button = document.getElementById("onoff").innerHTML;
    switch (button) {
        case "ON":
            document.getElementById("onoff").innerHTML = "OFF";
            break;
        case "OFF":
            document.getElementById("onoff").innerHTML = "ON";
            break;
        default:
            break;
    }
}