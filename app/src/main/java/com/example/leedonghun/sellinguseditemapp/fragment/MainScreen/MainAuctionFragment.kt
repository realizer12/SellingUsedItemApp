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
 * Class: MainAuctionFragment.
 * Created by leedonghun.
 * Created On 2020-09-01.
 * Description:
 *
 * 메인 화면 중에서 경매장
 * 관련  화면을 보여준다.
 */
class MainAuctionFragment:Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.main_auction_fragment,container,false)
        Logger.v("실행")


        return view
    }

}