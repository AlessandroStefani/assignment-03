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

    read = arduino.readline().decode()
    if (len(read)):
        if (read == "Luci Accese"):
            data = {"luci":"on"}
            msgHttp.post(data)
        if (read == "Luci Spente"):
            data = {"luci":"off"}
            msgHttp.post(data)
        if (read.startswith("Livello Tapparelle:")):
            lv = read.split(":")[1]
            data = {"tapparelle":lv}
            msgHttp.post(data)