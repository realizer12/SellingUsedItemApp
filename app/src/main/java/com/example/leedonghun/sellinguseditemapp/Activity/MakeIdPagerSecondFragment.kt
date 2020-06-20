package com.example.leedonghun.sellinguseditemapp.Activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.R
import kotlinx.android.synthetic.main.term_pager_second_fragment.*

/**
 * SellingUsedItemApp
 * Class: TermPagerSecondFragment.
 * Created by leedonghun.
 * Created On 2020-06-15.
 *
 * Description: 회원가입 뷰페이져에서  두번째 뷰페이져이다.
 * 이곳에서는 핸드폰 번호를 쓰고  인증 번호를  문자로 받고,
 * 인증번호를 정확히 적었을때  다음으로 넘어갈수 있다.
 */
class MakeIdPagerSecondFragment :Fragment() {

    //로그 쓸때  편하게 쓰기 위해서..
    val fragment_name_for_Log:String="TermPagerSecondFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {


        //프래그먼트 뷰 연결
        val view: View = inflater.inflate(R.layout.term_pager_second_fragment, container, false)

        Log.v("check_app_runnig_status",fragment_name_for_Log+"의 onCreateView 실행 됨")



        //인증 번호 받기 버튼
        btn_for_get_certification_code.setOnClickListener {
            Log.v("check_app_runnig_status",fragment_name_for_Log+"의 휴대폰 인증번호 받기 버튼 눌림")

        }


        //문자로 받은 인증 번호 확인 버튼
        btn_for_input_certification_code_complete.setOnClickListener {
            Log.v("check_app_runnig_status",fragment_name_for_Log+"의 인증번호 입력완료 버튼 눌림.")


        }



        return view
    }

}