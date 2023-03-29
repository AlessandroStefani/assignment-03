const table = document.getElementById("tabella luci");
const button = document.getElementById("onoff");
const slider = document.getElementById("myRange");
const output = document.getElementById("output");

//Prendo tutte le info

let getTabella = new XMLHttpRequest();
getTabella.onload = function () {
    dati = JSON.parse(this.responseText);
    let dateIn, dateOut;
    let oreIn, oreOut;
    let totDiffSec, diffSec, diffMin, diffHours;
    for (acc in dati) {
        dateIn = new Date(dati[acc].in * 1000);
        oreIn = [dateIn.getHours(), dateIn.getMinutes(), dateIn.getSeconds()];
        if (dati[acc].out == "/") {
            dateOut = dati[acc].out;
            oreOut = ["/", "/", "/"];
            diffSec = "/";
            diffMin = "/";
            diffHours = "/"
        } else {
            dateOut = new Date(dati[acc].out * 1000);
            oreOut = [dateOut.getHours(), dateOut.getMinutes(), dateOut.getSeconds()];
            totDiffSec = (dateOut - dateIn) / 1000;
            diffSec = totDiffSec % 60;
            diffMin = Math.floor(totDiffSec / 60);
            diffHours = Math.floor(diffMin / 60);
            diffMin = diffMin % 60;
        }
        table.innerHTML += `<tr>
        <td>${oreIn[0]}:${oreIn[1]}:${oreIn[2]}</td>
        <td>${oreOut[0]}:${oreOut[1]}:${oreOut[2]}</td>
        <td>${diffHours}:${diffMin}:${diffSec}</td>
        </tr>`;
    }//DA SISTEMARE
}
getTabella.open("GET", "dashboard.php?storico", false);
getTabella.send();

let getLuci = new XMLHttpRequest();
getLuci.onload = function () {
    if (this.responseText == "on") {
        button.innerHTML = "ON";
        button.style.color = "black";
        button.style.backgroundColor = "yellow"
    } else if (this.responseText == "off") {
        button.innerHTML = "OFF";
        button.style.color = "white";
        button.style.backgroundColor = "black"
    }
};
getLuci.open("GET", "dashboard.php?luci", false);
getLuci.send();

let getTapparelle = new XMLHttpRequest();
getTapparelle.onload = function () {
    slider.value = this.responseText;
    output.innerHTML = `${slider.value}%`;
}
getTapparelle.open("GET", "dashboard.php?tapparelle", false);
getTapparelle.send();

//slider


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
        window.location.reload();
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
        window.location.reload();
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