package com.pivot.pivot360.pivoteye

import android.app.Activity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.pivot.pivot360.pivotglass.R

open class BaseActivity : AppCompatActivity() {
    private var mActionProgressItem: MenuItem? = null
    private var mIsLoading = false

//    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
//        mActionProgressItem = menu.findItem(com.pivot.pivot360.R.id.miActionProgress)
//        setLoading(mIsLoading)
//
//        return super.onPrepareOptionsMenu(menu)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(com.pivot.pivot360.R.menu.menu_main, menu)
//        if (supportActionBar != null) {
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        }
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//
//        if (id == com.pivot.pivot360.R.id.action_logout) {
//            logout()
//            return true
//        } else if (id == android.R.id.home) {
//            onBackPressed()
//            return true
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

    fun setLoading(isLoading: Boolean) {
        mIsLoading = isLoading
        if (mActionProgressItem != null) {
            mActionProgressItem!!.isVisible = mIsLoading
        }
    }

    fun AppCompatActivity.doFragmentTransaction(fragManager: FragmentManager = supportFragmentManager,
                                                @IdRes containerViewId: Int,
                                                fragment: Fragment,
                                                tag: String = "",
                                                @AnimatorRes enterAnimation: Int = 0,
                                                @AnimatorRes exitAnimation: Int = 0,
                                                @AnimatorRes popEnterAnimation: Int = 0,
                                                @AnimatorRes popExitAnimation: Int = 0,
                                                isAddFragment: Boolean = false,
                                                isAddToBackStack: Boolean = true,
                                                allowStateLoss: Boolean = false) {

        val fragmentTransaction = fragManager.beginTransaction()
                .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)

        if (isAddFragment) {
            fragmentTransaction.add(containerViewId, fragment, tag)
        } else {
            fragmentTransaction.replace(containerViewId, fragment, tag)
        }

        if (isAddToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }

        if (allowStateLoss) {
            fragmentTransaction.commitAllowingStateLoss()
        } else {
            fragmentTransaction.commit()
        }
    }


    protected fun getCurrentFragment(): Fragment? {
        val frag = supportFragmentManager.findFragmentById(R.id.container)
        return if (frag == null) return null else frag
    }

    protected fun clearBackStack() {
        for (i in 0..supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStack()
        }
    }

//    fun logout() {
//        PreferenceUtil.removeUser(this)
//        PreferenceUtil.removeFirebaseTokenToken(this)
//        setLoading(true)
//        ChatClient.unlink(object : ApiCallback<Void?> {
//            override fun onCompleted(result: Void?) {
//
//                //Log.i(TAG, "Unlink Moxtra account successfully.")
//                setLoading(false)
//                doAsync {
//                    FirebaseMessaging.getInstance().isAutoInitEnabled = false
//                    FirebaseInstanceId.getInstance().deleteInstanceId()
//                }
//                PreferenceUtil.removeUser(this@BaseActivity)
//                PreferenceUtil.removeAccessToken(this@BaseActivity)
//                val intent = Intent(applicationContext, LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//
//
//            }
//
//            override fun onError(errorCode: Int, errorMsg: String?) {
//                //Log.e(TAG, "Failed to unlink Moxtra account, errorCode=$errorCode, errorMsg=$errorMsg")
//                setLoading(false)
//            }
//        })
//
//    }


    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}