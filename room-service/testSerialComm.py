import serial
import msgHttp
import time
import random

# num = random.randint(101,200)
# msg = "svo:" + str(num) + "\n"
# time.sleep(2)
# arduino.write(msg.encode())

arduino = 0

while 1:
    porta = input("inserire nome porta seriale: ")
    try:
        arduino = serial.Serial(porta, 9600, timeout=1)
    except:
        print("errore nella connessione tramite porta: " + porta)
    if (arduino):
        print("connesso a porta: " + porta)
        break


while 1:

    serMsg = arduino.readline().decode().strip()
    if (len(serMsg)):
        if (serMsg == "Luci Accese"):
            data = {"luci": "on"}
            print(data)
            print(msgHttp.post(data))
        elif (serMsg == "Luci Spente"):
            data = {"luci": "off"}
            print(data)
            print(msgHttp.post(data))
        elif (serMsg.startswith("Livello Tapparelle:")):
            lv = serMsg.split(":")[1]
            data = {"tapparelle": lv}
            print(data)
            print(msgHttp.post(data))

    # da implementare in dashboard
    dashMsg = msgHttp.get("comando")
    print(dashMsg)
    if "tapparelle" in dashMsg:
        serCom = "servo:" + dashMsg["tapparelle"] + "\n"
        arduino.write(serCom) #forse da sistemare
        data = {"comando":""}
        msgHttp.post(data)
    elif "luci" in dashMsg:
        serCom = "luci" + dashMsg["luci"] + "\n"
        arduino(serCom)
        data = {"comando":""}
        msgHttp.post(data)
        
            
        