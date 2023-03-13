import serial
import time
import random

arduino = 0

while 1:
    porta = input("inserire nome porta seriale: ")
    try:
        arduino=serial.Serial(porta, 9600, timeout=1)
    except:
        print("errore nella connessione tramite porta: " + porta)
    if(arduino):
        break


while 1:
    
    read = arduino.readline().decode()
    if(len(read)):
        print(read)
        '''
        num = random.randint(101,200)
        msg = "svo:" + str(num) + "\n"
        time.sleep(2)
        arduino.write(msg.encode())
        '''
    