package ec.edu.espe.chatbot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.espe.chatbot.R
import ec.edu.espe.chatbot.models.ChatMessage
import ec.edu.espe.chatbot.models.MessageAuthorType


class MessageListAdapter(private val context: Context, private val messageList: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = when(viewType) {
            MessageAuthorType.USER.ordinal -> LayoutInflater.from(context).inflate(R.layout.user_chat_item, parent, false)
            MessageAuthorType.BOT.ordinal -> LayoutInflater.from(context).inflate(R.layout.bot_chat_item, parent, false)
            else -> LayoutInflater.from(context).inflate(R.layout.bot_chat_item, parent, false)
        }

        return when(viewType) {
            MessageAuthorType.USER.ordinal -> UserMessageViewHolder(view)
            MessageAuthorType.BOT.ordinal -> BotMessageViewHolder(view)
            else -> BotMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when(holder.itemViewType) {
            MessageAuthorType.USER.ordinal -> (holder as UserMessageViewHolder).bind(message)
            else -> (holder as BotMessageViewHolder).bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return messageList[position].authorType!!.ordinal
    }

    inner class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.tv_user_message_text)
        private val timeTextView: TextView = itemView.findViewById(R.id.tv_user_message_time)

        fun bind(message: ChatMessage) {
            messageTextView.text = message.message
            timeTextView.text = message.time
        }
    }

    inner class BotMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.tv_bot_message_text)
        private val timeTextView: TextView = itemView.findViewById(R.id.tv_bot_message_time)
        private val botImageView: ImageView = itemView.findViewById(R.id.iv_bot_avatar)

        fun bind(message: ChatMessage) {
            messageTextView.text = message.message
            timeTextView.text = message.time
            botImageView.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}
