package com.example.leedonghun.sellinguseditemapp.fragment.MainScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Util.Logger

/**
 * SellingUsedItemApp
 * Class: MainHomeFragment.
 * Created by leedonghun.
 * Created On 2020-09-01.
 * Description:
 *
 * 중고마켓 메인 화면중에서
 * HOME에  해당 하는
 */
class MainHomeFragment :Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view:View=inflater.inflate(R.layout.main_home_fragment,container,false)
        Logger.v("실행")

        return view
    }//oncreateview 끝



}