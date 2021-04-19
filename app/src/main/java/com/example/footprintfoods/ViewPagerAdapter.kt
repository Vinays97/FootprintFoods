package com.example.footprintfoods

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    // Setup Variables
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()
    // getCount function
    override fun getCount(): Int {
        return mFragmentList.size
    }
    // getItem function
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }
    // getPageTitle function
    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }
    // addFragment function
    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }
}