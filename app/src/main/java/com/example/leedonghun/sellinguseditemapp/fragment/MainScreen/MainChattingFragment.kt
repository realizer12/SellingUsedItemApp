package com.example.leedonghun.sellinguseditemapp.fragment.MainScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.R

/**
 * SellingUsedItemApp
 * Class: MainChattingFragment.
 * Created by leedonghun.
 * Created On 2020-09-01.
 * Description:
 *
 *로그인 후 보이는 메인 화면에서
 *채팅 리스트를 보여주는  화면이다.
 *
 */
class MainChattingFragment:Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.main_chatting_fragment,container,false)


        return view
    }



}