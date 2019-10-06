package com.pivot.pivot360.pivoteye

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.GridView
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
import com.pivot.pivot360.content.listeners.EventResponseListener
import com.pivot.pivot360.network.GraphQlApiHandler
import com.pivot.pivot360.pivotglass.R
import kotlinx.android.synthetic.main.fragment_attachment.*
import java.util.ArrayList

/**
 * Main activity which displays a list of examples to the user
 */
class MenuActivity : Activity(), EventResponseListener, MenuListener {

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

    private val tileList = arrayOfNulls<MainMenuTile>(7)

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
        setContentView(R.layout.activity_menu)

        if (intent != null && intent.getStringExtra("identity") != null) {
            identity = intent.getStringExtra("identity")
            mToken = intent.getStringExtra("token")
            GraphQlApiHandler.instance.getEvent(EventQuery.builder().token(mToken!!).id(identity!!).build(), this)
        } else {
            Toast.makeText(this, "Event id not found.", Toast.LENGTH_SHORT).show()
        }

        var extras = hashMapOf(Pair("identity", identity), Pair("token", mToken))

        tileList[0] =
            MainMenuTile(this, R.string.camera_command, R.drawable.camera,
                CameraActivity::class.java, null, null)
        tileList[1] =
            MainMenuTile(this, R.string.document_command, R.drawable.document,
                DocumentActivity::class.java, null, null)
        tileList[2] = MainMenuTile(this, R.string.movie_command, R.drawable.movie,
            MovieActivity::class.java, null, null)
        tileList[3] =
            MainMenuTile(this, R.string.barcode_command, R.drawable.barcode,
                BarcodeActivity::class.java, null, null)

        tileList[4] =
            MainMenuTile(this, R.string.audio_command, R.drawable.asr,
                AudioCaptureActivity::class.java, null, null)

        tileList[5] = MainMenuTile(
            this,
            R.string.attachments_command,
            android.R.drawable.ic_menu_gallery,
            EventActivity::class.java, extras, null)
        tileList[6] =
            MainMenuTile(this, R.string.start_meet_command, R.drawable.ic_video_camera,
                MeetActivity::class.java, null, this)

        mMainMenuTileAdaptor = MainMenuTileAdaptor(tileList)
        mGridView = findViewById(R.id.gridView) as GridView
        mGridView!!.adapter = mMainMenuTileAdaptor

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mChatController != null) {
            mChatController!!.cleanup()
        }
    }

    override fun onError(message: String) {
        runOnUiThread { Toast.makeText(this@MenuActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onAuthInfoField(message: String) {
        runOnUiThread { Toast.makeText(this@MenuActivity, message, Toast.LENGTH_SHORT).show() }

    }

    override fun onResponseMessageField(message: String) {
        runOnUiThread { Toast.makeText(this@MenuActivity, message, Toast.LENGTH_SHORT).show() }
    }

    override fun OnEventsField(response: EventQuery.AsEventField?) {

        ChatClient.linkWithUniqueId(PreferenceUtil.getUserUniqueIdentiry(this@MenuActivity)!!, CLIENT_ID, CLIENT_SECRET, ORG_ID, object :
            ApiCallback<ChatClientDelegate> {
            override fun onError(p0: Int, p1: String?) {

            }

            override fun onCompleted(ccd: ChatClientDelegate) {
                mChatClientDelegate = ccd
                mChatRepo = mChatClientDelegate!!.createChatRepo()
                mChat = mChatRepo.getChatById(response?.chatIdentity())

                Log.e("TAG", "!!!!!!!!!!!" + mChat.topic)

                mChatController = mChatClientDelegate.createChatController(mChat)

                mMeetRepo = mChatClientDelegate.createMeetRepo()
                mMeetRepo.fetchMeets(object: ApiCallback<List<Meet>> {
                    override fun onError(p0: Int, p1: String?) {
                        Log.d("TAG", "ERROR")
                    }

                    override fun onCompleted(p0: List<Meet>) {

                        mMeet = p0.last()

                        runOnUiThread {
                            if (mMeet.isInProgress) {
                                tileList[6]!!.setSmallText(R.string.join_meet_command)
                            } else {
                                tileList[6]!!.setSmallText(R.string.start_meet_command)
                            }
                        }

                    }

                })

                mMeetRepo!!.setOnChangedListener(object : BaseRepo.OnRepoChangedListener<Meet> {
                    override fun onCreated(items: List<Meet>) {
                        mMeet = items.last()
                        runOnUiThread {
                            if (mMeet.isInProgress) {
                                tileList[6]!!.setSmallText(R.string.join_meet_command)
                            } else {
                                tileList[6]!!.setSmallText(R.string.start_meet_command)
                            }
                        }
                    }

                    override fun onUpdated(items: List<Meet>) {
                        for (meet in items) {
                            if (meet.id == mMeet.id) {
                                mMeet = meet
                                runOnUiThread {
                                    if (mMeet.isInProgress) {
                                        tileList[6]!!.setSmallText(R.string.join_meet_command)
                                    } else {
                                        tileList[6]!!.setSmallText(R.string.start_meet_command)
                                    }
                                }
                                return
                            }
                        }
                    }

                    override fun onDeleted(items: List<Meet>) {

                    }
                })
            }

        })
    }

    override fun startMeet() {
        val userList = ArrayList<User>()
        mChat.chatDetail.getMembers(object : ApiCallback<List<ChatMember>> {
            override fun onCompleted(chatMembers: List<ChatMember>) {
                userList.addAll(chatMembers)
                val topic = ChatClient.getMyProfile().firstName + "'s " + "meet"

                if (mMeet != null) {
                    MeetActivity.joinMeet(this@MenuActivity, mMeet)
                } else {
                    MeetActivity.startMeet(this@MenuActivity, topic, userList, mChat)
                }

                // mMyCustomLoader.dismissProgressDialog()

            }

            override fun onError(i: Int, s: String) {
                //mMyCustomLoader.dismissProgressDialog()

            }
        })
    }


}

interface MenuListener {
    fun startMeet()
}