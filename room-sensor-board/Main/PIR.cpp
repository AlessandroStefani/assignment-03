#include "PIR.h"
#include "Arduino.h"

PIR::PIR(int pin) {
  this->pin = pin;
  this->detectedStatus = false;
  pinMode(pin, INPUT);
  //Serial.println("Pir initialize");
  this->calibratePIR();
  state = NOT_DETECTED;
}

void PIR::calibratePIR() {
  Serial.print("Calibrating sensor... ");
  for(int i = 0; i < CALIBRATION_TIME_SEC; i++){
    //Serial.print(".");
    delay(1000);
  }
}

bool PIR::isSomeoneDetected() {
  detectedStatus = digitalRead(pin);
  if (detectedStatus) {
    //Serial.println("detected!");
  } else {
    //Serial.println("no more detected.");
  }
  return detectedStatus;
}

void PIR::changeState() {
  switch(state) {
    case DETECTED:
      if (!this->isSomeoneDetected()) {
        state = NOT_DETECTED;
        this->ts = millis();
      }
      break;

    case NOT_DETECTED:
      this->timeNotDetected = millis() - this->ts;

      if (this->isSomeoneDetected()) {
        this->timeNotDetected = 0;
        state = DETECTED;
      }
      break;
  }
}

// this method returns true when someone is detected between 0 and TIME_1 ms, else false.
bool PIR::isSomeoneDetectedT1() {
  return this->isSomeoneDetected() && (this->timeNotDetected >= 0 && this->timeNotDetected < TIME_1);
}

enum PIR::PIRState PIR::getState() {
  return this->state;
}
