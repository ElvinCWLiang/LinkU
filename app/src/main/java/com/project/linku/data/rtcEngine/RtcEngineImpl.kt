package com.project.linku.data.rtcEngine

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.agora.rtc.IRtcEngineEventHandler

class RtcEngineImpl @AssistedInject constructor(
    application: Application,
    @Assisted rtcEventHandler: IRtcEngineEventHandler
): RtcEngine {
    private val app: ApplicationInfo =
        application.packageManager.getApplicationInfo(application.packageName, PackageManager.GET_META_DATA)
    private var appId: String = app.metaData.getString(KEY, "1cd04ba4b0000000bef846dac00000bb")

    // Fill the temp token generated on Agora Console.
    private val token = ""

    private val rtcEngine = io.agora.rtc.RtcEngine.create(application, appId, rtcEventHandler)

    override fun initializeAndJoinChannel(channel: String) {
        rtcEngine.joinChannel(token, channel, "", 0)
    }

    override fun endCall() {
        rtcEngine.leaveChannel()
    }

    override fun muteCall() {
        rtcEngine.muteAllRemoteAudioStreams(true)
    }

    override fun speakerCall() {
        rtcEngine.setEnableSpeakerphone(!(rtcEngine.isSpeakerphoneEnabled))
    }

    companion object {
        const val KEY = "AGORA_SDK_APPID"
    }
}

interface RtcEngine {
    fun initializeAndJoinChannel(channel: String)
    fun endCall()
    fun muteCall()
    fun speakerCall()
}

@AssistedFactory
interface RtcEngineFactory {
    fun create(mRtcEventHandler: IRtcEngineEventHandler): RtcEngineImpl
}