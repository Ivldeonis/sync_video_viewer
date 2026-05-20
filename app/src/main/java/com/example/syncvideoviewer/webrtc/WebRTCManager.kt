package com.example.syncvideoviewer.webrtc

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import org.webrtc.*
import java.nio.ByteBuffer

class WebRTCManager(
    private val context: Context,
    private val gson: Gson
) {
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var localDataChannel: DataChannel? = null

    private val TAG = "WebRTCManager"

    init {
        initPeerConnectionFactory()
    }

    private fun initPeerConnectionFactory() {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)

        val factoryOptions = PeerConnectionFactory.Options()
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(factoryOptions)
            .createPeerConnectionFactory()
    }

    fun createPeerConnection(
        roomId: String,
        onIceCandidateListener: (IceCandidate) -> Unit
    ) {
        try {
            val iceServers = listOf(
                PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
                PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
            )

            val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
                iceTransportsType = PeerConnection.IceTransportsType.ALL
                bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
                rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
            }

            peerConnection = peerConnectionFactory?.createPeerConnection(
                rtcConfig,
                object : PeerConnection.Observer {
                    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
                        Log.d(TAG, "Signaling state: ${p0?.name}")
                    }

                    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                        Log.d(TAG, "Ice connection state: ${p0?.name}")
                    }

                    override fun onIceConnectionReceivingChange(p0: Boolean) {}

                    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
                        Log.d(TAG, "Ice gathering state: ${p0?.name}")
                    }

                    override fun onIceCandidate(p0: IceCandidate?) {
                        p0?.let { onIceCandidateListener(it) }
                    }

                    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}

                    override fun onAddStream(p0: MediaStream?) {}

                    override fun onRemoveStream(p0: MediaStream?) {}

                    override fun onDataChannel(p0: DataChannel?) {
                        Log.d(TAG, "On data channel: ${p0?.label()}")
                        setupDataChannelListeners(p0)
                    }

                    override fun onRenegotiationNeeded() {}

                    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}

                    override fun onTrack(p0: RtpTransceiver?) {}

                    override fun onConnectionChange(p0: PeerConnection.PeerConnectionState?) {
                        Log.d(TAG, "Connection state: ${p0?.name}")
                    }

                    override fun onStandardizedIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}
                }
            )

            // Create data channel
            val dataChannelInit = DataChannel.Init()
            localDataChannel = peerConnection?.createDataChannel("sync", dataChannelInit)
            setupDataChannelListeners(localDataChannel)

            Log.d(TAG, "Peer connection created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create peer connection", e)
        }
    }

    private fun setupDataChannelListeners(dataChannel: DataChannel?) {
        dataChannel?.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(p0: Long) {}

            override fun onStateChange() {
                Log.d(TAG, "Data channel state change: ${dataChannel?.state()}")
            }

            override fun onMessage(p0: DataChannel.Buffer?) {
                p0?.let {
                    val message = String(it.data.array())
                    Log.d(TAG, "Data channel message: $message")
                }
            }
        })
    }

    fun sendDataChannelMessage(message: String) {
        try {
            localDataChannel?.let {
                val buffer = DataChannel.Buffer(
                    ByteBuffer.wrap(message.toByteArray()),
                    false
                )
                it.send(buffer)
                Log.d(TAG, "Message sent via data channel: $message")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send data channel message", e)
        }
    }

    fun close() {
        try {
            localDataChannel?.dispose()
            peerConnection?.close()
            peerConnectionFactory?.dispose()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing WebRTCManager", e)
        }
    }
}
