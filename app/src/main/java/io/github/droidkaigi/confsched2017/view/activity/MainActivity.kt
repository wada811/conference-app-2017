package io.github.droidkaigi.confsched2017.view.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.MenuItem
import io.github.droidkaigi.confsched2017.R
import io.github.droidkaigi.confsched2017.databinding.ActivityMainBinding
import io.github.droidkaigi.confsched2017.util.LocaleUtil
import io.github.droidkaigi.confsched2017.view.fragment.InformationFragment
import io.github.droidkaigi.confsched2017.view.fragment.MapFragment
import io.github.droidkaigi.confsched2017.view.fragment.SessionsFragment
import io.github.droidkaigi.confsched2017.view.fragment.SettingsFragment
import io.github.droidkaigi.confsched2017.view.helper.BottomNavigationViewHelper

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var sessionsFragment: Fragment? = null
    private var mapFragment: Fragment? = null
    private var informationFragment: Fragment? = null
    private var settingsFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleUtil.initLocale(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        component.inject(this)
        initView()
        initFragments(savedInstanceState)
    }

    private fun initView() {
        BottomNavigationViewHelper.disableShiftingMode(binding.bottomNav)
        binding.bottomNav.setOnNavigationItemSelectedListener { item: MenuItem ->
            binding.title.text = item.title
            item.isChecked = true
            when (item.itemId) {
                R.id.nav_sessions -> switchFragment(sessionsFragment!!, SessionsFragment.TAG)
                R.id.nav_map -> switchFragment(mapFragment!!, MapFragment.TAG)
                R.id.nav_information -> switchFragment(informationFragment!!, InformationFragment.TAG)
                R.id.nav_settings -> switchFragment(settingsFragment!!, SettingsFragment.TAG)
            }
            false
        }
    }

    private fun initFragments(savedInstanceState: Bundle?) {
        val manager = supportFragmentManager
        sessionsFragment = manager.findFragmentByTag(SessionsFragment.TAG)
        mapFragment = manager.findFragmentByTag(MapFragment.TAG)
        informationFragment = manager.findFragmentByTag(InformationFragment.TAG)
        settingsFragment = manager.findFragmentByTag(SettingsFragment.TAG)
        if (sessionsFragment == null) {
            sessionsFragment = SessionsFragment.newInstance()
        }
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
        }
        if (informationFragment == null) {
            informationFragment = InformationFragment.newInstance()
        }
        if (settingsFragment == null) {
            settingsFragment = SettingsFragment.newInstance()
        }
        if (savedInstanceState == null) {
            switchFragment(sessionsFragment!!, SessionsFragment.TAG)
        }
    }

    private fun switchFragment(fragment: Fragment, tag: String): Boolean {
        if (fragment.isAdded) {
            return false
        }
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        val currentFragment = manager.findFragmentById(R.id.content_view)
        if (currentFragment != null) {
            ft.detach(currentFragment)
        }
        if (fragment.isDetached) {
            ft.attach(fragment)
        } else {
            ft.add(R.id.content_view, fragment, tag)
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        // NOTE: When this method is called by user's continuous hitting at the same time,
        // transactions are queued, so necessary to reflect commit instantly before next transaction starts.
        manager.executePendingTransactions()
        return true
    }

    override fun onBackPressed() {
        if (switchFragment(sessionsFragment!!, SessionsFragment.TAG)) {
            binding.bottomNav.menu.findItem(R.id.nav_sessions).isChecked = true
            binding.title.text = getString(R.string.sessions)
            return
        }
        super.onBackPressed()
    }

    companion object {
        fun createIntent(context: Context?): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}