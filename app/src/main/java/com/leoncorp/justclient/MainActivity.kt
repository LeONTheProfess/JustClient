package com.leoncorp.justclient

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.AsyncTask
import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextClock
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (prefs.contains("HOST")){
            findViewById<EditText>(R.id.ipText).setText(prefs.getString("HOST",""))
        }
        if (prefs.contains("PORT")){
            findViewById<EditText>(R.id.portText).setText(prefs.getInt("PORT",0).toString())
        }
    }

    fun buttonClick(view: View){

        val host: String = findViewById<EditText>(R.id.ipText).text.toString()
        val port: Int = findViewById<EditText>(R.id.portText).text.toString().toInt()
        val message: String = findViewById<EditText>(R.id.messageText).text.toString()
        val outText = findViewById<TextView>(R.id.outText)
        val editor = prefs.edit()
        editor.putString("HOST", host).apply()
        editor.putInt("PORT", port).apply()
        Thread(ClientThread(host,port,message,outText,this)).start()

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
                //text1.append("Отправлено на сервер\n$iText\n")
                // Ответ сервера
                val fromServer: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))

                val line: String = fromServer.readLine()
                MainActivity.runOnUiThread {
                    outText.text = line
                }
                Log.d("Получено",line)
                //outText.text = line
                //text1.append("Получено с сервера\n")
                //text1.append("""$line""".trimIndent())
                socket.close()
            } catch (e: SocketException) {
                Log.d("Ошибка",e.toString())
                e.printStackTrace()
            } catch (e: UnknownHostException) {
                Log.d("Ошибка",e.toString())
                e.printStackTrace()
            } catch (e: IOException) {
                Log.d("Ошибка",e.toString())
                e.printStackTrace()
            }
        }

    }

}




