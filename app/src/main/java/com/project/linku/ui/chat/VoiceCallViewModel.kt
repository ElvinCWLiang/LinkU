package com.project.linku.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.linku.data.rtcEngine.RtcEngineFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtc.IRtcEngineEventHandler
import javax.inject.Inject

@HiltViewModel
class VoiceCallViewModel @Inject constructor(
    rtcEngineFactory: RtcEngineFactory
): ViewModel() {
    private val TAG = "ev_" + javaClass.simpleName

    private val _statusDialog = MutableLiveData<String>()
    val statusDialog: LiveData<String> = _statusDialog

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            _statusDialog.postValue("UserJoined")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            endCall()
            _statusDialog.postValue("UserOffline")
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            _statusDialog.postValue("JoinChannelSuccess")
        }
    }

    val rtcEngine = rtcEngineFactory.create(mRtcEventHandler)

    /* agora >> */
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    fun initializeAndJoinChannel(channel: String) {
        try {
            rtcEngine.initializeAndJoinChannel(channel)
        } catch (e: Exception) {
            Log.i(TAG, "error = $e")
        }
    }

    fun endCall() {
        rtcEngine.endCall()
        Log.i(TAG, "endCall")
        _statusDialog.postValue("endCall")
    }

    fun muteCall() {
        rtcEngine.muteCall()
    }

    fun speakerCall() {
        rtcEngine.speakerCall()
    }
}
