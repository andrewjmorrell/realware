package com.pivot.pivot360.pivoteye

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.GridView
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
import com.pivot.pivot360.content.graphql.AssetQuery
import com.pivot.pivot360.content.listeners.SummaryResponseListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R
import java.util.ArrayList

/**
 * Main activity which displays a list of examples to the user
 */
class AssetActivity : Activity(), SummaryResponseListener, MenuListener {

    private var mMainMenuTileAdaptor: MainMenuTileAdaptor? = null
    private var mGridView: GridView? = null

    lateinit var mChatClientDelegate: ChatClientDelegate
    lateinit var mChatController: ChatController
    lateinit var mMeetSessionController: MeetSessionController
    lateinit var mChatRepo: ChatRepo
    lateinit var mChat: Chat
    lateinit var mMeetRepo: MeetRepo
    private val chatConfig = ChatConfig()
    private var mToken: String? = null

    lateinit var mMeet: Meet

    lateinit var identity: String

    private val CLIENT_ID = "gVT4402EnwU"
    private val CLIENT_SECRET = "gz5L0B4Ng1o"
    private val ORG_ID: String? = null

    lateinit var cameraButton: Button
    lateinit var documentButton: Button
    lateinit var audioButton: Button
    lateinit var videoButton: Button
    lateinit var attachmentButton: Button
    lateinit var videoConferenceButton: Button

    lateinit var eventName: TextView
    lateinit var eventDescription: TextView

    lateinit var mResponse: AssetQuery.AsAssetField

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState See Android docs
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_asset)

        if (intent != null && intent.getStringExtra("identity") != null) {
            identity = intent.getStringExtra("identity")
            mToken = PreferenceUtil.getToken(this)
            GraphQlApiHandler.instance.getAssets(AssetQuery.builder().token(mToken!!).id(identity!!).build(), this)
        } else {
            Toast.makeText(this, "asset id not found.", Toast.LENGTH_SHORT).show()
        }

        var extras = hashMapOf(Pair("identity", identity), Pair("token", mToken))

        //cameraButton = findViewById(R.id.cameraButton)
        //documentButton = findViewById(R.id.documentButton)
        eventName = findViewById(R.id.event_name)
        eventDescription = findViewById(R.id.event_description)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChatController != null) {
            mChatController!!.cleanup()
        }
    }

    override fun onError(message: String) {
        runOnUiThread { Toast.makeText(this@AssetActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String) {
        runOnUiThread { Toast.makeText(this@AssetActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String) {
        runOnUiThread { Toast.makeText(this@AssetActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAssetField(response: AssetQuery.AsAssetField?) {
        runOnUiThread {
            eventName.text = response?.name()
            eventDescription.text = response?.description()
        }

        mResponse = response!!

        val mChatRepo = mChatClientDelegate!!.createChatRepo()
        mChatRepo!!.createGroupChat(mResponse.name(), object : ApiCallback<Chat> {
            override fun onCompleted(_chat: Chat) {
                Log.i("!!!", "Create group chat successfully.")
                mChat = _chat
                mChat.chatDetail.inviteMembers(mChat.owner.orgId, mResponse.members()?.map { it.account()?.identity() }, object : ApiCallback<Void> {
                    override fun onCompleted(result: Void?) {
                        Log.i("!!!", "Invite members successfully.")
                    }

                    override fun onError(errorCode: Int, errorMsg: String) {
                        Log.e("!!!", "Failed to invite members, errorCode=$errorCode, errorMsg=$errorMsg")
                    }
                })
                mChatController = mChatClientDelegate!!.createChatController(mChat)

                ChatActivity.showChat(this@AssetActivity, mChat)
            }

            override fun onError(errorCode: Int, errorMsg: String) {
                Log.e("!!!", "Failed to create group chat, errorCode=$errorCode, errorMsg=$errorMsg")
                //finishInMainThread()
                //mMyCustomLoader.dismissProgressDialog()
            }
        })


//        ChatClient.linkWithUniqueId(PreferenceUtil.getUserUniqueIdentiry(this@AssetActivity)!!, CLIENT_ID, CLIENT_SECRET, ORG_ID, object :
//            ApiCallback<ChatClientDelegate> {
//            override fun onError(p0: Int, p1: String?) {
//
//            }
//
//            override fun onCompleted(ccd: ChatClientDelegate) {
//                mChatClientDelegate = ccd
//                mChatRepo = mChatClientDelegate!!.createChatRepo()
//                mChat = mChatRepo.getChatById(response?.chatIdentity())
//
//                Log.e("TAG", "!!!!!!!!!!!" + mChat.topic)
//
//                mChatController = mChatClientDelegate.createChatController(mChat)
//
//                mMeetRepo = mChatClientDelegate.createMeetRepo()
//                mMeetRepo.fetchMeets(object: ApiCallback<List<Meet>> {
//                    override fun onError(p0: Int, p1: String?) {
//                        Log.d("TAG", "ERROR")
//                    }
//
//                    override fun onCompleted(p0: List<Meet>) {
//
//                        mMeet = p0.last()
//                    }
//
//                })
//
//                mMeetRepo!!.setOnChangedListener(object : BaseRepo.OnRepoChangedListener<Meet> {
//                    override fun onCreated(items: List<Meet>) {
//                        mMeet = items.last()
//                    }
//
//                    override fun onUpdated(items: List<Meet>) {
//                        for (meet in items) {
//                            if (meet.id == mMeet.id) {
//                                return
//                            }
//                        }
//                    }
//
//                    override fun onDeleted(items: List<Meet>) {
//
//                    }
//                })
//            }
//
//        })
    }

    override fun startMeet() {
        val userList = ArrayList<User>()
        mChat.chatDetail.getMembers(object : ApiCallback<List<ChatMember>> {
            override fun onCompleted(chatMembers: List<ChatMember>) {
                userList.addAll(chatMembers)
                val topic = ChatClient.getMyProfile().firstName + "'s " + "meet"

                if (mMeet.isInProgress) {
                    MeetActivity.joinMeet(this@AssetActivity, mMeet)
                } else {
                    MeetActivity.startMeet(this@AssetActivity, topic, userList, mChat)
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
        val intent = Intent(this, DocumentActivity::class.java)
        startActivity(intent)
    }

    fun onEventsClick(view: View) {
        val intent = Intent(this, EventsByAssetActivity::class.java)
        var extras = hashMapOf(Pair("identity", identity), Pair("token", mToken))
        for (entry in extras.entries) {
            intent.putExtra(entry.key, entry.value)
        }
        startActivity(intent)
    }

    fun onAudioClick(view: View) {
        val intent = Intent(this, AudioCaptureActivity::class.java)
        startActivity(intent)
    }

    fun onVideoClick(view: View) {
        val intent = Intent(this, MovieActivity::class.java)
        startActivity(intent)
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