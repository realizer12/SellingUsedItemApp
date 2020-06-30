package com.example.leedonghun.sellinguseditemapp.Activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.R

/**
 * SellingUsedItemApp
 * Class: TermPagerFourthFragment.
 * Created by leedonghun.
 * Created On 2020-06-15.
 * Description:
 */
class MakeIdPagerFourthFragment :Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        //프래그먼트 뷰 연결
        val view: View = inflater.inflate(R.layout.term_pager_fourth_fragment, container, false)

        Log.v("check_app_runnig_status","TermPagerFourthFragment onCreateView 실행 됨")

        return view


    }

}