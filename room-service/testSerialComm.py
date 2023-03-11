import serial
import time
import random

arduino=serial.Serial('COM4', 9600,timeout=1)

print("Test moving blinds (servo) on arduino")

while 1:
    print(arduino.readline().decode())
    num = random.randint(0,100)
    msg = "servo:" + str(num) + "\n"
    time.sleep(2)
    arduino.write(msg.encode())