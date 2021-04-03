package com.leoncorp.justclient

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // получаем сохранённые настройки
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (prefs.contains("HOST")){
            findViewById<EditText>(R.id.ipText).setText(prefs.getString("HOST",""))
        }
        if (prefs.contains("PORT")){
            findViewById<EditText>(R.id.portText).setText(prefs.getInt("PORT",0).toString())
        }
    }

    fun buttonClick(view: View){
        // получаем настройки из полей ввода
        try{
            val host: String = findViewById<EditText>(R.id.ipText).text.toString()
            val port: Int = findViewById<EditText>(R.id.portText).text.toString().toInt()
            val message: String = findViewById<EditText>(R.id.messageText).text.toString()
            val outText = findViewById<TextView>(R.id.outText)
            // сохраняем настройки
            val editor = prefs.edit()
            editor.putString("HOST", host).apply()
            editor.putInt("PORT", port).apply()
            // запускаем поток отправки/получения сообщения
            Thread(ClientThread(host,port,message,outText,this)).start()
        }
        catch (e: NumberFormatException){
            val toast = Toast.makeText(applicationContext, "Ошибка. ${e.message}", Toast.LENGTH_SHORT)
            toast.show()
            Log.d("Ошибка",e.toString())
        }

    }

    internal class ClientThread(
            private val host: String,
            private val port: Int,
            private val message: String,
            private val outText: TextView,
            private val MainActivity: AppCompatActivity) : Runnable {
        override fun run() {
            try {
                val socket = Socket(host, port)
                socket.soTimeout = 10000
                val toServer = PrintWriter(socket.getOutputStream(), true)
                // Отправка данных на сервер
                if (message == "") toServer.println("Hello from TEST\n") else toServer.println(message)
                Log.d("Отправлено", message)

                // Ответ сервера
                val fromServer = BufferedReader(InputStreamReader(socket.getInputStream()))

                val line: String = fromServer.readLine()
                MainActivity.runOnUiThread {
                    outText.text = line
                }
                Log.d("Получено",line)
                socket.close()
            } catch (e: SocketException) {
                outToast(e.message)
                Log.d("Ошибка",e.toString())
                e.printStackTrace()
            } catch (e: UnknownHostException) {
                outToast(e.message)
                Log.d("Ошибка",e.toString())
                e.printStackTrace()
            } catch (e: IOException) {
                outToast(e.message)
                Log.d("Ошибка",e.toString())
                e.printStackTrace()
            }
        }
        
        //вывод сообщения
        private fun outToast(message: String?){
            if (message != null)
                MainActivity.runOnUiThread {
                    val toast = Toast.makeText(MainActivity.applicationContext,
                        "Ошибка. $message", Toast.LENGTH_LONG)
                    toast.show()
                }
        }

    }

}




