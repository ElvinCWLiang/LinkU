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
import kotlinx.coroutines.withContext
import java.util.*

class FireBaseRepository(private val callBack: IFireOperationCallBack?): IFireBaseApiService {
    private val tag = "ev_" + javaClass.simpleName
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val currentUser: FirebaseUser? = auth.currentUser

    override fun getUserName(): String? {
        TODO("Not yet implemented")
    }

    /* publish new article in PublishFragment */
    override fun publishArticle(articleModel: ArticleModel) {
        articleModel.publishTime = Timestamp.now().seconds
        currentUser?.let {
            Log.i(tag,"board = ${articleModel.publishBoard}")
            articleModel.publishAuthor = currentUser.email
            database.child(articleModel.publishBoard).push().setValue(articleModel)
                .addOnCompleteListener{
                    if (it.isSuccessful) this.callBack?.onSuccess(null)
                    else this.callBack?.onFail()
                }
        }
    }

    /* search account in ChatFragment */
    override fun searchAccount(str: String) {
        Log.i(tag,str)
        val mQuery = database.child(ACCOUNT_LIST).child(Parsefun.getInstance().parseEmailasAccount(str))
        mQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    this@FireBaseRepository.callBack?.onSuccess(dataSnapshot)
                } else {
                    this@FireBaseRepository.callBack?.onFail()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                this@FireBaseRepository.callBack?.onFail()
            }
        })
    }

    override fun addFriend(acc: String) {
        currentUser?.email.let {
            if (acc == auth.currentUser!!.email.toString()) return
            send(CUSTOM_MESSAGE, acc, 0)
            Log.i(tag, CUSTOM_MESSAGE)
        }
    }

    /* send message to another user */
    override fun send(userMessage: String, account: String, type: Int) {
        val time = Timestamp.now().seconds
        val localAccount = Parsefun.getInstance().parseEmailasAccount(currentUser?.email!!)
        val remoteAccount = Parsefun.getInstance().parseEmailasAccount(account)
        database.child(FRIEND_LIST).child(localAccount).child(remoteAccount).push()
            .setValue(FriendModel("", account, currentUser.email!!, userMessage, time, type))
            .addOnCompleteListener {
                database.child(FRIEND_LIST).child(remoteAccount).child(localAccount).push()
                    .setValue(FriendModel("", account, currentUser.email!!, userMessage, time, type))
                    .addOnCompleteListener {
                        this.callBack?.onSuccess(it)
                    }.addOnFailureListener {
                        this.callBack?.onFail()
                    }
            }

    }

    /* update avatar and send image */
    override fun send(imagePath: Uri) {
        val refStorage = FirebaseStorage.getInstance().reference.child("images/" + UUID.randomUUID().toString() + ".jpg")
        Log.i(tag, imagePath.toString())
        refStorage.putFile(imagePath)
            .addOnSuccessListener { it ->
                it.storage.downloadUrl.addOnSuccessListener {
                    Log.i(tag,"it = $it")
                    this.callBack?.onSuccess(it)
                }
            }.addOnFailureListener{
                this.callBack?.onFail()
            }
    }

    /* update avatar and send image */
    override fun sendImageWithArticle(imagePath: Uri) {
        val refStorage = FirebaseStorage.getInstance().reference.child("article/" + Parsefun.getInstance().parseEmailasAccount(auth.currentUser?.email.toString()) + ".jpg")
        Log.i(tag, imagePath.toString())
        refStorage.putFile(imagePath)
            .addOnSuccessListener { it ->
                it.storage.downloadUrl.addOnSuccessListener {
                    Log.i(tag,"it = $it")
                    this.callBack?.onSuccess(it)
                }
            }.addOnFailureListener{
                this.callBack?.onFail()
            }
    }

    /* send reply in ArticleModel */
    override fun sendReply(userReply: String, articleId: String, board: String) {
        val seconds = Timestamp.now().seconds
        val currentUser = auth.currentUser
        val articleModel = ArticleModel(
            "", seconds, board,
            currentUser?.email, "", userReply, "", articleId
        )
        database.child(articleModel.publishBoard).push().setValue(articleModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    this.callBack?.onSuccess(null)
                } else {
                    this.callBack?.onFail()
                }
            }
    }

    /* user sign in */
    override fun signIn(acc: String, pwd: String) {
        Log.i(tag,"sign in with acc = $acc, pwd = $pwd")
        auth.signInWithEmailAndPassword(acc, pwd)
            .addOnCompleteListener{
                if (it.isSuccessful) this.callBack?.onSuccess(null)
                else this.callBack?.onFail()
            }
    }

    override fun getFirebaseAuth(): FirebaseAuth {
        return auth
    }

    override fun signOut() {
        auth.signOut()
    }


    /* sign up with email and upload a UserModel file to Firebase*/
    override fun signUp(acc: String, pwd: String) {
        auth.createUserWithEmailAndPassword(acc, pwd)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    database.child(ACCOUNT_LIST).child(Parsefun.getInstance().parseEmailasAccount(acc)).setValue(UserModel(acc, "", "Hi, my name  is $acc"))
                    this.callBack?.onSuccess(null)
                } else this.callBack?.onFail()
            }
    }

    override fun syncBoard(board: String) {
        database.child(board).get().addOnSuccessListener {
            this.callBack?.onSuccess(it)
        }.addOnFailureListener{
            this.callBack?.onFail()
        }
    }

    override suspend fun syncUser(acc: String) {
        //Log.i(TAG, "syncUser = $acc")
        withContext(Dispatchers.IO) {
            val mQuery = database.child(ACCOUNT_LIST).child(Parsefun.getInstance().parseEmailasAccount(acc))
            mQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Log.i(TAG, "onSuccess = ${dataSnapshot.value}")
                        this@FireBaseRepository.callBack?.onSuccess(dataSnapshot)
                    } else {
                        //Log.i(TAG, "onFail - syncUser")
                        this@FireBaseRepository.callBack?.onFail()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.i(tag, "onCancelled - syncUser")
                    this@FireBaseRepository.callBack?.onFail()
                }
            })
        }
    }

    override fun notifyMessage(param: ChildEventListener) {
        auth.currentUser?.let {
            database.child(FRIEND_LIST)
                .child(
                    Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString())
                )
                .addChildEventListener(param)
        }
    }

    override fun syncConversation(acc: String, childEventListener: ChildEventListener) {
        auth.currentUser?.let {
            database.child(FRIEND_LIST)
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
            Log.i(tag,"syncFriendList = ${Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString())}")
            database.child(FRIEND_LIST)
                .child(Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString()))
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        this.callBack?.onSuccess(it.result)
                    } else {
                        Log.i(tag,"onFail")
                        this.callBack?.onFail()
                    }
                }
        }
    }

    /* update user Avatar */
    override fun updateAvatar(userModel: UserModel, imagePath: Uri) {
        val refStorage = FirebaseStorage.getInstance().reference.child("avatar/" + Parsefun.getInstance().parseEmailasAccount(auth.currentUser?.email.toString()) + ".jpg")
        Log.i(tag, userModel.useruri)
        refStorage.putFile(imagePath)
            .addOnSuccessListener { it ->
                it.storage.downloadUrl.addOnSuccessListener {
                    Log.i(tag,"it = $it")
                    userModel.useruri = it.toString()
                    this.callBack?.onSuccess(userModel)
                    database.child(ACCOUNT_LIST).child(Parsefun.getInstance().parseEmailasAccount(FirebaseAuth.getInstance().currentUser?.email.toString())).setValue(userModel)
                }
            }.addOnFailureListener{
                this.callBack?.onFail()
            }
    }

    /* update user introduction */
    override fun updateUserIntroduction(userModel: UserModel) {
        database.child(ACCOUNT_LIST).child(Parsefun.getInstance().parseEmailasAccount(FirebaseAuth.getInstance().currentUser?.email.toString())).setValue(userModel)
            .addOnCompleteListener {
                if (it.isSuccessful) this.callBack?.onSuccess(userModel)
            }
    }

    companion object {
        private const val ACCOUNT_LIST = "accountlist"
        private const val FRIEND_LIST = "friendlist"
        private const val CUSTOM_MESSAGE = "add new friend"
    }
}
