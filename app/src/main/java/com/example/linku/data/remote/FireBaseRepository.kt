package com.example.linku.data.remote

import android.content.Context
import android.util.Log
import com.example.linku.data.local.ArticleModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import kotlin.jvm.internal.Intrinsics


class FireBaseRepository(context: Context) {
    private val TAG = toString()
    private val auth: FirebaseAuth? = null
    private var database: DatabaseReference? = null
    private var mcallBack: IFireOperationCallBack? = null

    fun FireBaseRepository(callBack: IFireOperationCallBack?) {
        val reference: DatabaseReference = DataBase.getDatabase(Firebase).getReference()
        database = reference
        val auth: FirebaseAuth
        this.auth = auth
        val currentUser = auth.currentUser
        mcallBack = callBack
        if (this.auth != null) {
            Log.i("evlog", "auth login" + this.auth)
        } else {
            Log.i("evlog", "auth null")
        }
        if (currentUser != null) {
            Log.i("evlog", "user login")
        } else {
            Log.i("evlog", "user null")
        }
    }

    // com.example.linku.data.remote.IFireBaseApiService
    fun signUp(acc: String?, pwd: String?) {
        Intrinsics.checkNotNullParameter(acc, "acc")
        Intrinsics.checkNotNullParameter(pwd, "pwd")
        auth!!.createUserWithEmailAndPassword(acc!!, pwd!!)
            .addOnCompleteListener(`FireBaseRepository$$ExternalSyntheticLambda6`.INSTANCE)
    }

    fun signOut() {
        auth!!.signOut()
    }

    // com.example.linku.data.remote.IFireBaseApiService
    fun publishArticle(articleModel: ArticleModel): ArticleModel? {
        Intrinsics.checkNotNullParameter(articleModel, "articleModel")
        articleModel.publishTime = Timestamp.now().getSeconds()
        val currentUser = auth!!.currentUser
        articleModel.publishAuthor = currentUser?.email
        database!!.child(articleModel.publishBoard!!).push().setValue(articleModel)
            .addOnCompleteListener(
                OnCompleteListener<Any?> { task ->
                    // from class: com.example.linku.data.remote.FireBaseRepository$$ExternalSyntheticLambda2
                    // com.google.android.gms.tasks.OnCompleteListener
                    FireBaseRepository.`m2393publishArticle$lambda2`(this@FireBaseRepository, task)
                })
        return articleModel
    }



}