package com.project.linku.data.remote

import android.net.Uri
import android.util.Log
import com.project.linku.data.local.ArticleModel
import com.project.linku.data.local.FriendModel
import com.project.linku.data.local.UserModel
import com.project.linku.ui.utils.Parsefun
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class FireBaseRepository(callBack: IFireOperationCallBack?): IFireBaseApiService {
    private val TAG = "ev_" + javaClass.simpleName
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var mcallBack: IFireOperationCallBack? = null
    private val currentUser: FirebaseUser? = auth.currentUser
    private val ACCOUNTLIST = "accountlist"
    private val FRIENDLIST = "friendlist"
    private val cust_message = "add new friend"
    init {
        mcallBack = callBack
    }

    override fun getUserName(): String? {
        TODO("Not yet implemented")
    }

    /* publish new article in PublishFragment */
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

    /* search account in chatfragment */
    override fun searchAccount(str: String) {
        Log.i(TAG,str)
        val mQuery = database.child(ACCOUNTLIST).child(Parsefun.getInstance().parseEmailasAccount(str))
        mQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    mcallBack?.onSuccess(dataSnapshot)
                } else {
                    mcallBack?.onFail()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                mcallBack?.onFail()
            }
        })
    }

    override fun addFriend(acc: String) {
        currentUser?.email.let {
            if (acc == auth.currentUser!!.email.toString()) return
            send(cust_message, acc, 0)
            Log.i(TAG, cust_message)
        }
    }

    /* send message to another user */
    override fun send(userMessage: String, remote: String, type: Int) {
        val time = Timestamp.now().seconds
        val localaccount = Parsefun.getInstance().parseEmailasAccount(currentUser?.email!!)
        val remoteaccount = Parsefun.getInstance().parseEmailasAccount(remote)
        Log.i(TAG, "localaccount = $localaccount, remoteaccount = $remoteaccount")
        database.child(FRIENDLIST).child(localaccount).child(remoteaccount).push()
            .setValue(FriendModel("", remote, currentUser.email!!, userMessage, time, type))
            .addOnCompleteListener {
                database.child(FRIENDLIST).child(remoteaccount).child(localaccount).push()
                    .setValue(FriendModel("", remote, currentUser.email!!, userMessage, time, type))
                    .addOnCompleteListener {
                        mcallBack?.onSuccess(it)
                    }.addOnFailureListener {
                        mcallBack?.onFail()
                    }
            }

    }

    /* update avatar */
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

    /* send reply in ArticleModel */
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

    /* user sign in */
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


    /* sign up with email and upload a UserModel file to Firebase*/
    override fun signUp(acc: String, pwd: String) {
        auth?.createUserWithEmailAndPassword(acc, pwd)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    database.child(ACCOUNTLIST).child(Parsefun.getInstance().parseEmailasAccount(acc)).setValue(UserModel(acc, "", "Hi, my name  is $acc"))
                    mcallBack?.onSuccess(null)
                }
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

    override fun syncUser(acc: String) {
        //Log.i(TAG, "syncUser = $acc")
        GlobalScope.launch(Dispatchers.IO) {
            val mQuery = database.child(ACCOUNTLIST).child(Parsefun.getInstance().parseEmailasAccount(acc))
            mQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Log.i(TAG, "onSuccess = ${dataSnapshot.value}")
                        mcallBack?.onSuccess(dataSnapshot)
                    } else {
                        //Log.i(TAG, "onFail - syncUser")
                        mcallBack?.onFail()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.i(TAG, "onCancelled - syncUser")
                    mcallBack?.onFail()
                }
            })
        }
    }

    override fun notifyMessage(childEventListener: ChildEventListener) {
        auth.currentUser?.let {
            database.child(FRIENDLIST)
                .child(
                    Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString())
                )
                .addChildEventListener(childEventListener)
        }
    }

    override fun syncConversation(acc: String, childEventListener: ChildEventListener) {
        auth.currentUser?.let {
            database.child(FRIENDLIST)
                .child(Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString()))
                .child(Parsefun.getInstance().parseEmailasAccount(acc))
                .addChildEventListener(childEventListener)
        }
    }

    override fun syncArticleResponse(articleId: String, board: String, childEventListener: ChildEventListener) {
        auth.currentUser?.let {
            database.child(board)
                .addChildEventListener(childEventListener)
        }
    }

    override fun syncFriendList() {
        auth.currentUser?.let {
            Log.i(TAG,"syncFriendList = ${Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString())}")
            database.child(FRIENDLIST)
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

    /* update user Avatar */
    override fun updateAvatar(userModel: UserModel, imagePath: Uri) {
        val refStorage = FirebaseStorage.getInstance().reference.child("avatar/" + Parsefun.getInstance().parseEmailasAccount(auth.currentUser?.email.toString()) + ".jpg")
        Log.i(TAG, userModel.useruri)
        refStorage.putFile(imagePath)
            .addOnSuccessListener { it ->
                it.storage.downloadUrl.addOnSuccessListener {
                    Log.i(TAG,"it = $it")
                    userModel.useruri = it.toString()
                    mcallBack?.onSuccess(userModel)
                    database.child(ACCOUNTLIST).child(Parsefun.getInstance().parseEmailasAccount(FirebaseAuth.getInstance().currentUser?.email.toString())).setValue(userModel)
                }
            }.addOnFailureListener{
                mcallBack?.onFail()
            }
    }

    /* update user introduction */
    override fun updateUserIntroduction(userModel: UserModel) {
        database.child(ACCOUNTLIST).child(Parsefun.getInstance().parseEmailasAccount(FirebaseAuth.getInstance().currentUser?.email.toString())).setValue(userModel)
            .addOnCompleteListener {
                if (it.isSuccessful) mcallBack?.onSuccess(userModel)
            }
    }
}
