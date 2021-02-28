package codeafifudin.fatakhul.chatapp.adapter

import android.view.View
import codeafifudin.fatakhul.chatapp.R
import codeafifudin.fatakhul.chatapp.models.ItemMessage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.row_item_from_message.view.*

class ItemMessageFromUser(val itemMessage: ItemMessage) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_row_item_from_message.text = itemMessage.messages
        viewHolder.itemView.textView_row_date_from_message.text = itemMessage.date_message
        if(itemMessage.image != ""){
            Picasso.get().load(itemMessage.image).into(viewHolder.itemView.imageView_row_from_messages)
        }else{
            viewHolder.itemView.imageView_row_from_messages.visibility = View.GONE
        }
    }

    override fun getLayout(): Int {
        return R.layout.row_item_from_message
    }

}