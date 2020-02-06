package com.pivot.pivot360.pivoteye.event

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.moxtra.sdk.ChatClient
import com.moxtra.sdk.chat.controller.ChatConfig
import com.moxtra.sdk.chat.controller.ChatController
import com.moxtra.sdk.chat.model.Chat
import com.moxtra.sdk.chat.model.ChatMember
import com.moxtra.sdk.chat.repo.ChatRepo
import com.moxtra.sdk.client.ChatClientDelegate
import com.moxtra.sdk.common.ApiCallback
import com.moxtra.sdk.common.BaseRepo
import com.moxtra.sdk.common.model.User
import com.moxtra.sdk.meet.controller.MeetSessionController
import com.moxtra.sdk.meet.model.Meet
import com.moxtra.sdk.meet.repo.MeetRepo
import com.pivot.pivot360.content.graphql.EventQuery
import com.pivot.pivot360.content.listeners.GenericListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivoteye.*
import com.pivot.pivot360.pivoteye.capture.AudioCaptureActivity
import com.pivot.pivot360.pivoteye.capture.CameraActivity
import com.pivot.pivot360.pivoteye.capture.VideoRecorderActivity
import com.pivot.pivot360.pivoteye.chat.ChatActivity
import com.pivot.pivot360.pivoteye.chat.MeetActivity
import com.pivot.pivot360.pivotglass.R
import java.util.ArrayList

/**
 * Main activity which displays a list of examples to the user
 */
class EventActivity : BaseActivity(), GenericListener<Any>,
    MenuListener {

    lateinit var mChatClientDelegate: ChatClientDelegate
    lateinit var mChatController: ChatController
    lateinit var mMeetSessionController: MeetSessionController
    lateinit var mChatRepo: ChatRepo
    lateinit var mChat: Chat
    lateinit var mMeetRepo: MeetRepo
    private val chatConfig = ChatConfig()
    private var mToken: String? = null
    private var mUniqueId: String? = null

    lateinit var mMeet: Meet

    lateinit var identity: String

    private val CLIENT_ID = "gVT4402EnwU"
    private val CLIENT_SECRET = "gz5L0B4Ng1o"
    private val ORG_ID: String? = null

    lateinit var eventName: TextView
    lateinit var eventDescription: TextView

    private val BASE_DOMAIN = "sandbox.moxtra.com"

    private lateinit var conferenceButton: Button
    private lateinit var documentButton: Button

    private var acceptThread: AcceptThread? = null

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState See Android docs
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setContentView(R.layout.activity_event)
        ChatClient.setupDomain(BASE_DOMAIN, null, null, null)

        if (intent != null && intent.getStringExtra("identity") != null) {
            identity = intent.getStringExtra("identity")
            mToken = intent.getStringExtra("token")
            mUniqueId = intent.getStringExtra("uniqueid")
            GraphQlApiHandler.instance.getData<EventQuery, GenericListener<Any>>(EventQuery.builder().token(mToken!!).id(identity!!).build(), this)
        } else {
            Toast.makeText(this, "Event id not found.", Toast.LENGTH_SHORT).show()
        }

        var extras = hashMapOf(Pair("identity", identity), Pair("token", mToken))

        //documentButton = findViewById(R.id.documentButton)
        conferenceButton = findViewById(R.id.videoConference)
        conferenceButton.isEnabled = false
        eventName = findViewById(R.id.event_name)
        eventDescription = findViewById(R.id.event_description)

        acceptThread = AcceptThread()

        acceptThread?.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mChatController.cleanup()
        } catch (e: Exception) {}
    }

    override fun OnResults(response: Any?) {
        when (response) {
            is EventQuery.Data -> {

                response.event().let {
                    when (it) {
                        is EventQuery.AsEventField -> {
                            onEventsField(it)
                        }
                        is EventQuery.AsResponseMessageField -> {
                            onResponseMessageField(it.message())
                        }
                        is EventQuery.AsAuthInfoField -> {
                            onAuthInfoField(it.message())
                        }
                    }
                }
            }
        }
    }

    override fun onError(message: String?) {
        runOnUiThread { Toast.makeText(this@EventActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String?) {
        runOnUiThread { Toast.makeText(this@EventActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String?) {
        runOnUiThread { Toast.makeText(this@EventActivity, message, Toast.LENGTH_SHORT).show() }
    }

    fun onEventsField(response: EventQuery.AsEventField) {
        runOnUiThread {
            eventName.text = response?.title()
            eventDescription.text = response?.description()
        }


        ChatClient.linkWithUniqueId(mUniqueId!!, CLIENT_ID, CLIENT_SECRET, ORG_ID, object :
            ApiCallback<ChatClientDelegate> {
            override fun onError(p0: Int, p1: String?) {
                runOnUiThread { Toast.makeText(this@EventActivity, p1, Toast.LENGTH_SHORT).show() }
            }

            override fun onCompleted(ccd: ChatClientDelegate) {
                mChatClientDelegate = ccd
                mChatRepo = mChatClientDelegate.createChatRepo()
                Log.e("TAG", "!!!!!!!!!!!!!ci"+response.chatIdentity())
                mChat = mChatRepo.getChatById(response.chatIdentity())

                Log.e("TAG", "!!!!!!!!!!!" + mChat.topic)

                mChatController = mChatClientDelegate.createChatController(mChat)

                mMeetRepo = mChatClientDelegate.createMeetRepo()
                mMeetRepo.fetchMeets(object: ApiCallback<List<Meet>> {
                    override fun onError(p0: Int, p1: String?) {
                        Log.d("TAG", "ERROR")
                    }

                    override fun onCompleted(p0: List<Meet>) {

                        mMeet = p0.last()
                    }

                })

                mMeetRepo!!.setOnChangedListener(object : BaseRepo.OnRepoChangedListener<Meet> {
                    override fun onCreated(items: List<Meet>) {
                        mMeet = items.last()
                    }

                    override fun onUpdated(items: List<Meet>) {
                        for (meet in items) {
                            if (meet.id == mMeet.id) {
                                return
                            }
                        }
                    }

                    override fun onDeleted(items: List<Meet>) {

                    }
                })

                conferenceButton.isEnabled = true
            }

        })
    }

    override fun startMeet() {
        val userList = ArrayList<User>()
        mChat.chatDetail.getMembers(object : ApiCallback<List<ChatMember>> {
            override fun onCompleted(chatMembers: List<ChatMember>) {
                userList.addAll(chatMembers)
                val topic = ChatClient.getMyProfile().firstName + "'s " + "meet"

                if (mMeet.isInProgress) {
                    MeetActivity.joinMeet(
                        this@EventActivity,
                        mMeet
                    )
                } else {
                    MeetActivity.startMeet(
                        this@EventActivity,
                        topic,
                        userList,
                        mChat
                    )
                }

                // mMyCustomLoader.dismissProgressDialog()

            }

            override fun onError(i: Int, s: String) {
                //mMyCustomLoader.dismissProgressDialog()

            }
        })
    }

    fun onCameraClick(view: View) {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    fun onDocumentClick(view: View) {

    }

    fun onAudioClick(view: View) {
        val intent = Intent(this, AudioCaptureActivity::class.java)
        startActivity(intent)
    }

    fun onVideoClick(view: View) {

    }

    fun onRecordVideoClick(view: View) {
        val intent = Intent(this, VideoRecorderActivity::class.java)
        startActivity(intent)
    }

    fun onAttachmentsClick(view: View) {
        val intent = Intent(this, EventAttachmentActivity::class.java)
        var extras = hashMapOf(Pair("identity", identity), Pair("token", mToken))
            for (entry in extras.entries) {
                intent.putExtra(entry.key, entry.value)
            }
        startActivity(intent)
    }

    fun onVideoConferenceClick(view: View) {
        startMeet()
    }

    fun onViewChatClick(view: View) {
        ChatActivity.showChat(this, mChat)
    }
}

interface MenuListener {
    fun startMeet()
}