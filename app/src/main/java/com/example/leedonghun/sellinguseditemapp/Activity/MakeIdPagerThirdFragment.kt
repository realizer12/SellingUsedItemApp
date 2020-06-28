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
 * Class: TermPagerThirdFragment.
 * Created by leedonghun.
 * Created On 2020-06-15.
 * Description:
 */
class MakeIdPagerThirdFragment(private val check_sns_or_email:Int) :Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //프래그먼트 뷰 연결
        val view: View = inflater.inflate(R.layout.term_pager_third_fragment, container, false)

        Log.v("check_app_runnig_status","TermPagerThirdFragment onCreateView 실행 됨 -> sns 로그인 여부 => $check_sns_or_email")


        //그래서 해당 구별 값을 생성자를 통해 보내줌. 0-> sns 회원가입 1-> 이메일 회원가입
        if(check_sns_or_email==0){//-> sns  회원 가입일때는  닉네임만  적으면 됨.




        }else if(check_sns_or_email==1){//이메일 회원가입일때




        }


        return view

    }

}