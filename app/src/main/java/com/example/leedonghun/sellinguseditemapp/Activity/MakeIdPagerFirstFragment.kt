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
 *
 * Class: TermPagerFirstFragment.
 * Created by leedonghun.
 * Created On 2020-06-15.
 *
 * Description:  MakeNewLoginIdActivity에서 뷰페이저에서 맨처음
 * 보이는  프래그먼트 회원 정보를  받아가는 것에 대한  동의를  받기위한
 * 부분이다.  전체 동의를  해야 다음 버튼이 활성화 된다.
 *
 *
 */
class MakeIdPagerFirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

      //프래그먼트 뷰 연결
      val view: View = inflater.inflate(R.layout.term_pager_first_fragment, container, false)

      Log.v("check_app_runnig_status","TermPagerFirstFragment의 onCreateView 실행 됨")

      return view
    }


}