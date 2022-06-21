package com.project.linku.ui.chat

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.data.local.FriendModel
import com.project.linku.ui.utils.GlideApp
import com.project.linku.ui.utils.Parsefun
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.adapter_conversation.view.*

class ConversationAdapter(_fragment: ConversationFragment, _acc: String):
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private val TAG = "ev_" + javaClass.simpleName
    private var mfriendmodel: List<FriendModel> = ArrayList()
    private val fragment = _fragment
    private var channel : String? = null
    private var acc : String? = _acc

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
        val txv_local = itemView.txv_board
        val txv_remote = itemView.txv_conversation_remote
        val img_author_local = itemView.img_author_local
        val img_author_remote = itemView.img_author_remote
        val img_local = itemView.img_local
        val img_remote = itemView.img_remote
        val txv_local_time = itemView.txv_local_time
        val txv_remote_time = itemView.txv_remote_time
        val img_txv_local = itemView.img_txv_local
        val img_txv_remote = itemView.img_txv_remote

        fun bind(friendModel: FriendModel, position: Int) {
            txv_local.text = friendModel.content
            txv_remote.text = friendModel.content
            txv_local_time.text = Parsefun.getInstance().parseSecondsToDate(friendModel.time)
            txv_remote_time.text = Parsefun.getInstance().parseSecondsToDate(friendModel.time)
            channel = friendModel.content
            img_txv_remote.button.setOnClickListener(ListeneraddCall(friendModel.type))
            img_txv_local.button.setOnClickListener(ListeneraddCall(friendModel.type))

            pos = position
            val remoteaccount = friendModel.emailfrom
            val localaccout = FirebaseAuth.getInstance().currentUser?.email
            //Log.i(TAG, "currentUser = $localaccout, remote = $remoteaccount, $friendModel")

            if (remoteaccount == localaccout) {
                img_author_remote.visibility = INVISIBLE
                img_author_local.visibility = VISIBLE
                GlideApp.with(itemView).load(MainActivity.userkeySet.get(localaccout)?.useruri).placeholder(R.drawable.cat).circleCrop().into(img_author_local)
                /* type 0 = text, type 1 = image, type 2 = voice call, type 3 = video call */
                when (friendModel.type) {
                    0 -> {
                        txv_remote.visibility = INVISIBLE
                        txv_local.visibility = VISIBLE
                        txv_local_time.visibility = VISIBLE
                        txv_remote_time.visibility = View.GONE
                        img_local.visibility = View.GONE
                        img_remote.visibility = View.GONE
                        img_txv_remote.visibility = View.GONE
                        img_txv_local.visibility = View.GONE
                    }
                    1 -> {
                        img_local.visibility = VISIBLE
                        img_remote.visibility = View.GONE
                        txv_remote.visibility = View.GONE
                        txv_local.visibility = View.GONE
                        txv_remote_time.visibility = View.GONE
                        txv_local_time.visibility = VISIBLE
                        img_txv_remote.visibility = View.GONE
                        img_txv_local.visibility = View.GONE
                        GlideApp.with(itemView).load(Uri.parse(friendModel.content)).into(img_local)
                    }
                    2 -> {
                        txv_remote.visibility = View.GONE
                        txv_local.visibility = View.GONE
                        txv_local_time.visibility = VISIBLE
                        txv_remote_time.visibility = View.GONE
                        img_local.visibility = View.GONE
                        img_remote.visibility = View.GONE
                        img_txv_remote.visibility = View.GONE
                        img_txv_local.visibility = VISIBLE
                    }

                    3 -> {
                        txv_remote.visibility = View.GONE
                        txv_local.visibility = View.GONE
                        txv_local_time.visibility = VISIBLE
                        txv_remote_time.visibility = View.GONE
                        img_local.visibility = View.GONE
                        img_remote.visibility = View.GONE
                        img_txv_remote.visibility = View.GONE
                        img_txv_local.visibility = VISIBLE
                    }
                }
            } else {
                img_author_remote.visibility = VISIBLE
                img_author_local.visibility = INVISIBLE
                GlideApp.with(itemView).load(MainActivity.userkeySet.get(remoteaccount)?.useruri).placeholder(R.drawable.cat).circleCrop().into(img_author_remote)

                /* type 0 = text, type 1 = image, type 2 = voice call, type 3 = video call */
                when (friendModel.type) {
                    0 -> {
                        txv_remote.visibility = VISIBLE
                        txv_local.visibility = INVISIBLE
                        img_local.visibility = View.GONE
                        img_remote.visibility = View.GONE
                        txv_remote_time.visibility = VISIBLE
                        txv_local_time.visibility = View.GONE
                        img_txv_remote.visibility = View.GONE
                        img_txv_local.visibility = View.GONE
                    }
                    1 -> {
                        img_remote.visibility = VISIBLE
                        img_local.visibility = View.GONE
                        txv_remote.visibility = View.GONE
                        txv_local.visibility = View.GONE
                        txv_remote_time.visibility = VISIBLE
                        txv_local_time.visibility = View.GONE
                        img_txv_remote.visibility = View.GONE
                        img_txv_local.visibility = View.GONE
                        GlideApp.with(itemView).load(Uri.parse(friendModel.content)).into(img_remote)
                    }
                    2 -> {
                        txv_remote.visibility = View.GONE
                        txv_local.visibility = View.GONE
                        img_local.visibility = View.GONE
                        img_remote.visibility = View.GONE
                        txv_remote_time.visibility = VISIBLE
                        txv_local_time.visibility = View.GONE
                        img_txv_remote.visibility = VISIBLE
                        img_txv_local.visibility = View.GONE
                    }

                    3 -> {
                        txv_remote.visibility = View.GONE
                        txv_local.visibility = View.GONE
                        img_local.visibility = View.GONE
                        img_remote.visibility = View.GONE
                        txv_remote_time.visibility = VISIBLE
                        txv_local_time.visibility = View.GONE
                        img_txv_remote.visibility = VISIBLE
                        img_txv_local.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun setModelList(friendmodel: List<FriendModel>) {
        mfriendmodel = friendmodel
    }

    inner class ListeneraddCall(_type: Int) : View.OnClickListener {
        private val type = _type
        override fun onClick(v: View?) {
            val bundle = Bundle()
            bundle.putString("channel", channel)
            bundle.putString("email", acc)
            Log.i(TAG, "Type = $type, Channel = $channel")
            when (type) {
                2 -> fragment.findNavController().navigate(R.id.action_navigation_conversation_to_navigation_voicecall, bundle)
                3 -> fragment.findNavController().navigate(R.id.action_navigation_conversation_to_navigation_voicecall, bundle)
            }
        }
    }
}