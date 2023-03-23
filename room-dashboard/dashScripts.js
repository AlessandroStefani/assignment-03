
const button = document.getElementById("onoff");
const slider = document.getElementById("myRange");
const output = document.getElementById("output");

let getLuci = new XMLHttpRequest();
getLuci.onload = function () {
    if (this.responseText == "on") {
        goOn();
    } else if (this.responseText == "off") {
        goOff();
    }
};
getLuci.open("GET", "dashboard.php?luci", false);
getLuci.send();

let getTapparelle = new XMLHttpRequest();
getTapparelle.onload = function() {
   slider.value = this.responseText;
}
getTapparelle.open("GET", "dashboard.php?tapparelle", false);
getTapparelle.send();

output.innerHTML = `${slider.value}%`;

slider.oninput = function() {
  output.innerHTML = `${this.value}%`;
  postCommand("tapparelle:" + this.value);
}

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
    var postCmdLuci = new XMLHttpRequest();
    postCmdLuci.onload = function () {
        postCommand("luci:off");
    };
    postCmdLuci.open("POST", "dashboard.php", true);
    postCmdLuci.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    postCmdLuci.send("luci=off");
}

function goOn() {
    button.innerHTML = "ON";
    button.style.color = "black";
    button.style.backgroundColor = "yellow"
    var postCmdLuci = new XMLHttpRequest();
    postCmdLuci.onload = function () {
        postCommand("luci:on");
    };
    postCmdLuci.open("POST", "dashboard.php", true);
    postCmdLuci.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    postCmdLuci.send("luci=on");
}

function postCommand(comando) {
    var postCmd = new XMLHttpRequest();
    postCmd.open("POST", "dashboard.php", true);
    postCmd.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    postCmd.send(`comando=${comando}`);
}