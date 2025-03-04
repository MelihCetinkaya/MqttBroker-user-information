package com.example.heatandhumidity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.util.*

class ComparisonsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var temperatureRecordTextView: TextView
    private lateinit var humidityRecordTextView: TextView
    private lateinit var mqttClient: MqttAndroidClient
    private lateinit var drawerLayout: DrawerLayout
    private val handler = Handler(Looper.getMainLooper())
    private val reconnectRunnable = object : Runnable {
        override fun run() {
            if (!mqttClient.isConnected) {
                setupMqttClient()
            }
            handler.postDelayed(this, 2000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comparisons)

        // Setup toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.navView)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        temperatureRecordTextView = findViewById(R.id.temperatureRecordTextView)
        humidityRecordTextView = findViewById(R.id.humidityRecordTextView)

        temperatureRecordTextView.text = "-"
        humidityRecordTextView.text = "-"

        setupMqttClient()
        handler.post(reconnectRunnable)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Go to WelcomeActivity (home)
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish() // Close this activity
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Profil seçildi", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_comparisons -> {
                // Already in comparisons, just close drawer
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_settings -> {
                Toast.makeText(this, "Ayarlar seçildi", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupMqttClient() {
        val serverUri = "tcp://192.168.109.162:1883"
        val clientId = "AndroidClient-Comparisons-" + UUID.randomUUID().toString()
        mqttClient = MqttAndroidClient(applicationContext, serverUri, clientId, Ack.AUTO_ACK)

        val options = MqttConnectOptions().apply {
            isCleanSession = true
            connectionTimeout = 60
            keepAliveInterval = 60
            isAutomaticReconnect = true
        }

        try {
            println("Attempting to connect to local MQTT broker at $serverUri")
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    println("MQTT Connection successful")
                    subscribeToTopics()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    println("MQTT Connection failed: ${exception?.message}")
                    exception?.printStackTrace()
                    runOnUiThread {
                        temperatureRecordTextView.text = "-"
                        humidityRecordTextView.text = "-"
                    }
                }
            })
        } catch (e: MqttException) {
            println("MQTT Exception during connect: ${e.message}")
            e.printStackTrace()
            runOnUiThread {
                temperatureRecordTextView.text = "-"
                humidityRecordTextView.text = "-"
            }
        }
    }

    private fun subscribeToTopics() {
        println("Setting up MQTT callbacks...")
        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                println("MQTT Connection lost: ${cause?.message}")
                cause?.printStackTrace()
                runOnUiThread {
                    temperatureRecordTextView.text = "-"
                    humidityRecordTextView.text = "-"
                }
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val value = String(message?.payload ?: ByteArray(0))
                println("Message received - Topic: $topic, Value: $value")
                
                if (value.isNotEmpty()) {
                    runOnUiThread {
                        when (topic) {
                            "sensor/temperature_record" -> {
                                println("Setting temperature record to: $value")
                                temperatureRecordTextView.text = value
                            }
                            "sensor/humidity_record" -> {
                                println("Setting humidity record to: $value")
                                humidityRecordTextView.text = value
                            }
                        }
                    }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                println("Message delivery complete")
            }
        })

        println("Subscribing to topics...")
        try {
            mqttClient.subscribe("sensor/temperature_record", 1, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    println("Successfully subscribed to temperature record topic")
                    mqttClient.subscribe("sensor/humidity_record", 1, null, object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            println("Successfully subscribed to humidity record topic")
                        }
                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            println("Failed to subscribe to humidity record: ${exception?.message}")
                            exception?.printStackTrace()
                        }
                    })
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    println("Failed to subscribe to temperature record: ${exception?.message}")
                    exception?.printStackTrace()
                }
            })
        } catch (e: MqttException) {
            println("Exception during subscribe: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            handler.removeCallbacks(reconnectRunnable)
            mqttClient.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}
