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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import javax.inject.Inject

class FireBaseRepository @Inject constructor(
    private val auth: FirebaseAuth
): IFireBaseApiService {
    private val tag = "ev_" + javaClass.simpleName
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val currentUser: FirebaseUser? = auth.currentUser

    override fun getUserName(): String? {
        TODO("Not yet implemented")
    }

    /* publish new article in PublishFragment */
    override fun publishArticle(articleModel: ArticleModel) = callbackFlow {
        articleModel.publishTime = Timestamp.now().seconds
        currentUser?.let {
            Log.i(tag,"board = ${articleModel.publishBoard}")
            articleModel.publishAuthor = currentUser.email
            database.child(articleModel.publishBoard).push().setValue(articleModel)
                .addOnCompleteListener{
                    if (it.isSuccessful) {
                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
        }

        awaitClose()
    }

    /* search account in ChatFragment */
    override fun searchAccount(str: String) = callbackFlow {
        Log.i(tag,str)
        val query = database.child(ACCOUNT_LIST).child(Parsefun.getInstance().parseEmailasAccount(str))
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    trySend(FirebaseResult.Success(dataSnapshot))
                } else {
                    trySend(FirebaseResult.Failure(Throwable()))
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                trySend(FirebaseResult.Failure(databaseError.toException()))
            }
        }
        query.addListenerForSingleValueEvent(listener)

        awaitClose { query.removeEventListener(listener) }
    }

    override fun addFriend(account: String) = callbackFlow {
        currentUser?.let {
            if (account == it.email.toString()) return@let
            val time = Timestamp.now().seconds
            val localAccount = Parsefun.getInstance().parseEmailasAccount(currentUser.email!!)
            val remoteAccount = Parsefun.getInstance().parseEmailasAccount(account)
            database.child(FRIEND_LIST).child(localAccount).child(remoteAccount).push()
                .setValue(FriendModel("", account, currentUser.email!!, CUSTOM_MESSAGE, time, 0))
                .addOnCompleteListener {
                    database.child(FRIEND_LIST).child(remoteAccount).child(localAccount).push()
                        .setValue(FriendModel("", account, currentUser.email!!, CUSTOM_MESSAGE, time, 0))
                        .addOnCompleteListener {
                            trySend(FirebaseResult.Success(true))
                        }.addOnFailureListener { exception ->
                            trySend(FirebaseResult.Failure(exception))
                        }
                }
        }

        awaitClose()
    }

    /* send message to another user */
    override fun send(userMessage: String, account: String, type: Int) = callbackFlow {
        val time = Timestamp.now().seconds
        val localAccount = Parsefun.getInstance().parseEmailasAccount(currentUser?.email!!)
        val remoteAccount = Parsefun.getInstance().parseEmailasAccount(account)
        database.child(FRIEND_LIST).child(localAccount).child(remoteAccount).push()
            .setValue(FriendModel("", account, currentUser.email!!, userMessage, time, type))
            .addOnCompleteListener {
                database.child(FRIEND_LIST).child(remoteAccount).child(localAccount).push()
                    .setValue(FriendModel("", account, currentUser.email!!, userMessage, time, type))
                    .addOnCompleteListener {
                        trySend(FirebaseResult.Success(true))
                    }.addOnFailureListener {
                        trySend(FirebaseResult.Failure(it))
                    }
            }

        awaitClose()
    }

    /* update avatar and send image */
    override fun send(imagePath: Uri) = callbackFlow {
        val refStorage = FirebaseStorage.getInstance().reference.child("images/" + UUID.randomUUID().toString() + ".jpg")
        Log.i(tag, imagePath.toString())
        refStorage.putFile(imagePath)
            .addOnSuccessListener { it ->
                it.storage.downloadUrl.addOnSuccessListener {
                    trySend(FirebaseResult.Success(it))
                }
            }.addOnFailureListener{
                trySend(FirebaseResult.Failure(it))
            }
        awaitClose()
    }

    /* update avatar and send image */
    override fun sendImageWithArticle(imagePath: Uri): Flow<FirebaseResult<Uri>> = callbackFlow {
        val refStorage = FirebaseStorage.getInstance().reference.child("article/" + Parsefun.getInstance().parseEmailasAccount(auth.currentUser?.email.toString()) + ".jpg")
        Log.i(tag, imagePath.toString())
        refStorage.putFile(imagePath)
            .addOnSuccessListener { it ->
                it.storage.downloadUrl.addOnSuccessListener {
                    trySend(FirebaseResult.Success(it))
                }
            }.addOnFailureListener{
                trySend(FirebaseResult.Failure(it))
            }
        awaitClose()
    }

    /* send reply in ArticleModel */
    override fun sendReply(userReply: String, articleId: String, board: String) = callbackFlow {
        val seconds = Timestamp.now().seconds
        val currentUser = auth.currentUser
        val articleModel = ArticleModel(
            "", seconds, board,
            currentUser?.email, "", userReply, "", articleId
        )
        database.child(articleModel.publishBoard).push().setValue(articleModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(FirebaseResult.Success(true))
                } else {
                    it.exception?.let { exception ->
                        trySend(FirebaseResult.Failure(exception))
                    }
                }
            }
        awaitClose()
    }

    /* user sign in */
    override fun signIn(acc: String, pwd: String) = callbackFlow {
        Log.i(tag,"sign in with acc = $acc, pwd = $pwd")
        auth.signInWithEmailAndPassword(acc, pwd)
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    trySend(FirebaseResult.Success(true))
                } else {
                    trySend(FirebaseResult.Failure(Throwable()))
                }
            }
        awaitClose{}
    }

    override fun getFirebaseAuth(): FirebaseAuth {
        return auth
    }

    override fun signOut() {
        auth.signOut()
    }


    /* sign up with email and upload a UserModel file to Firebase*/
    override fun signUp(acc: String, pwd: String) = callbackFlow {
        auth.createUserWithEmailAndPassword(acc, pwd)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    database.child(ACCOUNT_LIST).child(Parsefun.getInstance().parseEmailasAccount(acc)).setValue(UserModel(acc, "", "Hi, my name  is $acc"))
                    trySend(FirebaseResult.Success(true))
                } else {
                    trySend(FirebaseResult.Failure(Throwable()))
                }
            }

        awaitClose()
    }

    override fun syncBoard(board: String) = callbackFlow {
        database.child(board).get().addOnSuccessListener {
            trySend(FirebaseResult.Success(it))
        }.addOnFailureListener{
            trySend(FirebaseResult.Failure(it))
        }

        awaitClose()
    }

    override fun syncUser(acc: String): Flow<FirebaseResult<DataSnapshot>> = callbackFlow {
        val query = database.child(ACCOUNT_LIST).child(Parsefun.getInstance().parseEmailasAccount(acc))
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                trySend(FirebaseResult.Success(dataSnapshot))
            }
            override fun onCancelled(databaseError: DatabaseError) {
                trySend(FirebaseResult.Failure(databaseError.toException()))
            }
        }
        query.addListenerForSingleValueEvent(listener)

        awaitClose { query.removeEventListener(listener) }
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

    override fun syncFriendList() = callbackFlow {
        auth.currentUser?.let {
            Log.i(tag,"syncFriendList = ${Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString())}")
            database.child(FRIEND_LIST)
                .child(Parsefun.getInstance().parseEmailasAccount(auth.currentUser!!.email.toString()))
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        trySend(FirebaseResult.Success(it.result))
                    } else {
                        it.exception?.let { exception ->
                            trySend(FirebaseResult.Failure(exception))
                        }
                    }
                }
        }

        awaitClose()
    }

    /* update user Avatar */
    override fun updateAvatar(userModel: UserModel, imagePath: Uri) = callbackFlow {
        val refStorage = FirebaseStorage.getInstance().reference.child("avatar/" + Parsefun.getInstance().parseEmailasAccount(auth.currentUser?.email.toString()) + ".jpg")
        Log.i(tag, userModel.useruri)
        refStorage.putFile(imagePath)
            .addOnSuccessListener { it ->
                it.storage.downloadUrl.addOnSuccessListener {
                    Log.i(tag,"it = $it")
                    userModel.useruri = it.toString()
                    trySend(FirebaseResult.Success(userModel))
                    database.child(ACCOUNT_LIST).child(Parsefun.getInstance().parseEmailasAccount(FirebaseAuth.getInstance().currentUser?.email.toString())).setValue(userModel)
                }
            }.addOnFailureListener{
                trySend(FirebaseResult.Failure(it))
            }
        awaitClose()
    }

    /* update user introduction */
    override fun updateUserIntroduction(userModel: UserModel) = callbackFlow {
        database.child(ACCOUNT_LIST).child(Parsefun.getInstance().parseEmailasAccount(FirebaseAuth.getInstance().currentUser?.email.toString())).setValue(userModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(FirebaseResult.Success(userModel))
                }
            }
        awaitClose()
    }

    companion object {
        private const val ACCOUNT_LIST = "accountlist"
        private const val FRIEND_LIST = "friendlist"
        private const val CUSTOM_MESSAGE = "add new friend"
    }
}

sealed class FirebaseResult<out T> {
    data class Success<T>(val data: T) : FirebaseResult<T>()
    data class Failure(val error: Throwable) : FirebaseResult<Nothing>()
}