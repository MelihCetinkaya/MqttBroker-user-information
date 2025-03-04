
#include <DHT.h>
#include <WiFi.h>
#include <PubSubClient.h>

#define DHTPIN 14        
#define DHTTYPE DHT11   


const char* ssid = "Simon Adebisi";
const char* password = "simon357";

// MQTT Broker bilgileri
const char* mqtt_server = "192.168.109.162";  
const int mqtt_port = 1883;
const char* mqtt_topic_temp = "esp32/temperature";
const char* mqtt_topic_hum = "esp32/humidity";

WiFiClient espClient;
PubSubClient client(espClient);
DHT dht(DHTPIN, DHTTYPE);

void setup_wifi() {
  delay(10);
  Serial.println("WiFi'ya bağlanıyor...");
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi bağlandı");
  Serial.println("IP adresi: ");
  Serial.println(WiFi.localIP());
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("MQTT'ye bağlanıyor...");
    String clientId = "ESP32Client-";
    clientId += String(random(0xffff), HEX);
    
    if (client.connect(clientId.c_str())) {
      Serial.println("bağlandı");
    } else {
      Serial.print("hata, rc=");
      Serial.print(client.state());
      Serial.println(" 5 saniye sonra tekrar dene");
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(9600);
  dht.begin();
  setup_wifi();
  client.setServer(mqtt_server, mqtt_port);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  float h = dht.readHumidity();      
  float t = dht.readTemperature();   

  if (!isnan(h) && !isnan(t)) { 
    
    Serial.print("Nem: ");
    Serial.print(h);
    Serial.println(" %");
    Serial.print("Sıcaklık: ");
    Serial.print(t);
    Serial.println(" C");

    // MQTT üzerinden gönder
    char temp[10];
    char hum[10];
    dtostrf(t, 4, 2, temp);
    dtostrf(h, 4, 2, hum);
    
    client.publish(mqtt_topic_temp, temp);
    client.publish(mqtt_topic_hum, hum);
  } else {
    Serial.println("DHT11 Okuma Hatası!");
  }

  delay(2000);  
}
