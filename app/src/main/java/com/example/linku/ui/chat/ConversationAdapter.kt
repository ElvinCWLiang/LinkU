package com.example.linku.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.linku.R
import com.example.linku.data.local.FriendModel
import com.example.linku.ui.utils.Parsefun
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.adapter_conversation.view.*

class ConversationAdapter(_fragment: Fragment, _container: ViewGroup?):
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private val TAG = "ev_" + javaClass.simpleName
    private var mfriendmodel: List<FriendModel> = ArrayList()

    // androidx.recyclerview.widget.RecyclerView.Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_conversation, parent, false)
        return ConversationViewHolder(this, view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(mfriendmodel[position], position)
    }

    override fun getItemCount(): Int {
        return mfriendmodel.size
    }

    inner class ConversationViewHolder(mConversationAdapter: ConversationAdapter, itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pos = 0
        val txv_local = itemView.txv_conversation_local
        val txv_remote = itemView.txv_conversation_remote
        val img_local = itemView.img_local
        val img_remote = itemView.img_remote
        val localaccout = FirebaseAuth.getInstance().currentUser?.email

        fun bind(friendModel: FriendModel, position: Int) {
            txv_local.text = friendModel.content
            txv_remote.text = friendModel.content
            pos = position
            val remoteaccount = Parsefun.getInstance().parseAccountasEmail(friendModel.email)
            Log.i(TAG, "local = $localaccout, remote = $remoteaccount, content = ${friendModel.content}, pos = $pos")

            if (remoteaccount == localaccout) {
                Log.i(TAG,"equals")
                txv_remote.visibility = INVISIBLE
                img_remote.visibility = INVISIBLE
                txv_local.visibility = VISIBLE
                img_local.visibility = VISIBLE
            } else {
                Log.i(TAG,"!equals")
                txv_remote.visibility = VISIBLE
                img_remote.visibility = VISIBLE
                txv_local.visibility = INVISIBLE
                img_local.visibility = INVISIBLE
            }
        }
    }

    fun setModelList(friendmodel: List<FriendModel>) {
        mfriendmodel = friendmodel
    }

}