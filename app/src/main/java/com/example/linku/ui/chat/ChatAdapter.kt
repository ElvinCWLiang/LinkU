package com.example.linku.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.linku.MainActivity
import com.example.linku.R
import com.example.linku.data.local.FriendModel
import com.example.linku.ui.utils.GlideApp
import kotlinx.android.synthetic.main.adapter_chat.view.*


class ChatAdapter(_fragment: Fragment , _container: ViewGroup?):
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val TAG = "ev_" + javaClass.simpleName
    private var mfriendModel: List<FriendModel> = ArrayList()
    private var fragment = _fragment

    // androidx.recyclerview.widget.RecyclerView.Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_chat, parent, false)
        return ChatViewHolder(this, view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(mfriendModel[position], position)
    }

    // androidx.recyclerview.widget.RecyclerView.Adapter
    override fun getItemCount(): Int {
        return mfriendModel.size
    }

    inner class ChatViewHolder(mChatAdapter: ChatAdapter, itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var pos = 0
        val txv_account = itemView.txv_conversation_local
        val txv_time = itemView.txv_time
        val txv_content = itemView.txv_content
        val img_author = itemView.img_local

        fun bind(friendModel: FriendModel, position: Int) {
            txv_account.text = friendModel.email
            txv_time.text = friendModel.time.toString()
            txv_content.text = friendModel.content
            pos = position
            GlideApp.with(itemView).load(MainActivity.userWithUrikeySet.get(friendModel.email)).placeholder(R.drawable.cat).circleCrop().into(img_author)
            itemView.setOnClickListener(this)
        }


        override fun onClick(v: View?) {
            val bundle = Bundle()
            bundle.putString("email", mfriendModel[pos].email)
            fragment.findNavController().navigate(R.id.navigation_conversation, bundle)
        }
    }

    fun setModelList(friendModel: List<FriendModel>) {
        mfriendModel = friendModel
    }

}