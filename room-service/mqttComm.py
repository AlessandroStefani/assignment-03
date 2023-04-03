import paho.mqtt.client as mqtt
from threading import Lock

lock = Lock()
mqttMsg = ""

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print("Mqtt connected with result code "+str(rc))
    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("assignment-03")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    global mqttMsg
    with lock:
        mqttMsg = msg.payload

def loop():
    client = mqtt.Client("ass-03.Comm")
    client.on_connect = on_connect
    client.on_message = on_message

    client.connect("broker.mqtt-dashboard.com", 1883, 60)

    client.loop_forever()
