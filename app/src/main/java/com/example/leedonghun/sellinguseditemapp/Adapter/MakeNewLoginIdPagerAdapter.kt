package com.example.leedonghun.sellinguseditemapp.Adapter

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.leedonghun.sellinguseditemapp.Activity.MakeIdPagerFirstFragment
import com.example.leedonghun.sellinguseditemapp.Activity.MakeIdPagerFourthFragment
import com.example.leedonghun.sellinguseditemapp.Activity.MakeIdPagerSecondFragment
import com.example.leedonghun.sellinguseditemapp.Activity.MakeIdPagerThirdFragment

/**
 * SellingUsedItemApp
 *
 * Class: MakeNewLoginIdPagerAdapter.
 * Created by leedonghun.
 * Created On 2020-06-15.
 *
 * Description: MakeNewLoginIdActivity 에서  뷰페이져 부분에  프래그먼트를  사용한다.
 * 여기는 각 뷰페이져 포지션에 프래그먼트를 생성해주는 adapter 이다.
 */
class MakeNewLoginIdPagerAdapter(fm:FragmentActivity, private val check_sns_or_email:Int,private val context: Context) : FragmentStateAdapter(fm) {



   //각 포지션 별 프래그먼트 생성
    override fun createFragment(position: Int): Fragment {

           //포지션 별  해당  프래그먼트  실행 시켜줌.
           return when (position) {

               0 -> MakeIdPagerFirstFragment()
               1 -> MakeIdPagerSecondFragment(context)

               //여기서 sns 로그인의 경우는 로그인 이메일이랑 비밀 번호가 필요없음
               //그래서 해당 구별 값을 생성자를 통해 보내줌. 0-> sns 회원가입 1-> 이메일 회원가입
               2 -> MakeIdPagerThirdFragment(check_sns_or_email)
               3 -> MakeIdPagerFourthFragment()
               else -> {
                   error("이메일 회원가입에서 뷰페이져의 프래그먼트 포지션이 잘 못들어옴")
               }
           }
    }//createFragment 끝

    //회원가입 용 뷰페이져  페이지수는 4개임
    override fun getItemCount(): Int {

            return 4
    }//getItemCount 끝

}