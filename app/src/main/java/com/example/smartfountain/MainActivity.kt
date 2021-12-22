package com.example.smartfountain

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import protocols.UIUpdater
import com.example.smartfountain.MQTTManager

class MainActivity : Activity(), UIUpdater {

    var mqttManager:MQTTManager? = null

    // Interface methods
    override fun resetUIWithConnection(status: Boolean) {

        findViewById<EditText>(R.id.ipAddressField).isEnabled  = !status
        findViewById<EditText>(R.id.topicField).isEnabled      = !status
        findViewById<EditText>(R.id.messageField).isEnabled    = status
        findViewById<Button>(R.id.connectBtn).isEnabled      = !status
        findViewById<Button>(R.id.sendBtn).isEnabled         = status

        // Update the status label.
        if (status){
            updateStatusViewWith("Connected")
        }else{
            updateStatusViewWith("Disconnected")
        }
    }

    override fun updateStatusViewWith(status: String) {
        findViewById<TextView>(R.id.statusLabl).text = status
    }

    override fun update(message: String) {

        var text = findViewById<EditText>(R.id.messageHistoryView).text.toString()
        var newText = """
            $text
            $message
            """
        //var newText = text.toString() + "\n" + message +  "\n"
        findViewById<EditText>(R.id.messageHistoryView).setText(newText)
        findViewById<EditText>(R.id.messageHistoryView).setSelection(findViewById<EditText>(R.id.messageHistoryView).text.length)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enable send button and message textfield only after connection
        resetUIWithConnection(false)
    }

    fun connect(view: View){

        if (!(findViewById<EditText>(R.id.ipAddressField).text.isNullOrEmpty() && findViewById<EditText>(R.id.topicField).text.isNullOrEmpty())) {
            var host = "tcp://" + findViewById<EditText>(R.id.ipAddressField).text.toString() + ":1883"
            var topic = findViewById<EditText>(R.id.topicField).text.toString()
            var connectionParams = MQTTConnectionParams("MQTTSample",host,topic,"amy","pate")
            mqttManager = MQTTManager(connectionParams,applicationContext,this)
            mqttManager?.connect()
        }else{
            updateStatusViewWith("Please enter all valid fields")
        }

    }

    fun sendMessage(view: View){

        mqttManager?.publish(findViewById<EditText>(R.id.messageField).text.toString())

        findViewById<EditText>(R.id.messageField).setText("")
    }
}