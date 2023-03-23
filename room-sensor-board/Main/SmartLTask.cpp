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
}

void SmartLTask::sendApproveMessage() {
  // TODO
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
        this->sendApproveMessage();
      }

      break;
    case ON:
      if (intensityLevel >= Lmax && !INHIBIT_LS) {
        led->turnOff();
        state = OFF;
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
      }
      break;
  }
}
