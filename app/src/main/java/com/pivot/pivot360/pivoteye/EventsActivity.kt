package com.pivot.pivot360.pivoteye

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.pivot.pivot360.pivotglass.R


class EventsActivity : BaseActivity() {
    lateinit var fragment: Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        //viewPager.currentItem = 0
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.title = "My Jobs"
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction().replace(R.id.container, MyEventsFragment.newInstance()).commit()



        //doFragmentTransaction(containerViewId = R.id.container, fragment = MyEventsFragment())


    }

}
