#define FREQ 5
#define PERIOD 1000/FREQ

#include "LightSensor.h"
#include "Arduino.h"

LightSensor::LightSensor(int pin) {
  this->pin = pin;
  pinMode(pin, INPUT);
}

double LightSensor::getIntensity() {
  int value = analogRead(pin);
  valueInVolt = ((double) value) * 5/1024;
  Serial.println(String(value) + " -> in volt: " + valueInVolt );

  return valueInVolt;
}
