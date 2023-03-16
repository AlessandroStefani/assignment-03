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
        break


while 1:

    serMsg = arduino.readline().decode()
    if (len(serMsg)):
        if (serMsg == "Luci Accese"):
            data = {"luci":"on"}
            msgHttp.post(data)
        if (serMsg == "Luci Spente"):
            data = {"luci":"off"}
            msgHttp.post(data)
        if (serMsg.startswith("Livello Tapparelle:")):
            lv = serMsg.split(":")[1]
            data = {"tapparelle":lv}
            msgHttp.post(data)
            
    #da implementare in dashboard
    dashMsg = eval(msgHttp.get("comando"))
    if "tapparelle" in dashMsg:
        serCom = "servo:" + dashMsg["tapparelle"] + "\n"
        arduino.write(serCom) #forse da sistemare
    elif "luci" in dashMsg:
        serCom = "luci" + dashMsg["luci"] + "\n"
        arduino(serCom)
        
            
        