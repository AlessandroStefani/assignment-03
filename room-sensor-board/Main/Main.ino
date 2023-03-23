#include <WiFi.h>
#include "PubSubClient.h"
#include "Led.h"
#include "LightSensor.h"
#include "PIR.h"
#include "SmartLTask.h"

#define MSG_BUFFER_SIZE  50
#define LIGHTSENSOR_PIN 7 //the pin must be analog
#define PIR_PIN 37
#define LED_PIN 4
 
TaskHandle_t wTask;
TaskHandle_t sTask;
TaskHandle_t pTask;
TaskHandle_t lsTask;

Led* led1;
LightSensor* ls;
PIR* pir;
SmartLTask* slt;

/* wifi network info */

const char* ssid = "Wind3 HUB - 03C89C";
const char* password = "43ys4kxxtc762yxx";

/* MQTT server address */
const char* mqtt_server = "broker.mqtt-dashboard.com";

/* MQTT topic */
const char* topic = "esiot-2022";

/* MQTT client management */

WiFiClient espClient;
PubSubClient client(espClient);


unsigned long lastMsgTime = 0;
char msg[MSG_BUFFER_SIZE];
int value = 0;

bool messageAlreadySent = false;

void setup_wifi() {

  delay(10);

  Serial.println(String("Connecting to ") + ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

/* MQTT subscribing callback */
// PENSO CHE QUESTO METODO SARÀ DA TOGLIERE, PER ADESSO LO TENGO PER TESTING
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.println(String("Message arrived on [") + topic + "] len: " + length );
}

void reconnect() {
  
  // Loop until we're reconnected
  
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    
    // Create a random client ID
    String clientId = String("esiot-2122-client-")+String(random(0xffff), HEX);

    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
      // Once connected, publish an announcement...
      // client.publish("outTopic", "hello world");
      // ... and resubscribe
      client.subscribe(topic);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(115200); 
  ls = new LightSensor(LIGHTSENSOR_PIN);
  pir = new PIR(PIR_PIN);
  slt = new SmartLTask(LED_PIN, pir, ls);

  // all setups for WiFi
  setup_wifi();
  randomSeed(micros());
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback); 

  // CPU 0 is for protocols like: Wifi or Bluetooth (RF Comunication)
  xTaskCreatePinnedToCore(WifiTask,"wTask",10000,NULL,1,&wTask,0);                         
  delay(500); 

  // CPU 1 is for running application program
  // the fifth arg of function is for the priority
  xTaskCreatePinnedToCore(PIRTask,"pTask",10000,NULL,1,&pTask,1);          
  delay(500);

  xTaskCreatePinnedToCore(LightSensorTask, "lsTask", 10000, NULL, 1, &lsTask, 1);
  delay(500);

  xTaskCreatePinnedToCore(SmartTask,"sTask",10000,NULL,2,&sTask,1);          
  delay(500);
}

void WifiTask( void * parameter ){
  Serial.print("Task1 is running on core ");
  Serial.println(xPortGetCoreID());

  for(;;){
    delay(5);
    
    if (!client.connected()) {
      reconnect();
    }
    client.loop();

    unsigned long now = millis();

    if (now - lastMsgTime > 10000) {
      lastMsgTime = now;

      // essenzialmente cerco di mandare il messaggio di notifica solo una volta
      // tramite il booleano messageAlreadySent
      bool sendMessage = slt->sendApproveMessage();
      if (sendMessage && !messageAlreadySent) {
        messageAlreadySent = true;
        /* creating a msg in the buffer */
        snprintf (msg, MSG_BUFFER_SIZE, "on");

        Serial.println(String("Publishing message: ") + msg);
    
        /* publishing the msg */
        client.publish(topic, msg);
      } else if (!sendMessage) {
        messageAlreadySent = false;
      }
    }
    
  } 
}

void SmartTask( void * parameter ){
  Serial.print("SmartLTask is running on core ");
  Serial.println(xPortGetCoreID());

  for(;;){
    slt->tick();
    delay(1000);
  }
}

void PIRTask( void * parameter ){
  Serial.print("PIRTask is running on core ");
  Serial.println(xPortGetCoreID());

  for(;;){
    pir->changeState();
    delay(500);
  }
}

void LightSensorTask( void * parameter ){
  Serial.print("LightSensorTask is running on core ");
  Serial.println(xPortGetCoreID());

  for(;;){
    ls->calculateIntensity();
    delay(250);
  }
}

void loop() {
  Serial.print("this is the main loop running on core ");
  Serial.println(xPortGetCoreID());
  delay(1000000);
}
