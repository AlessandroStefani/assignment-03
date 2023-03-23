/*
 * Second example, about tasks that can be run
 * thanks to FreeRTOS support.  
 *
 */
#include "Led.h"
#include "LightSensor.h"
#include "PIR.h"
#include "SmartLTask.h"

#define LIGHTSENSOR_PIN 7 //the pin must be analog
#define PIR_PIN 10
 
TaskHandle_t Task1;
TaskHandle_t sTask;
TaskHandle_t pTask;
TaskHandle_t lsTask;

Led* led1;
LightSensor* ls;
PIR* pir;
SmartLTask* slt;

const int led_1 = 4;
const int led_2 = 5;

void setup() {
  Serial.begin(115200); 
  ls = new LightSensor(LIGHTSENSOR_PIN);
  pir = new PIR(PIR_PIN);
  slt = new SmartLTask(led_1, pir, ls);  

  // CPU 0 is for protocols like: Wifi or Bluetooth (RF Comunication)
  xTaskCreatePinnedToCore(Task1code,"Task1",10000,NULL,1,&Task1,0);                         
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

void Task1code( void * parameter ){
  Serial.print("Task1 is running on core ");
  Serial.println(xPortGetCoreID());

  for(;;){
    //led1->turnOn();
    //digitalWrite(led_1, HIGH);
    //delay(500);
    //led1->turnOff();
    //digitalWrite(led_1, LOW);
    delay(500);
    pir->isSomeoneDetected();
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
