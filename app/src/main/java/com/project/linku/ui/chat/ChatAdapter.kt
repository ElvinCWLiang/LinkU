package com.project.linku.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.linku.MainActivity
import com.project.linku.R
import com.project.linku.data.local.FriendModel
import com.project.linku.databinding.AdapterChatBinding
import com.project.linku.ui.utils.GlideApp
import com.project.linku.ui.utils.Parsefun
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatAdapter(private val navigate: (Bundle) -> Unit):
    ListAdapter<FriendModel, ChatAdapter.ChatViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            AdapterChatBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatViewHolder(private val binding: AdapterChatBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        fun bind(friendModel: FriendModel) {
            binding.txvBoard.text = friendModel.email
            binding.txvTitle.text = Parsefun.getInstance().parseSecondsToDate(friendModel.time)
            binding.txvContent.text = friendModel.content
            GlideApp.with(itemView).load(MainActivity.userkeySet[friendModel.email]?.useruri).placeholder(R.drawable.cat).circleCrop().into(binding.imgLocal)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val bundle = Bundle()
            v?.resources?.let {
                bundle.putString(it.getString(R.string.email), getItem(adapterPosition).email)
                navigate.invoke(bundle)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<FriendModel>() {
        override fun areItemsTheSame(
            oldItem: FriendModel,
            newItem: FriendModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FriendModel,
            newItem: FriendModel
        ): Boolean {
            return oldItem.email == newItem.email &&
                    oldItem.emailfrom == newItem.emailfrom &&
                    oldItem.content == newItem.content &&
                    oldItem.time == newItem.time &&
                    oldItem.type == newItem.type
        }
    }
}