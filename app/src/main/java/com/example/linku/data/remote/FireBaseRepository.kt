package com.example.linku.data.remote

import android.content.Context
import android.util.Log
import com.example.linku.data.local.ArticleModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlin.jvm.internal.Intrinsics


class FireBaseRepository(callBack: IFireOperationCallBack?): IFireBaseApiService {
    private val TAG = "ev_" + javaClass.simpleName
    private val auth: FirebaseAuth? = null
    private var database: DatabaseReference? = null
    private var mcallBack: IFireOperationCallBack? = null

    init {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        mcallBack = callBack
        if (this.auth != null) {
            Log.i(TAG, "auth login" + this.auth)
        } else {
            Log.i(TAG, "auth null")
        }
        if (currentUser != null) {
            Log.i(TAG, "user login")
        } else {
            Log.i(TAG, "user null")
        }
    }

    override fun addFriend(str: String?) {
        TODO("Not yet implemented")
    }

    override fun getUserName(): String? {
        TODO("Not yet implemented")
    }

    override fun publishArticle(articleModel: ArticleModel?): ArticleModel? {
        articleModel?.publishTime = Timestamp.now().getSeconds()
        val currentUser = auth!!.currentUser
        articleModel?.publishAuthor = currentUser?.email
        database!!.child(articleModel?.publishBoard!!).push().setValue(articleModel)
            .addOnCompleteListener{
                if (it.isSuccessful) mcallBack?.onSuccess(null)
                else mcallBack?.onFail()
            }
        return articleModel
    }

    override fun searchAccount(str: String?) {
        TODO("Not yet implemented")
    }

    override fun send(str: String?, str2: String?) {
        TODO("Not yet implemented")
    }

    override fun sendReply(str: String?, str2: String?, str3: String?) {
        TODO("Not yet implemented")
    }

    override fun signIn(str: String?, str2: String?) {
        TODO("Not yet implemented")
    }

    override fun signOut() {
        auth?.signOut()
    }

    override fun signUp(acc: String, pwd: String) {
        auth?.createUserWithEmailAndPassword(acc, pwd)
            ?.addOnCompleteListener {
                if (it.isSuccessful) mcallBack?.onSuccess(null)
                else mcallBack?.onFail()
            }
    }

    override fun syncBoard(str: String?): DataSnapshot? {
        TODO("Not yet implemented")
    }

    override fun syncConversation(str: String?, childEventListener: ChildEventListener?) {
        TODO("Not yet implemented")
    }

    override fun syncFriendList(): DataSnapshot? {
        TODO("Not yet implemented")
    }
}