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
 * Class: MainMyExchangeFragment.
 * Created by leedonghun.
 * Created On 2020-09-01.
 * Description:
 *
 * 로그인 후 보여지는  중고마켓 메인 화면 중에서
 * 내거래 파트를 보여주는 화면이다.
 */
class MainMyExchangeFragment:Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view=inflater.inflate(R.layout.main_my_exchange_fragment,container,false)
        
        Logger.v("실행")


        return view
    }




}