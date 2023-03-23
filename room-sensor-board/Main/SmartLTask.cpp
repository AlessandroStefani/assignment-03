#include "SmartLTask.h"
#include "LightSensor.h"
#include "Arduino.h"

#define INHIBIT_LS 0

const double Lmax = 4.0;

SmartLTask::SmartLTask(int ledPin, PIR* pir, LightSensor* lightSensor) {
  this->pir = pir;
  this->led = new Led(ledPin, OUTPUT);
  this->LS = lightSensor;
  state = OFF;
  this->lighOn = false;
}

// this method return true if you can send approve message through MQTT
// when the light must be On
bool SmartLTask::sendApproveMessage() {
  if (this->lighOn) {
    Serial.println("Status Led: on");
  } else {
    Serial.println("Status Led: off");
  }
  return this->lighOn;
}

void SmartLTask::tick() {
  bool stateDetection = pir->isSomeoneDetected();
  double intensityLevel = LS->getIntensity();
  switch(state) {
    case OFF:
      if (stateDetection && (INHIBIT_LS || intensityLevel < Lmax)) {
        led->turnOn();
        state = ON;
        // here where i send through MQTT the message
        this->lighOn = true;
      }

      break;
    case ON:
      if (intensityLevel >= Lmax && !INHIBIT_LS) {
        led->turnOff();
        state = OFF;
        // you notify that the led is Off
        this->lighOn = false;
      } else if (!stateDetection) {
        state = TURNING_OFF;
      }
      break;
    case TURNING_OFF:
      if (pir->isSomeoneDetectedT1()) {
        state = ON;
      } else {
        led->turnOff();
        state = OFF;
        this->lighOn = false;
      }
      break;
  }
}
