package com.example.smartfountain

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import protocols.UIUpdater
import com.example.smartfountain.MQTTManager

class MainActivity : Activity(), UIUpdater {
    private val username = "amy"
    private val password = "pate"
    private val host = "tcp://192.168.0.21:1883"
    var topic:String = ""
    var modeTopic:String = ""
    var statusTopic:String = ""
    var fountainId:String = ""
    var mqttManager:MQTTManager? = null


    // Interface methods
    override fun resetUIWithConnection(status: Boolean) {
        findViewById<Button>(R.id.connectBtnWF).isEnabled      = !status
        findViewById<Button>(R.id.fountainToggleBlock).isEnabled         = status
        findViewById<Button>(R.id.fountainToggleBtn).isEnabled         = status
        findViewById<TextView>(R.id.statusLevel).text = "-"
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
        // update water level
        findViewById<TextView>(R.id.statusLevel).text = message
        if (message == "MAX" || message == "LOW"){
            findViewById<Button>(R.id.fountainToggleBlock).isEnabled         = false
            findViewById<Button>(R.id.fountainToggleBtn).isEnabled         = false
            findViewById<ToggleButton>(R.id.fountainToggleBtn).setChecked(false)
            findViewById<ToggleButton>(R.id.fountainToggleBlock).setChecked(true)
        }
        else if (message == "OK"){
            findViewById<Button>(R.id.fountainToggleBlock).isEnabled         = true
            findViewById<Button>(R.id.fountainToggleBtn).isEnabled         = true
            findViewById<ToggleButton>(R.id.fountainToggleBlock).setChecked(false)
        }


//        var text = findViewById<EditText>(R.id.messageHistoryView).text.toString()
//        var newText = """
//            $text
//            $message
//            """
//        //var newText = text.toString() + "\n" + message +  "\n"
//        findViewById<EditText>(R.id.messageHistoryView).setText(newText)
//        findViewById<EditText>(R.id.messageHistoryView).setSelection(findViewById<EditText>(R.id.messageHistoryView).text.length)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enable send button and message textfield only after connection
        resetUIWithConnection(false)

    }

    fun connect(view: View){

        if (!findViewById<EditText>(R.id.fountainField).text.isNullOrEmpty()) {
            fountainId = findViewById<EditText>(R.id.fountainField).text.toString()
            topic = "/home/$fountainId"
            statusTopic = "$topic/status"
            modeTopic = "$topic/mode"
            val connectionParams = MQTTConnectionParams("",host,statusTopic,username,password)
            mqttManager = MQTTManager(connectionParams,applicationContext,this)
            mqttManager?.connect()
        }else{
            updateStatusViewWith("Please enter all valid fields")
        }

    }

//    fun sendMessage(view: View){
//        mqttManager?.publish(modeTopic, findViewById<EditText>(R.id.messageField).text.toString())
//
//        findViewById<EditText>(R.id.messageField).setText("")
//    }

    fun toggleFountain(view: View){
        var toggleButtonState = findViewById<ToggleButton>(R.id.fountainToggleBtn).isChecked()

        if (toggleButtonState){
            mqttManager?.publish(modeTopic, "1")
        }
        else{
            mqttManager?.publish(modeTopic, "-1")
        }
    }

    fun toggleFountainBlock(view: View){
        var toggleButton = findViewById<ToggleButton>(R.id.fountainToggleBlock)

        if (toggleButton.isChecked()){
            mqttManager?.publish(modeTopic, "2")
            toggleButton.setBackgroundColor(Color.parseColor("#E81F1F"))

            if(findViewById<ToggleButton>(R.id.fountainToggleBtn).isChecked()){
                findViewById<ToggleButton>(R.id.fountainToggleBtn).setChecked(false)
            }
            findViewById<Button>(R.id.fountainToggleBtn).isEnabled         = false
        }
        else{
            mqttManager?.publish(modeTopic, "-1")
            toggleButton.setBackgroundColor(Color.parseColor("#FF33E81F"))
            findViewById<Button>(R.id.fountainToggleBtn).isEnabled         = true

        }
    }
}