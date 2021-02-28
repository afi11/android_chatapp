package codeafifudin.fatakhul.chatapp.adapter

import android.view.View
import codeafifudin.fatakhul.chatapp.R
import codeafifudin.fatakhul.chatapp.models.LatestMessage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.row_item_last_message.view.*
import org.json.JSONObject

class UserLatestMessage(val latestMessage: LatestMessage) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_row_item_user_name.text = latestMessage.name
        viewHolder.itemView.textView_Message_row_item_last_message.text = latestMessage.message
        viewHolder.itemView.textView_time_row_item_last_message.text = latestMessage.waktu
        if(latestMessage.num_unread_message.toInt() < 1){
            viewHolder.itemView.linearLayoutCount.visibility = View.GONE
        }else{
            viewHolder.itemView.linearLayoutCount.visibility = View.VISIBLE
            viewHolder.itemView.textView_count_unread_message_latest_message.text = latestMessage.num_unread_message
        }
        Picasso.get().load(latestMessage.photo).into(viewHolder.itemView.imageView_row_item_last_message)
    }

    override fun getLayout(): Int {
        return R.layout.row_item_last_message
    }

}