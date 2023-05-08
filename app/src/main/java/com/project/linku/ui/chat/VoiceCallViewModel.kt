package com.project.linku.ui.chat

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine

class VoiceCallViewModel(application: Application) : AndroidViewModel(application) {

    private val mapplication: Application = application
    private val TAG = "ev_" + javaClass.simpleName

    private val _statusDialog = MutableLiveData<String>()
    val statusDialog: LiveData<String> = _statusDialog

    /* agora >> */
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    // Fill the App ID of your project generated on Agora Console.
    private val APP_ID = ""
    // Fill the channel name.
    private val CHANNEL = ""
    // Fill the temp token generated on Agora Console.
    private val TOKEN = ""
    private var mRtcEngine: RtcEngine?= null

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            _statusDialog.postValue("UserJoined")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            Log.i(TAG, "onUserOffline")
            endCall()
            _statusDialog.postValue("UserOffline")
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            _statusDialog.postValue("JoinChannelSuccess")
        }
    }
    /* agora << */

    fun initializeAndJoinChannel(channel: String) {
        try {
            mRtcEngine = RtcEngine.create(mapplication.applicationContext, APP_ID, mRtcEventHandler)
            mRtcEngine!!.joinChannel(TOKEN, channel, "", 0)
        } catch (e: Exception) {
            Log.i(TAG, "error = $e")
        }
    }

    fun endCall() {
        mRtcEngine!!.leaveChannel()
        Log.i(TAG, "endCall")
        _statusDialog.postValue("endCall")
    }

    fun muteCall() {
        mRtcEngine!!.muteAllRemoteAudioStreams(true)
    }

    fun speakerCall() {
        mRtcEngine!!.setEnableSpeakerphone(!(mRtcEngine!!.isSpeakerphoneEnabled))
    }
}