const table = document.getElementById("tabella luci");
const button = document.getElementById("onoff");
const slider = document.getElementById("myRange");
const output = document.getElementById("output");

//Prendo tutte le info

let getTabella = new XMLHttpRequest();
getTabella.onload = function () {
    dati = JSON.parse(this.responseText);
    let dateIn;
    let oreIn;
    let dateOut;
    let oreOut;
    let diff;
    for (acc in dati) {
        dateIn = new Date(dati[acc].in * 1000);
        oreIn = [dateIn.getHours(), dateIn.getMinutes(), dateIn.getSeconds()];
        if (dati[acc].out == "/") {
            dateOut = dati[acc].out;
            oreOut = ["/", "/", "/"];
            diff = "/";
        } else {
            dateOut = new Date(dati[acc].out * 1000);
            oreOut = [dateOut.getHours(), dateOut.getMinutes(), dateOut.getSeconds()];
            diff = (dateOut - dateIn) / 1000;
        }
        table.innerHTML += `<tr>
        <td>${oreIn[0]}:${oreIn[1]}:${oreIn[2]}</td>
        <td>${oreOut[0]}:${oreOut[1]}:${oreOut[2]}</td>
        <td>${diff}</td>
        </tr>`;
    }//DA SISTEMARE
}
getTabella.open("GET", "dashboard.php?storico", false);
getTabella.send();

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
getTapparelle.onload = function () {
    slider.value = this.responseText;
}
getTapparelle.open("GET", "dashboard.php?tapparelle", false);
getTapparelle.send();

//slider

output.innerHTML = `${slider.value}%`;

slider.onmouseup = function () {
    output.innerHTML = `${this.value}%`;
    var postLvTapparelle = new XMLHttpRequest();
    postLvTapparelle.open("POST", "dashboard.php", false)
    postLvTapparelle.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    postLvTapparelle.send("tapparelle=" + this.value);
    postCommand("tapparelle:" + this.value);
}

function change() {
    switch (button.innerHTML) {
        case "ON":
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