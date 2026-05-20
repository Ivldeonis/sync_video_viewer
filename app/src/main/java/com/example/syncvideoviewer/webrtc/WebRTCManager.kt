package com.example.syncvideoviewer.webrtc

import android.content.Context
import android.util.Log
import com.example.syncvideoviewer.data.repository.FirebaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebRTCManager @Inject constructor(
    private val context: Context,
    private val firebaseRepository: FirebaseRepository
) {

    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var dataChannel: DataChannel? = null
    private var localDataChannel: DataChannel? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    companion object {
        private const val TAG = "WebRTCManager"
    }

    init {
        initWebRTC()
    }

    private fun initWebRTC() {
        try {
            PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(context)
                    .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
                    .createInitializationOptions()
            )

            val options = PeerConnectionFactory.Options()
            peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(DefaultVideoEncoderFactory(
                    null,
                    true,
                    true
                ))
                .setVideoDecoderFactory(DefaultVideoDecoderFactory(null))
                .createPeerConnectionFactory()

            Log.d(TAG, "WebRTC initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize WebRTC", e)
        }
    }

    fun createPeerConnection(
        roomId: String,
        onIceCandidateListener: (ICECandidateListener) -> Unit
    ) {
        try {
            val iceServers = listOf(
                PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
                PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
            )

            val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
                iceTransportPolicy = PeerConnection.IceTransportPolicy.ALL
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
                        Log.d(TAG, "ICE connection state: ${p0?.name}")
                    }

                    override fun onIceConnectionReceivingChange(p0: Boolean) {}

                    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
                        Log.d(TAG, "ICE gathering state: ${p0?.name}")
                    }

                    override fun onIceCandidate(iceCandidate: IceCandidate?) {
                        iceCandidate?.let {
                            Log.d(TAG, "New ICE candidate: ${it.sdp}")
                            coroutineScope.launch {
                                firebaseRepository.addICECandidate(
                                    roomId,
                                    firebaseRepository.getCurrentUserId(),
                                    com.example.syncvideoviewer.data.model.ICECandidate(
                                        candidate = it.sdp,
                                        sdpMLineIndex = it.sdpMLineIndex,
                                        sdpMid = it.sdpMid
                                    )
                                )
                            }
                        }
                    }

                    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}

                    override fun onAddStream(p0: MediaStream?) {}

                    override fun onRemoveStream(p0: MediaStream?) {}

                    override fun onDataChannel(dataChannel: DataChannel?) {
                        Log.d(TAG, "Data channel received")
                        this@WebRTCManager.dataChannel = dataChannel
                        setupDataChannelListeners(dataChannel)
                    }

                    override fun onRenegotiationNeeded() {
                        Log.d(TAG, "Renegotiation needed")
                    }

                    override fun onAddTrack(
                        p0: RtpReceiver?,
                        p1: Array<out MediaStream>?
                    ) {
                    }

                    override fun onTrack(p0: RtpTransceiver?) {}

                    override fun onConnectionChange(p0: PeerConnection.PeerConnectionState?) {
                        Log.d(TAG, "Connection state: ${p0?.name}")
                    }

                    override fun onStandardizedIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}

                    override fun onStandardizedIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
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

    fun createOffer() {
        peerConnection?.createOffer(
            object : SdpObserver {
                override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                    peerConnection?.setLocalDescription(this, sessionDescription)
                    Log.d(TAG, "Offer created successfully")
                }

                override fun onSetSuccess() {
                    Log.d(TAG, "Local description set successfully")
                }

                override fun onCreateFailure(p0: String?) {
                    Log.e(TAG, "Offer creation failed: $p0")
                }

                override fun onSetFailure(p0: String?) {
                    Log.e(TAG, "Set description failed: $p0")
                }
            },
            MediaConstraints()
        )
    }

    fun createAnswer() {
        peerConnection?.createAnswer(
            object : SdpObserver {
                override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                    peerConnection?.setLocalDescription(this, sessionDescription)
                    Log.d(TAG, "Answer created successfully")
                }

                override fun onSetSuccess() {
                    Log.d(TAG, "Local description set successfully")
                }

                override fun onCreateFailure(p0: String?) {
                    Log.e(TAG, "Answer creation failed: $p0")
                }

                override fun onSetFailure(p0: String?) {
                    Log.e(TAG, "Set description failed: $p0")
                }
            },
            MediaConstraints()
        )
    }

    fun setRemoteDescription(sdp: String, type: String) {
        val sessionDescription = SessionDescription(
            SessionDescription.Type.fromCanonicalForm(type),
            sdp
        )
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onSetSuccess() {
                Log.d(TAG, "Remote description set successfully")
            }

            override fun onCreateFailure(p0: String?) {}
            override fun onSetFailure(p0: String?) {
                Log.e(TAG, "Set remote description failed: $p0")
            }
        }, sessionDescription)
    }

    private fun setupDataChannelListeners(channel: DataChannel?) {
        channel?.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(previousAmount: Long) {}

            override fun onStateChange() {
                Log.d(TAG, "Data channel state: ${channel.state()}")
            }

            override fun onMessage(buffer: DataChannel.Buffer?) {
                buffer?.data?.let {
                    val message = String(it.array())
                    Log.d(TAG, "Data channel message: $message")
                }
            }
        })
    }

    fun sendDataChannelMessage(message: String) {
        try {
            localDataChannel?.let {
                val buffer = DataChannel.Buffer(
                    org.webrtc.DataChannel.ByteBufferFactory(message.toByteArray()),
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
            localDataChannel?.close()
            dataChannel?.close()
            peerConnection?.close()
            peerConnection = null
            Log.d(TAG, "WebRTC connection closed")
        } catch (e: Exception) {
            Log.e(TAG, "Error closing WebRTC connection", e)
        }
    }
}

interface ICECandidateListener {
    fun onIceCandidate(candidate: IceCandidate)
}
