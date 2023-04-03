from serial import Serial
from threading import Thread
import datetime
import mqttComm
import msgHttp

startDay = datetime.time(8)
endDay = datetime.time(19)
firstEntry = True
statoLuci = False
arduino = 0

while 1:
    porta = input("inserire nome porta seriale: ")
    try:
        arduino = Serial(porta, 9600, timeout=1)
    except:
        print("errore nella connessione tramite porta: " + porta)
    if (arduino):
        print("connesso a porta: " + porta)
        break

mqttThread = Thread(None, mqttComm.loop, None)
mqttThread.start()
while 1:
    # messaggi da arduino (BlueThoot), trasmessi alla dashboard
    serMsg = arduino.readline().decode().strip()
    if (len(serMsg)):
        if (serMsg == "Luci Accese"):
            data = {"luci": "on"}
            if statoLuci == False:
                statoLuci = True
                msgHttp.post(data)
        elif (serMsg == "Luci Spente"):
            data = {"luci": "off"}
            if statoLuci == True:
                statoLuci = False           
                msgHttp.post(data)
        elif (serMsg.startswith("Livello Tapparelle:")):
            lv = serMsg.split(":")[1]
            data = {"tapparelle": lv}
            print(data)
            print(msgHttp.post(data))

    # comandi dalla dashboard, trasmessi ad arduino
    dashMsg = msgHttp.get("comando")
    if len(dashMsg) > 0:
        print("dashboard dice: " + dashMsg)
    if "tapparelle" in dashMsg:
        serCmd = "servo:" + dashMsg.split(":")[1] + "\n"
        arduino.write(serCmd.encode())
        data = {"comando": ""}
        msgHttp.post(data)
    elif "luci" in dashMsg:
        if "on" in dashMsg:
            statoLuci = True
            arduino.write(b"on\n")
        elif "off" in dashMsg:
            statoLuci = False
            arduino.write(b"off\n")
        data = {"comando": ""}
        msgHttp.post(data)

    # segnali dai sensori, trasmessi ad arduino e dashboard
    msgMqtt = ""
    mqttComm.lock.acquire()
    if mqttComm.mqttMsg != "":
        msgMqtt = mqttComm.mqttMsg.decode()
        mqttComm.mqttMsg = ""
    mqttComm.lock.release()
    if msgMqtt:
        if (msgMqtt == "on"):
            arduino.write(b"on\n")
            if statoLuci == False:
                statoLuci = True
                msgHttp.post({"luci": "on"})
        elif (msgMqtt == "off"):
            arduino.write(b"off\n")
            if statoLuci == True:
                statoLuci = False
                msgHttp.post({"luci": "off"})
        elif (msgMqtt == "in" and datetime.datetime.now().time() > startDay and firstEntry):
            firstEntry = False
            arduino.write(b"servo:0\n")
            msgHttp.post({"tapparelle": "0"})
        elif (msgMqtt == "out" and datetime.datetime.now().time() > endDay):
            firstEntry = True
            arduino.write(b"servo:100\n")
            msgHttp.post({"tapparelle": "100"})
        
    