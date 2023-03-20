#include "Led.h"
#include "Arduino.h"

Led::Led(int pin, int mode) {
  this->pin = pin;
  pinMode(this->pin, mode);
}

void Led::turnOn() {
  digitalWrite(this->pin, HIGH);
  this->statusL = true;
}

void Led::turnOff() {
  digitalWrite(this->pin, LOW);
  this->statusL = false;
}

bool Led::statusLed() {
  return this->statusL;
}