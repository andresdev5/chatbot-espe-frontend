package ec.edu.espe.chatbot.models

import org.json.JSONObject

data class AssistanteRequestMessage(
    val prompt: String,
    val threadId: String
) {
    fun toJsonObject(): JSONObject {
        val json = JSONObject()
        json.put("prompt", prompt)
        json.put("thread_id", threadId)

        return json
    }
}