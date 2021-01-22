/*    Daniel Burruchaga Sola

      HAMPO.ino

      (X)Lectura de Temperatura Humedad / Modificacion Temp
      (X)SEGURIDAD
      (X)MOVIMIENTO
      (X)LDR
      (X)APRENDIZAJE ---> BUZZER + BOTON
*/

#include <analogWrite.h>
#include <ESP32Tone.h>
#include <ESP32PWM.h>
//#include <M5Stack.h>
#include "driver/timer.h"
#include "driver/adc.h"
#include <DHT.h>
#include <DHT_U.h>
//========================================== Buffer Datos ==================================
float bufferTemp[100], bufferHum[100], bufferLum[100];
unsigned int cont = 1;
//========================================== OLED PANTALLA ================================
#include <Adafruit_BusIO_Register.h>
#include <Adafruit_I2CDevice.h>
#include <Adafruit_I2CRegister.h>
#include <Adafruit_SPIDevice.h>

#include <SPI.h>
#include <Wire.h>
#include "Adafruit_GFX.h"
#include "Adafruit_SSD1306.h"
#include <Adafruit_I2CDevice.h>
#define SCREEN_WIDTH 128 // OLED display width, in pixels
#define SCREEN_HEIGHT 64 // OLED display height, in pixels

// Declaration for an SSD1306 display connected to I2C (SDA, SCL pins)
#define OLED_RESET     -1 // Reset pin # (or -1 if sharing Arduino reset pin)
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

#define PIN_DESINFECTAR 23
#define PIN_LED_UP 17
#define PIN_LED_DOWN 16
bool energyKey = false;
//======================================== TIMER =========================================================
#include <WiFi.h>
#include "time.h"

char ssid[15];
char password[15];

const char* ntpServer = "pool.ntp.org";
const long  gmtOffset_sec = 3600;
const int   daylightOffset_sec = 3600;
bool syncDone = false;
void connectWifi() {
  while (true) {
    if (Serial.available()) {

      display.clearDisplay();
      display.setTextSize(1);

        display.setTextColor(SSD1306_WHITE);
        display.setCursor(0, 0);
      if (Serial.read() == '+') {
        syncDone = true;
        display.println("SYNC DONE");
        display.display(); // actually display all of the above

        String SSID_string = Serial.readStringUntil('*');
        //  ssid  = data;
        //Serial.print(data);
        SSID_string.toCharArray(ssid, sizeof(SSID_string));
        String PASS_string = Serial.readStringUntil('$');
        PASS_string.toCharArray(password, sizeof(PASS_string));

        //Serial.println(ssid);
        //Serial.println(password);

        display.print("Connecting to ssid: ");
        display.println(SSID_string);
        display.print(" Password: ");
        display.println(PASS_string);
       display.display(); // actually display all of the above

        break;
      }
    }
    
  }
}

void printLocalTime()
{
  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) {
    //Serial.println("Failed to obtain time");
    return;
  }
  Serial.print(&timeinfo, "%A, %B %d %Y %H:%M:%S");
}
// ===================================== SERVO ===========================================================
#include <ESP32Servo.h>
int pinServo = 2;

void servo() {
  tone(pinServo, 100, // C
       2000); // half a second
  tone(pinServo, 5274, // E
       1000); // half a second
}

//====================================== ULTRASONIDOS COMEDERO ============================================
#define PIN_ECHO_COMER 35
#define PIN_ECHO_BEBER 34

#define PIN_TRIGGER 0


unsigned long counterSeconds = 0;
unsigned long lastMilli = 0;

long duration;
int distance, distance_bebedero, distance_comedero;
unsigned long lastMicros = 0;


int ultrasonidos(int Pin_echo) {
  digitalWrite(PIN_TRIGGER, LOW);

  if ((micros() - lastMicros) > 4) {
    digitalWrite(PIN_TRIGGER, HIGH);
  }
  // if ((micros() - lastMicros) > 30) {  //generamos Trigger (disparo) de 10us
  delayMicroseconds(7);
  digitalWrite(PIN_TRIGGER, LOW);
  duration = pulseIn(Pin_echo, HIGH);

  distance = duration * 0.034 / 2;
  lastMicros = micros();
  return distance;
}
void Ultrasonidos_Timer() {
  if ((millis() - lastMilli) > 1000) { // Cuenta cada segundo
    // Serial.println(counterSeconds);
    lastMilli = millis();
    counterSeconds++;
  }
  if (counterSeconds == 20) { // Cuenta cada Hora
    if (syncDone)sendAndroidValues();
    counterSeconds++;
    distance_bebedero = ultrasonidos(34);
  }

  //El trigger de los dos ultrasonidos van al mismo pin pero se ejecuta en tiempos diferentes

  if (counterSeconds == 40) { // Cuenta cada Hora
    distance_comedero = ultrasonidos(35);
    counterSeconds = 0;
  }
}
//=======================================TEMPERATURA/HUMEDAD/LUMI==========================================
#define LUM_PIN 15
#define DHTTYPE DHT11   // DHT 11
#define DHTPin  5     // what digital pin we're connected to

#define TemperaturaBaja 25
#define PIN_RELE 32
DHT dht(DHTPin, DHTTYPE);
float mediaTemp = 0, mediaHum = 0, mediaLum = 0;
unsigned int SumatorioLum = 0, maximoLum = 0, minimoLum = 4095, SumatorioTemp = 0, SumatorioHum = 0,  maximoHum = 0,  minimoHum = 4095 ;
float  maximoTemp = 0,  minimoTemp = 4095;
//============================================ PIR INTERRUPT Movimiento ==============================================
const byte interruptPinMovimiento = 25;
volatile int interruptCounterMovimiento = 0 ;
int numberOfInterruptsMovimiento = 0;

portMUX_TYPE mux = portMUX_INITIALIZER_UNLOCKED;

void IRAM_ATTR handleInterruptMovimiento() {
  portENTER_CRITICAL_ISR(&mux);
  interruptCounterMovimiento++;
  portEXIT_CRITICAL_ISR(&mux);
}
//============================================ PIR INTERRUPT Vueltas ==============================================
const byte interruptPinVueltas = 13;
volatile int interruptCounterVueltas = 0 ;
int numberOfInterruptsVueltas = 0;
float  metro_recorridos = 0;
portMUX_TYPE muxVueltas = portMUX_INITIALIZER_UNLOCKED;

void IRAM_ATTR handleInterruptVueltas() {
  portENTER_CRITICAL_ISR(&muxVueltas);
  interruptCounterVueltas++;
  portEXIT_CRITICAL_ISR(&muxVueltas);
}
//============================================  INTERRUPT SEGURIDAD  ==============================================
#define PIN_SEGURIDAD 18
volatile int interruptCounterSeguridad = 0 ;
int numberOfInterruptsSeguridad = 0;

portMUX_TYPE muxSeguridad = portMUX_INITIALIZER_UNLOCKED;

void IRAM_ATTR handleInterruptSeguridad() {
  portENTER_CRITICAL_ISR(&muxSeguridad);
  interruptCounterSeguridad++;
  portEXIT_CRITICAL_ISR(&muxSeguridad);
}
//============================================  INTERRUPT Adiestramiento ==============================================
//BUZZER
#define PIN_BUZZER 4

bool keyAdiestramiento = false;
int freq = 2000;
int channel = 0;
int resolution = 8;

const byte interruptPinAdiestramiento = 26;
volatile int interruptCounterAdiestramiento = 0 ;
int numberOfInterruptsAdiestramiento = 0;

portMUX_TYPE muxAdiestramiento = portMUX_INITIALIZER_UNLOCKED;

void IRAM_ATTR handleInterruptAdiestramiento() {
  portENTER_CRITICAL_ISR(&muxAdiestramiento);
  if (keyAdiestramiento) {
    interruptCounterAdiestramiento++;
  }
  portEXIT_CRITICAL_ISR(&muxAdiestramiento);
}
//=========================================== TIMER ========================================================

#define TIMER_DIVIDER   80
#define TIMER_SCALE     (TIMER_BASE_CLK / TIMER_DIVIDER) // 80e6/TIMER_DIVIDER
#define TIMER0_INTERVAL0_SEC   (1) // segundos
#define ESP_INTR_FLAG_TIMER0
#define BUF_SIZE 1024

volatile int Flag_ISR_Timer0 = 0;
hw_timer_t * timer0 = NULL;

void IRAM_ATTR ISR_Timer0() {
  Flag_ISR_Timer0 = 1; // Activacion de Timer
}

void TimerConfig() {
  // Configuracion Timer0
  timer0 = timerBegin(0, TIMER_DIVIDER, true); // = 1uS
  timerAttachInterrupt(timer0, &ISR_Timer0, true);
  timerAlarmWrite(timer0, 1000, true); //cada 1 000us segundos
  timerAlarmEnable(timer0);
}
///============================== GET TEMP/HUM ================================================
void getTempHumLum() {
  bufferTemp[cont] = dht.readTemperature(); // Buffer con datos de lectura Temperatura
  bufferHum[cont] = dht.readHumidity(); // Buffer con datos de lectura Humedad
  bufferLum[cont] = analogRead(LUM_PIN); // Buffer con datos de lectura Humedad
  //============================================================================================================================ DESCOMENTAR ======================================================0
  //Si no realiza bien la lectura
  if (isnan(bufferTemp[cont]) or isnan(bufferHum[cont])) {
    Serial.println("ERROR AL LEER SENSOR HUM/TEMP");
  }
  mediaTemp = media_temp();
  mediaHum = media_hum();
  mediaLum = map(media_lum(), 0, 4095, 0, 100);
}

void regularTemp() {
  if (mediaTemp < TemperaturaBaja) {
    digitalWrite(PIN_RELE, LOW);
  }
  else {
    digitalWrite(PIN_RELE, HIGH);
  }
}
//======================= TRATAMIENTO DE DATOS ============================================
float media_lum() {
  SumatorioLum = bufferLum[cont]  + SumatorioLum;

  if (bufferLum[cont] < minimoLum ) minimoLum = bufferLum[cont];  //Da el minimo
  if (bufferLum[cont] > maximoLum ) maximoLum = bufferLum[cont];  //Da el maximo
  float mediaLum = SumatorioLum / cont;
  return mediaLum;
}
float media_temp() {
  SumatorioTemp = bufferTemp[cont]  + SumatorioTemp;

  if (bufferTemp[cont] < minimoTemp ) minimoTemp = bufferTemp[cont];  //Da el minimo
  if (bufferTemp[cont] > maximoTemp ) maximoTemp = bufferTemp[cont];  //Da el maximo
  float mediaTemp = SumatorioTemp / cont;
  return mediaTemp;
}
float media_hum() {
  SumatorioHum = bufferHum[cont]  + SumatorioHum  ;

  if (bufferHum[cont] > maximoHum ) maximoHum = bufferHum[cont];  // Da el maximo
  if (bufferHum[cont] < minimoHum ) minimoHum = bufferHum[cont];  // Da el minimo
  int mediaHum = SumatorioHum / cont;
  return mediaHum;
}
//================================= RESET =================================================
void reset() {
  cont++;
  if (cont == 100) {
    cont = 1; // Return to home
    SumatorioHum = 0;
    SumatorioTemp = 0;
    SumatorioLum = 0;
  }
}
//=================================== INTERRUPCIONES =====================================
void Interrupts() {
  if (interruptCounterMovimiento > 0) {
    interruptCounterMovimiento = 0;
    portENTER_CRITICAL(&mux);
    portEXIT_CRITICAL(&mux);
    numberOfInterruptsMovimiento++;
    //Serial.print("MOVIMIENTO ");
    //Serial.println(numberOfInterruptsMovimiento);
  }

  if (interruptCounterVueltas > 0) {
    interruptCounterVueltas = 0;
    portENTER_CRITICAL(&mux);
    portEXIT_CRITICAL(&mux);
    numberOfInterruptsVueltas++;
    metro_recorridos = numberOfInterruptsVueltas * 0.12;

    //Serial.print("Vueltas ");
    //Serial.println(numberOfInterruptsVueltas);
  }

  if (interruptCounterSeguridad > 0) {
    interruptCounterSeguridad = 0;
    portENTER_CRITICAL(&muxSeguridad);
    portEXIT_CRITICAL(&muxSeguridad);
    numberOfInterruptsSeguridad++;
    //Serial.print("Seguridad");
    //Serial.println(numberOfInterruptsSeguridad);
  }
}
//================================= DESINFECTAR AGUA  && Servo =================================
void SerialCase() {
  if (Serial.available()) {
    switch (Serial.read()) {
      case 'g':  // EN EL SERIAL HAY QUE ENVIAR DOS ==  gg
        digitalWrite(PIN_DESINFECTAR, HIGH);
        // Serial.println("DESINFECTAR_ON");
        break;
      case 't':  // EN EL SERIAL HAY QUE ENVIAR DOS ==  gg
        digitalWrite(PIN_LED_UP, HIGH);
        digitalWrite(PIN_LED_DOWN, LOW);
        // Serial.println("UP");
        break;
      case 's':  // EN EL SERIAL HAY QUE ENVIAR DOS ==  gg
        digitalWrite(PIN_LED_DOWN, HIGH);
        digitalWrite(PIN_LED_UP, LOW);
        //  Serial.println("DOWN");
        break;
      case 'f':  // EN EL SERIAL HAY QUE ENVIAR DOS ==  ff
        digitalWrite(PIN_DESINFECTAR, LOW);
        // Serial.println("DESINFECTAR_OFF");
        break;
      case 'p':
        digitalWrite(PIN_LED_DOWN, HIGH);
        digitalWrite(PIN_LED_UP, HIGH);
        // Serial.println("BOTH");
        break;
      case 'q':
        digitalWrite(PIN_LED_DOWN, LOW);
        digitalWrite(PIN_LED_UP, LOW);
        // Serial.println("DOWN");
        break;
      case 'h':
        tone(pinServo, 100,
             2000); // half a second
        tone(pinServo, 5274,
             1000); // half a second
        ledcWriteTone(channel, 0); // EL IO2 Al Activarse Activa algo del BUZZER_PIN  que hay que desactivarlo así...
        break;
      case 'm': //Ahorro energia  ON
        energyKey = true;
        display.clearDisplay();
        break;
      case 'n': //Ahorro energia  OFF
        energyKey = false;
        display.clearDisplay();
        break;
      case 'k':
        sendAndroidValues();
        break;

    }
  }
}
//=================================== ADIESTRAMIENTO ===================================
/*
    Si llega por el serial una 'a' pitará el buzzer y activará eñ keyAdiestramiento, mientras
    esté en true, activará un contador de unos 30 segundos en los cuales el hamster tendrá que apretar
    el botón para recibir una gratificación
*/
unsigned int counterAdiestramiento = 0;

void Adiestramiento() {

  if (Serial.available() ) {
    switch (Serial.read()) {
      case 'a':
        ledcWriteTone(channel, 2000);
        keyAdiestramiento = true;
        //Serial.println("INICIO BUZZER");
        break;
    }
  }
  if (keyAdiestramiento) {
    counterAdiestramiento++;
    if (counterAdiestramiento == 100) {
      ledcWriteTone(channel, 0);
      //Serial.println("FIN BUZZER");
    }

    if (counterAdiestramiento == 1000) {/// 30 segundos para pulsar el botón
      counterAdiestramiento = 0;
      keyAdiestramiento = false;
      //Serial.println("FIN ADIESTRAMIENTO");

    }

    if  (interruptCounterAdiestramiento > 0) {
      keyAdiestramiento = false;
      //Serial.println("FIN ADIESTRAMIENTO");
      counterAdiestramiento = 0;
      interruptCounterAdiestramiento = 0;
      portENTER_CRITICAL(&muxAdiestramiento);
      portEXIT_CRITICAL(&muxAdiestramiento);
      numberOfInterruptsAdiestramiento++;
    }
  }
}
//=================================== MOSTRAR EN PANTALLA ==============================
void mostrarPantalla() {
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.print("Temperatura: "); //
  display.print(mediaTemp);
  display.println(" ºC");

  display.print("TMax: ");
  display.println(maximoTemp);
  display.print("TMin: ");
  display.println(minimoTemp);
  display.print("Humedad: "); ///
  display.print(mediaHum);
  display.println(" %");
  display.print("Lum: "); //
  display.print(mediaLum);
  display.println(" %");

  display.print("Movimiento: "); //
  display.println(numberOfInterruptsMovimiento);
  display.print("Metros: ");
  display.println(metro_recorridos);
  display.print("Seguridad: ");
  display.println(numberOfInterruptsSeguridad);
  display.display(); // actually display all of the above
}

//==========================================================================================
float Enviar[8] = {};

String datas[8] = {};
void sendAndroidValues()
{ datas[0] = "Temperatura";
  datas[1] = "Humedad";
  datas[2] = "Movimiento";
  datas[3] = "Luminosidad";
  datas[4] = "BebederoCm";
  datas[5] = "ComederoCm";
  datas[6] = "MetrosRecorridos";
  datas[7] = "Time";

  Enviar[0] = mediaTemp;
  Enviar[1] = mediaHum;
  Enviar[2] = numberOfInterruptsMovimiento;
  Enviar[3] = mediaLum;
  Enviar[4] = distance_bebedero;
  Enviar[5] = distance_comedero;
  Enviar[6] = metro_recorridos;
  Enviar[7] = 1;

  Serial.print('{');              //hay que poner # para el comienzo de los datos, así Android sabe que empieza el String de datos
  for (int k = 0; k < 8; k++)
  { Serial.print("\"");
    Serial.print(datas[k]);
    Serial.print("\"");
    Serial.print(":");
    Serial.print("\"");
    if (k == 7) {
      //init and get the time
      printLocalTime();
    }
    else {
      Serial.print(Enviar[k]);
    }
    Serial.print("\"");
    if ( k != 7) {
      Serial.print(',');            //separamos los datos con el +, así no es más fácil debuggear la información que enviamos
    }
  }
  Serial.print('}');               //con esto damos a conocer la finalización del String de datos
  Serial.print("##");               //con esto damos a conocer la finalización del String de datos
}


//=================================== SETUP ================================================
void setup() {
  //M5.begin();
  Serial.begin(9600);
  dht.begin();
  TimerConfig();
  //SERVO CONFIG
  //RELE PARA TEMPERATURA
  pinMode(PIN_LED_DOWN, OUTPUT);
  pinMode(PIN_LED_UP, OUTPUT);
  pinMode(PIN_RELE, OUTPUT);
  pinMode(PIN_DESINFECTAR, OUTPUT);
  //ULTRASONIDOS
  pinMode(PIN_TRIGGER, OUTPUT);
  pinMode(PIN_ECHO_COMER, INPUT);
  pinMode(PIN_ECHO_BEBER, INPUT);

  // INTERRUPCIÓN PARA PIR MOVIMIENTO
  pinMode(interruptPinMovimiento, INPUT);
  attachInterrupt(digitalPinToInterrupt(interruptPinMovimiento), handleInterruptMovimiento, FALLING);
  // INTERRUPCIÓN PARA  Vueltas
  // pinMode(interruptPinVueltas, INPUT);
  attachInterrupt(digitalPinToInterrupt(interruptPinVueltas), handleInterruptVueltas, FALLING);
  // INTERRUPCIÓN PARA ADIESTRAMIENTO
  pinMode(interruptPinAdiestramiento, INPUT);
  attachInterrupt(digitalPinToInterrupt(interruptPinAdiestramiento), handleInterruptAdiestramiento, FALLING);
  // INTERRUPCIÖN PARA SEGURIDAD ABRIR JAULA
  pinMode(PIN_SEGURIDAD, INPUT);
  attachInterrupt(digitalPinToInterrupt(PIN_SEGURIDAD), handleInterruptSeguridad, FALLING);
  //BUZZER
  ledcSetup(channel, freq, resolution);
  ledcAttachPin(PIN_BUZZER, channel);
  //DISPLAY OLED
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println(F("SSD1306 allocation failed"));
    for (;;);
  }
  delay(1000);
  /* Serial.println("***********************************************");
    Serial.println("            INICIADO HAMPO V1.0");
    Serial.println("***********************************************");
    Serial.println();
    Serial.println("             PRUEBAS VERIFICADAS");
    Serial.println();
    Serial.println("(X)Lectura de Temperatura Humedad / Modificacion Temp");
    Serial.println("(X)SEGURIDAD");
    Serial.println("(X)MOVIMIENTO");
    Serial.println("(X)LDR");
    Serial.println("(X)APRENDIZAJE ---> BUZZER + BOTON");
    Serial.println();
    Serial.println(" Para activar el modo Aprendizaje pulsar 'a'");
    Serial.println(" Para activar el modo Desinfección pulsar 'gg'");
    Serial.println(" Para desactivar el modo Desinfección pulsar 'ff'");
    Serial.println(" Para activar el modo Alimentación pulsar 'hh'");
    Serial.println(" CONECTANDO WIFI...");*/
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(0, 0);
  display.print("Connecting to ");
  display.print(ssid);

  display.display(); // actually display all of the above
  connectWifi();
  //Serial.print("SALIR DEL SETUP");
  //connect to WiFi
  WiFi.begin((char*)ssid, (char*) password);
  // CONECTANDO
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    display.print("...");
    display.display();
  }
  //Serial.print("Conectate");

  display.print(" CONNECTED ");
  //init and get the time
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
  // printLocalTime();
}
//=================================== LOOP ================================================
void loop() {
  if (Flag_ISR_Timer0) { // SI ESTA ACTIVADO EL FLAG TIMER
    Flag_ISR_Timer0 = 0; // DESACTIVAR EL FLAG
    getTempHumLum();
    regularTemp();
    reset();
    if (!energyKey)mostrarPantalla();
    else {
      display.clearDisplay();
      display.display();
    }
    Adiestramiento();
    Interrupts();
  }
  Ultrasonidos_Timer();
  SerialCase();
}
