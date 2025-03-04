package com.springexample.interuserinformationapp.service;

import com.springexample.interuserinformationapp.entity.RecordValues;
import com.springexample.interuserinformationapp.entity.User;
import com.springexample.interuserinformationapp.entity.ValueType;
import com.springexample.interuserinformationapp.entity.Values;
import com.springexample.interuserinformationapp.repository.RecordRepo;
import com.springexample.interuserinformationapp.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttService {

    private final UserRepo userRepo;
    private final RecordRepo recordRepo;

    private User user;
    private double maxTemperature;
    private double recordTemperature;
    private double maxHumidity;
    private double recordHumidity;

    private MqttClient client;

    @Value("${mqtt.broker}")
    private String brokerUrl;

    @Value("${mqtt.topic.temperature}")
    private String temperature;

    @Value("${mqtt.topic.humidity}")
    private String humidity;


    public void init(String username) {
        try{user = userRepo.findUserByUsername(username);
            recordTemperature=userRepo.findUserRecordHeat().getValues().getTemperature();
            recordHumidity=userRepo.findUserRecordHumidity().getValues().getHumidity();
        }
        catch(Exception ignored){

        }

        if (user != null ) {

            if (user.getValues() == null) {

                Values values = new Values();
                values.setTemperature(0.0);
                values.setHumidity(0.0);
                user.setValues(values);
            }

            maxTemperature = user.getValues().getTemperature();
            maxHumidity = user.getValues().getHumidity();

        }
        else{
            System.out.println("user is null");
        }

        new Thread(this::startMqttListener).start();// ana iş akışını engellememek için thread kullandık.
    }

    public void startMqttListener() {
        while (true) {
            try {
                if (client == null || !client.isConnected()) {
                    connectMqtt();
                }

                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void connectMqtt() {
        try {
            client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            client.connect(options);


            client.subscribe(temperature, (topic, message) -> {
                String payload = new String(message.getPayload());
                System.out.println("📥 MQTT Mesaj Alındı - Topic: " + topic + ", Değer: " + payload);

                try {
                    double value = Double.parseDouble(payload);
                    checkAndSaveTemperature(value);
                } catch (Exception e) {
                    System.err.println("🚨 MQTT Mesajı Hatalı: " + payload);
                    e.printStackTrace();
                }
            });

            client.subscribe(humidity, (topic, message) -> {
                double value = Double.parseDouble(new String(message.getPayload()));
                checkAndSaveHumidity(value);
            });

            System.out.println("MQTT bağlantısı başarılı!");
        } catch (Exception e) {
            System.err.println("MQTT bağlanamadı, tekrar denenecek...");
            e.printStackTrace();
        }
    }

    private void checkAndSaveHumidity(double humidity) {
        if (humidity > maxHumidity) {
            maxHumidity = humidity;
            user.getValues().setHumidity(humidity);
            userRepo.save(user);
            if(humidity > recordHumidity) {
                RecordValues recordValues = new RecordValues();
                recordValues.setName(user.getName());
                recordValues.setValueType(ValueType.Humidity);
                recordValues.setValue(humidity);
                recordRepo.save(recordValues);
                publishHumidityRecord("sensor/humidity_record",
                        "new humidity record: " + humidity + " from user named " + user.getName());
            }
        }
    }

    private void checkAndSaveTemperature(double temperature) {

        if (temperature > maxTemperature) {
            maxTemperature = temperature;
            user.getValues().setTemperature(temperature);
            userRepo.save(user);
            if(temperature > recordTemperature) {
            RecordValues recordValues = new RecordValues();
            recordValues.setName(user.getName());
            recordValues.setValueType(ValueType.Temperature);
            recordValues.setValue(temperature);
            recordRepo.save(recordValues);

                publishTemperatureRecord("sensor/temperature_record",
                        "new temperature record: " + temperature + " from user named " + user.getName());

            }
        }
    }

    private void publishTemperatureRecord(String topic, String message) {
        try {
            client.publish(topic, message.getBytes(), 2, true);
        } catch (Exception e) {
            System.err.println("publish edilirken hata meydana geldi: " + e);
        }
    }

    private void publishHumidityRecord(String topic, String message) {
        try {
            client.publish(topic, message.getBytes(), 2, true);
        } catch (Exception e) {
            System.err.println("publish edilirken hata meydana geldi: " + e);
        }
    }

}
