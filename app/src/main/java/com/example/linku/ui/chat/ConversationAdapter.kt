package com.example.linku.ui.chat

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.linku.MainActivity
import com.example.linku.R
import com.example.linku.data.local.FriendModel
import com.example.linku.ui.utils.GlideApp
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
        val img_author_local = itemView.img_author_local
        val img_author_remote = itemView.img_author_remote
        val localaccout = FirebaseAuth.getInstance().currentUser?.email
        val img_local = itemView.img_local
        val img_remote = itemView.img_remote
        val txv_local_time = itemView.txv_local_time
        val txv_remote_time = itemView.txv_remote_time

        fun bind(friendModel: FriendModel, position: Int) {
            txv_local.text = friendModel.content
            txv_remote.text = friendModel.content
            txv_local_time.text = Parsefun.getInstance().parseSecondsToDate(friendModel.time)
            txv_remote_time.text = Parsefun.getInstance().parseSecondsToDate(friendModel.time)
            pos = position
            val remoteaccount = Parsefun.getInstance().parseAccountasEmail(friendModel.email)
            Log.i(TAG, "local = $localaccout, remote = $remoteaccount, content = ${friendModel.content}, type = ${friendModel.type}, pos = $pos")

            if (remoteaccount == localaccout) {
                Log.i(TAG,"equals")
                img_author_remote.visibility = INVISIBLE
                img_author_local.visibility = VISIBLE
                if (friendModel.type == 0) {
                    txv_remote.visibility = INVISIBLE
                    txv_local.visibility = VISIBLE
                    txv_local_time.visibility = VISIBLE
                    txv_remote_time.visibility = View.GONE
                    img_local.visibility = View.GONE
                    img_remote.visibility = View.GONE
                } else if (friendModel.type == 1) {
                    img_local.visibility = VISIBLE
                    img_remote.visibility = View.GONE
                    txv_remote.visibility = View.GONE
                    txv_local.visibility = View.GONE
                    txv_remote_time.visibility = VISIBLE
                    txv_local_time.visibility = View.GONE
                    GlideApp.with(itemView).load(Uri.parse(friendModel.content)).into(img_local)
                }
            } else {
                Log.i(TAG,"!equals")
                img_author_remote.visibility = VISIBLE
                img_author_local.visibility = INVISIBLE
                if (friendModel.type == 0) {
                    txv_remote.visibility = VISIBLE
                    txv_local.visibility = INVISIBLE
                    img_local.visibility = View.GONE
                    img_remote.visibility = View.GONE
                    txv_remote_time.visibility = VISIBLE
                    txv_local_time.visibility = View.GONE
                } else if (friendModel.type == 1) {
                    img_remote.visibility = VISIBLE
                    img_local.visibility = View.GONE
                    txv_remote.visibility = View.GONE
                    txv_local.visibility = View.GONE
                    txv_local_time.visibility = VISIBLE
                    txv_remote_time.visibility = View.GONE
                    GlideApp.with(itemView).load(Uri.parse(friendModel.content)).into(img_remote)
                }
            }
        }
    }

    fun setModelList(friendmodel: List<FriendModel>) {
        mfriendmodel = friendmodel
    }

}