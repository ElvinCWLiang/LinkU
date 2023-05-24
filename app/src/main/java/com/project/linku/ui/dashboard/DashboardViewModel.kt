package com.project.linku.ui.dashboard

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.project.linku.data.local.LocalDatabase
import com.project.linku.data.local.LocalRepository
import com.project.linku.data.local.UserModel
import com.project.linku.data.remote.FireBaseRepository
import com.project.linku.ui.utils.Save
import com.google.firebase.auth.FirebaseAuth
import com.project.linku.data.remote.FirebaseResult
import com.project.linku.ui.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository : FireBaseRepository,
    private val application: Application
) : ViewModel() {

    private val tag = "ev_" + javaClass.simpleName
    private val _userAccount = MutableLiveData<String>().apply { value = FirebaseAuth.getInstance().currentUser?.email }
    val userAccount : LiveData<String> = _userAccount
    private val _updateRespond = MutableLiveData<Event<String>>()
    val updateRespond : LiveData<Event<String>> = _updateRespond.distinctUntilChanged()
    private val _isAvatarChanged = MutableLiveData<Uri>()
    val isAvatarChanged : LiveData<Uri> = _isAvatarChanged
    private val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString()
    private val _introduction = MutableLiveData<String>().apply {
        viewModelScope.launch(Dispatchers.IO) {
            postValue(LocalRepository(LocalDatabase.getInstance(this@DashboardViewModel.application)).getUser(currentUser)?.userintroduction)
        }
    }
    val introduction : LiveData<String> = _introduction

    fun signUp(acc: String, pwd: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.signUp(acc, pwd).collectLatest {
                when (it) {
                    is FirebaseResult.Success -> {
                        Log.i(tag, acc + "signUp")
                        _updateRespond.value = Event("Success")
                        signIn(acc, pwd)
                    }
                    else -> {
                        _updateRespond.value = Event("Fail")
                    }
                }
            }
        }
    }

    fun signIn(account: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.signIn(
                account, password
            ).collectLatest {
                when (it) {
                    is FirebaseResult.Success -> {
                        Save.saveUser(application, account, password)
                        Log.i(tag, account + "signIn")
                        _userAccount.value = account
                        _updateRespond.value = Event("Success")
                        _isAvatarChanged.value = Uri.parse(Save.getUserAvatarUri(application, account))
                        viewModelScope.launch(Dispatchers.IO) {
                            syncUser(account)
                        }
                    }
                    else -> {
                        Log.i(tag, "signIn onFail")
                        _updateRespond.value = Event("Fail")
                    }
                }
            }
        }
    }

    private fun syncUser(account: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncUser(account).collectLatest {
                when (it) {
                    is FirebaseResult.Success -> {
                        //save current user
                        viewModelScope.launch {
                            val userModel = it.data.getValue(UserModel::class.java)
                            LocalRepository(LocalDatabase.getInstance(application)).insertUserList(userModel)
                            _introduction.value = userModel?.userintroduction.orEmpty()
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun updateAvatar(imagePath: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val userModel = LocalRepository(LocalDatabase.getInstance(application)).getUser(currentUser)
            userModel?.let {
                userModel.useruri = imagePath.toString()
                repository.updateAvatar(userModel, imagePath).collectLatest {
                    when (it) {
                        is FirebaseResult.Success -> {
                            viewModelScope.launch {
                                LocalRepository(LocalDatabase.getInstance(application)).insertUserList(it.data)
                            }
                            _isAvatarChanged.value = Uri.parse(it.data.useruri)
                            Save.saveUserAvatarUri(application, currentUser, Uri.parse(it.data.useruri))
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun updateUserIntroduction(intro: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userModel = LocalRepository(LocalDatabase.getInstance(application)).getUser(currentUser)
            userModel?.let {
                userModel.userintroduction = intro
                repository.updateUserIntroduction(userModel).collectLatest {
                    _introduction.value = it.data.userintroduction
                    viewModelScope.launch {
                        LocalRepository(LocalDatabase.getInstance(application)).insertUserList(it.data)
                    }
                }
            }
        }
    }

    fun logout() {
        repository.signOut()
        viewModelScope.launch(Dispatchers.IO) {
            LocalRepository(LocalDatabase.getInstance(application)).deleteFriendList()
        }
        Save.deleteUser(application)
        _userAccount.value = null
    }

    fun login() {
        FirebaseAuth.getInstance().addAuthStateListener {
            _userAccount.value = it.currentUser.toString()
        }
    }
}