#include "Led.h"
#include "Arduino.h"

Led::Led(int pin)
{
    this->pin = pin;
    pinMode(pin, OUTPUT);
}

void Led::off()
{
    digitalWrite(pin, LOW);
}

void Led::on()
{
    digitalWrite(pin, HIGH);
}