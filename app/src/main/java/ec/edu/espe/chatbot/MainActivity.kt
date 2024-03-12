package ec.edu.espe.chatbot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import ec.edu.espe.chatbot.adapters.MessageListAdapter
import ec.edu.espe.chatbot.models.AssistanteRequestMessage
import ec.edu.espe.chatbot.models.ChatMessage
import ec.edu.espe.chatbot.models.MessageAuthorType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageTextView: EditText
    private lateinit var sendMessageButton: Button
    private lateinit var assistantThreadId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageRecyclerView = findViewById(R.id.rv_chat)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = MessageListAdapter(this, messageList)

        messageTextView = findViewById(R.id.et_chat_message)

        sendMessageButton = findViewById(R.id.btn_send_message)
        sendMessageButton.setOnClickListener {
            sendMessage()
        }

        sendBotMessage("Hola, soy tu asistente virtual para DCCO ESPE. ¿En qué puedo ayudarte?")

        messageTextView.isEnabled = false
        sendMessageButton.isEnabled = false

        createAssistantThread()
    }

    private fun sendMessage() {
        val message = messageTextView.text.toString().trim()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm a")

        if (message.isNotEmpty()) {
            val chatMessage = ChatMessage()
            chatMessage.message = message
            chatMessage.authorType = MessageAuthorType.USER
            chatMessage.time = LocalDateTime.now().format(timeFormatter)

            messageList.add(chatMessage)
            messageRecyclerView.adapter?.notifyItemInserted(messageList.size - 1)
            messageRecyclerView.scrollToPosition(messageList.size - 1)
            messageTextView.setText("")

            hideKeyboard()

            sendAssistantMessage(message)
        } else {
            messageTextView.requestFocus()
        }
    }

    private fun hideKeyboard() {
        messageTextView.clearFocus()

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(messageTextView.windowToken, 0)
    }

    private fun createAssistantThread() {
        val request = JsonObjectRequest(
            Request.Method.GET,
            "http://200.105.253.153:5000/new",
            null,
            { response ->
                val threadId = response.getString("thread_id")
                assistantThreadId = threadId
                messageTextView.isEnabled = true
                sendMessageButton.isEnabled = true
            },
            { error ->
                error.printStackTrace()
            }
        )

        // increase the timeout to 30 seconds
        request.retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun sendAssistantMessage(message: String) {
        val request = JsonObjectRequest(
            Request.Method.POST,
            "http://200.105.253.153:5000/message",
            AssistanteRequestMessage(message, assistantThreadId).toJsonObject(),
            { response ->
                val botMessage = response.getString("result")
                sendBotMessage(botMessage)
            },
            { error ->
                error.printStackTrace()
                sendBotMessage("Lo siento, ocurrio un error al solicitar la respuesta. Por favor, intenta nuevamente.")
            }
        )

        // increase the timeout to 30 seconds

        request.retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun sendBotMessage(message: String) {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm a")
        val botMessage = ChatMessage()
        botMessage.message = message
        botMessage.time = LocalDateTime.now().format(timeFormatter)
        botMessage.authorType = MessageAuthorType.BOT

        messageList.add(botMessage)
        messageRecyclerView.adapter?.notifyItemInserted(messageList.size - 1)
        messageRecyclerView.scrollToPosition(messageList.size - 1)
    }
}