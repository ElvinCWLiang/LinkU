package com.example.linku.data.remote

import android.net.Uri
import android.util.Log
import com.example.linku.data.local.ArticleModel
import com.example.linku.data.local.FriendModel
import com.example.linku.ui.utils.Parsefun
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class FireBaseRepository(callBack: IFireOperationCallBack?): IFireBaseApiService {
    private val TAG = "ev_" + javaClass.simpleName
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var mcallBack: IFireOperationCallBack? = null
    private val currentUser: FirebaseUser? = auth.currentUser

    init {

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

    override fun getUserName(): String? {
        TODO("Not yet implemented")
    }

    override fun publishArticle(articleModel: ArticleModel) {
        articleModel.publishTime = Timestamp.now().seconds
        currentUser?.let {
            Log.i(TAG,"board = ${articleModel.publishBoard}")
            articleModel.publishAuthor = currentUser?.email
            database.child(articleModel.publishBoard).push().setValue(articleModel)
                .addOnCompleteListener{
                    if (it.isSuccessful) mcallBack?.onSuccess(null)
                    else mcallBack?.onFail()
                }
        }

    }

    override fun searchAccount(str: String) {
        Log.i(TAG,str)
        val mQuery = database.child("accountlist").child(Parsefun.getInstance().parseEmailasAccount(str))
        mQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "onSuccess = $str")
                    mcallBack?.onSuccess(null)
                } else {
                    Log.i(TAG, "onFail = $str")
                    mcallBack?.onFail()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                mcallBack?.onFail()
            }
        })
    }

    override fun send(userMessage: String, acc: String, type: Int) {
        val time = Timestamp.now().seconds
        val localaccount = Parsefun.getInstance().parseEmailasAccount(currentUser?.email!!)
        val remoteaccount = Parsefun.getInstance().parseEmailasAccount(acc)
        database.child("friendlist").child(localaccount).child(remoteaccount).push()
            .setValue(FriendModel("", localaccount, userMessage, time, type))
            /*.addOnCompleteListener{

            }*/
        database.child("friendlist").child(remoteaccount).child(localaccount).push()
            .setValue(FriendModel("", localaccount, userMessage, time, 0))
            /*.addOnCompleteListener {

            }*/
    }

    override fun send(imagePath: Uri) {
        val refStorage = FirebaseStorage.getInstance().reference.child("images/" + UUID.randomUUID().toString() + ".jpg")
        Log.i(TAG, imagePath.toString())
        refStorage.putFile(imagePath)
            .addOnSuccessListener { it ->
                it.storage.downloadUrl.addOnSuccessListener {
                    Log.i(TAG,"it = $it")
                    mcallBack?.onSuccess(it)
                }
            }.addOnFailureListener{
                mcallBack?.onFail()
            }
    }

    override fun sendReply(userReply: String, articleId: String, board: String) {
        val seconds = Timestamp.now().seconds
        val currentUser = auth.currentUser
        val articleModel = ArticleModel(
            "", seconds, board,
            currentUser?.email, "", userReply, articleId
        )
        database.child(articleModel.publishBoard).push().setValue(articleModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    mcallBack?.onSuccess(null)
                } else {
                    mcallBack?.onFail()
                }
            }
    }

    override fun signIn(acc: String, pwd: String) {
        Log.i(TAG,"sign in with acc = $acc, pwd = $pwd")
        auth?.signInWithEmailAndPassword(acc, pwd)
            .addOnCompleteListener{
                if (it.isSuccessful) mcallBack?.onSuccess(null)
                else mcallBack?.onFail()
            }
    }

    override fun getFirebaseAuth(): FirebaseAuth? {
        return auth
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

    override fun syncBoard(str: String) {
        database.child(str).get().addOnSuccessListener {
            mcallBack?.onSuccess(it)
        }.addOnFailureListener{
            mcallBack?.onFail()
        }
    }

    override fun syncConversation(acc: String, childEventListener: ChildEventListener) {
        auth.currentUser?.let {
            database.child("friendlist")
                .child(Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString()))
                .child(Parsefun.getInstance().parseEmailasAccount(acc))
                .addChildEventListener(childEventListener)
        }
    }

    override fun addFriend(acc: String) {
        currentUser?.email.let {
            Log.i(TAG, "local = ${Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString())}, remote = ${Parsefun.getInstance().parseEmailasAccount(acc)}" )
            val local = Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString())
            val remote = Parsefun.getInstance().parseEmailasAccount(acc)
            if (local == remote) return
            database.child("friendlist")
                .child(local)
                .child(remote)
                .push().setValue(FriendModel("", Parsefun.getInstance().parseEmailasAccount(acc), "add new friend", Timestamp.now().seconds, 0))
                .addOnCompleteListener {
                    if (it.isSuccessful) mcallBack?.onSuccess(null)
                    else mcallBack?.onFail()
                }
        }
    }

    override fun syncFriendList() {
        auth.currentUser?.let {
            Log.i(TAG,"syncFriendList = ${Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString())}")
            database.child("friendlist")
                .child(Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString()))
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        mcallBack?.onSuccess(it.result)
                    } else {
                        Log.i(TAG,"onFail")
                        mcallBack?.onFail()
                    }
                }
        }
    }

    override fun updateAvatar(imagePath: Uri) {
        val refStorage = FirebaseStorage.getInstance().reference.child("avatar/" + Parsefun.getInstance().parseEmailasAccount(auth.currentUser?.email.toString()) + ".jpg")
        Log.i(TAG, imagePath.toString())
        refStorage.putFile(imagePath)
            .addOnSuccessListener { it ->
                it.storage.downloadUrl.addOnSuccessListener {
                    Log.i(TAG,"it = $it")
                    mcallBack?.onSuccess(it)
                }
            }.addOnFailureListener{
                mcallBack?.onFail()
            }
    }
}
