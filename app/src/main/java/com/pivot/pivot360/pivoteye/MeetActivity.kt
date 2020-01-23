package com.pivot.pivot360.pivoteye

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.moxtra.sdk.ChatClient
import com.moxtra.sdk.chat.controller.ChatConfig
import com.moxtra.sdk.chat.model.Chat
import com.moxtra.sdk.client.ChatClientDelegate
import com.moxtra.sdk.common.ApiCallback
import com.moxtra.sdk.common.model.User
import com.moxtra.sdk.meet.controller.MeetSessionConfig
import com.moxtra.sdk.meet.controller.MeetSessionController
import com.moxtra.sdk.meet.model.Meet
import com.moxtra.sdk.meet.model.MeetSession
import com.moxtra.sdk.meet.repo.MeetRepo
import com.pivot.pivot360.pivoteye.Constants.CLIENT_ID
import com.pivot.pivot360.pivoteye.Constants.CLIENT_SECRET
import com.pivot.pivot360.pivotglass.R
import java.util.*

class MeetActivity : BaseActivity(), GlassGestureDetector.OnGestureListener {

    private val mHandler = Handler()

    private var mChatClientDelegate: ChatClientDelegate? = null
    private var mMeetRepo: MeetRepo? = null
    private var mMeetSession: MeetSession? = null
    private var mMeetSessionController: MeetSessionController? = null
    lateinit var mChat: Chat

    private var glassGestureDetector: GlassGestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setContentView(R.layout.activity_meet)
        val intent = intent
        if (intent == null) {
            Log.e(TAG, "no intent received")
            finishInMainThread()
            return
        }

        glassGestureDetector = GlassGestureDetector(this, this)

        mChatClientDelegate = ChatClient.getClientDelegate()
        if (mChatClientDelegate == null) {
            Log.e(TAG, "ChatClient is null")
            finishInMainThread()
            return
        }
        mMeetRepo = mChatClientDelegate!!.createMeetRepo()

        setLoading(true)
        val action = intent.getStringExtra(KEY_ACTION)
        try {
            mChat = intent.getParcelableExtra("chat")
        } catch (e: Exception) {
        }
        when {
            ACTION_JOIN == action -> joinMeet(intent)
            ACTION_JOIN_NOTIFICATION == action -> joinMeetFromNotification(intent)
            ACTION_START == action -> startMeet(intent)
            ACTION_SHOW == action -> {
                mMeetSessionController = mChatClientDelegate!!.createMeetSessionController(null)
                mMeetSession = mMeetSessionController!!.meetSession
                showMeetFragment()
            }
            else -> {
                Log.e(TAG, "unsupported action: $action")
                finishInMainThread()
            }
        }
    }

    private fun showMeetFragment() {
        mHandler.post {
            var fragment = supportFragmentManager.findFragmentById(R.id.meet_frame)
            if (fragment == null) {
                var config = MeetSessionConfig()
                config.isAutoStartVideoEnabled = true
                config.isAutoJoinVoIPEnabled = true
                mMeetSessionController?.setMeetSessionConfig(config)
                fragment = mMeetSessionController!!.createMeetFragment()
                supportFragmentManager.beginTransaction().add(R.id.meet_frame, fragment!!).commit()
            }
            mMeetSessionController!!.setSwitchToNormalViewActionListener { view, aVoid -> showMeet(view.context) }
            mMeetSessionController!!.setSwitchToFloatingViewActionListener { view, aVoid -> finish() }
            mMeetSession!!.setOnMeetEndedEventListener { finish() }
            setLoading(false)
        }
    }

    private fun finishInMainThread() {
        mHandler.post { finish() }
    }

    private fun joinMeet(intent: Intent) {
        val meet = intent.getParcelableExtra<Meet>(KEY_MEET)
        if (meet == null) {
            Log.e(TAG, "No chat found")
            finishInMainThread()
            return
        }


        mMeetRepo!!.joinMeet(meet.id, object : ApiCallback<MeetSession> {
            override fun onCompleted(meetSession: MeetSession) {
                Log.i(TAG, "Join meet successfully.")
                mMeetSession = meetSession
                mMeetSessionController = mChatClientDelegate!!.createMeetSessionController(mMeetSession)
                showMeetFragment()
            }

            override fun onError(errorCode: Int, errorMsg: String) {
                Log.e(TAG, "Failed to join meet, errorCode=$errorCode, errorMsg=$errorMsg")
                finishInMainThread()
            }
        })

    }

    private fun joinMeetFromNotification(intent: Intent) {
        val meetId = intent.getStringExtra(ACTION_JOIN_NOTIFICATION)
        if (meetId == null) {
            Log.e(TAG, "No chat found")
            finishInMainThread()
            return
        }
        mMeetRepo!!.joinMeet(meetId, object : ApiCallback<MeetSession> {
            override fun onCompleted(meetSession: MeetSession) {
                Log.i(TAG, "Join meet successfully.")
                mMeetSession = meetSession
                mMeetSessionController = mChatClientDelegate!!.createMeetSessionController(mMeetSession)
                showMeetFragment()
            }

            override fun onError(errorCode: Int, errorMsg: String) {
                Log.e(TAG, "Failed to join meet, errorCode=$errorCode, errorMsg=$errorMsg")
                finishInMainThread()
            }
        })

    }

    private fun startMeet(intent: Intent) {
        val topic = intent.getStringExtra(KEY_TOPIC)
        val userList = intent.getParcelableArrayListExtra<User>(KEY_USER_LIST)

        mMeetRepo!!.startMeetWithTopic(topic, mChat, object : ApiCallback<MeetSession> {
            override fun onCompleted(meetSession: MeetSession?) {
                Log.i(TAG, "Start meet successfully.")
                mMeetSession = meetSession

                mMeetSession?.inviteParticipants(userList, object : ApiCallback<Void> {
                    override fun onCompleted(result: Void?) {
                        Log.i(TAG, "Invite participants successfully.")
                    }

                    override fun onError(errorCode: Int, errorMsg: String?) {
                        Log.i(TAG, "Failed to invite participants, errorCode=$errorCode, errorMsg=$errorMsg")
                    }
                })
                mMeetSessionController = mChatClientDelegate!!.createMeetSessionController(mMeetSession)
                showMeetFragment()
            }

            override fun onError(errorCode: Int, errorMsg: String) {
                Log.e(TAG, "Failed to start meet, errorCode=$errorCode, errorMsg=$errorMsg")
                finishInMainThread()
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mMeetSession != null) {
            mMeetSession!!.cleanup()
        }
        if (mMeetSessionController != null) {
            mMeetSessionController!!.cleanup()
        }
        Log.d(TAG, "onCreate")
    }

    override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean {
        when (gesture) {
            GlassGestureDetector.Gesture.TAP ->
                // Response for TAP gesture
                return false
            GlassGestureDetector.Gesture.SWIPE_FORWARD ->
                // Response for SWIPE_FORWARD gesture
                return false
            GlassGestureDetector.Gesture.SWIPE_BACKWARD ->
                finish()
            else -> return false
        }
        return false
    }

    companion object {

        private const val TAG = "DEMO_MeetActivity"
        private const val KEY_ACTION = "action"
        private const val ACTION_JOIN = "join"
        private const val ACTION_JOIN_NOTIFICATION = "joinNotification"
        private const val ACTION_START = "start"
        private const val ACTION_SHOW = "show"
        private const val KEY_MEET = "meet"
        private const val KEY_TOPIC = "topic"
        private const val KEY_USER_LIST = "userList"

        fun joinMeet(ctx: Context, meet: Meet) {
            val intent = Intent(ctx, MeetActivity::class.java)
            intent.putExtra(KEY_ACTION, ACTION_JOIN)
            intent.putExtra(KEY_MEET, meet)
            ctx.startActivity(intent)
        }

        fun joinMeetNotification(ctx: Context, meet: String, uniqueId: String) {
            ChatClient.linkWithUniqueId(uniqueId!!, CLIENT_ID, CLIENT_SECRET, null, object :
                ApiCallback<ChatClientDelegate> {
                override fun onError(p0: Int, p1: String?) {
                    //runOnUiThread { Toast.makeText(this@MeetActivity, p1, Toast.LENGTH_SHORT).show() }
                    Log.e("TAG", "!!!!!!!!!!error: {p1}")
                }

                override fun onCompleted(ccd: ChatClientDelegate) {
                    val intent = Intent(ctx, MeetActivity::class.java)
                    intent.putExtra(KEY_ACTION, ACTION_JOIN_NOTIFICATION)
                    intent.putExtra(ACTION_JOIN_NOTIFICATION, meet)
                    ctx.startActivity(intent)
                }
            })
        }

        fun startMeet(ctx: Context, topic: String, userList: ArrayList<User>, chat: Chat) {
            val intent = Intent(ctx, MeetActivity::class.java)
            intent.putExtra(KEY_ACTION, ACTION_START)
            intent.putExtra(KEY_TOPIC, topic)
            intent.putExtra("chat", chat)
            intent.putParcelableArrayListExtra(KEY_USER_LIST, userList)
            ctx.startActivity(intent)
        }

        fun showMeet(ctx: Context) {
            val intent = Intent(ctx, MeetActivity::class.java)
            intent.putExtra(KEY_ACTION, ACTION_SHOW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ctx.startActivity(intent)
        }
    }
}