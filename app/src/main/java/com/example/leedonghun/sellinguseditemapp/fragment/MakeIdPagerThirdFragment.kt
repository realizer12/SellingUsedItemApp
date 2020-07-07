package com.example.leedonghun.sellinguseditemapp.Activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.Interface.SendPhonNumberToAnotherFragment
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Singleton.auth_phon_num
import kotlinx.android.synthetic.main.term_pager_third_fragment.view.*


/**
 * SellingUsedItemApp
 * Class: TermPagerThirdFragment.
 * Created by leedonghun.
 * Created On 2020-06-15.
 * Description:
 */
class MakeIdPagerThirdFragment(private val check_sns_or_email:Int,context: Context) :Fragment() {


    //inputmethodmanager ->  소프트 키보드 관련 조작 담당
    val mInputMethodManager:InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {


        //프래그먼트 뷰 연결
        val view: View = inflater.inflate(R.layout.term_pager_third_fragment, container, false)

        Log.v("check_app_runnig_status","TermPagerThirdFragment onCreateView 실행 됨 -> sns 로그인 여부 0=sns , 1=이메일    결과=> $check_sns_or_email")


        //배경 눌렀을때도  키보드 내려갈수 있게 만듬
        view.entire_layout_of_termpager_third_fragment.setOnClickListener {

            mInputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0);

            Toast.makeText(activity, auth_phon_num.phonnumber,Toast.LENGTH_SHORT).show()

        }


        //그래서 해당 구별 값을 생성자를 통해 보내줌. 0-> sns 회원가입 1-> 이메일 회원가입
        if(check_sns_or_email==0){//-> sns  회원 가입일때는  닉네임만  적으면 됨.

            //로그인 이메일, 비밀번호, 비밀번호 체크  모두 gone
            view.txt_for_make_new_login_email.visibility=View.GONE
            view.txt_for_make_login_pwd.visibility=View.GONE
            view.linearlayout_for_add_new_login_email.visibility=View.GONE
            view.linearlayout_for_add_new_login_pwd.visibility=View.GONE
            view.linearlayout_for_double_check_new_login_pwd.visibility=View.GONE

        }else if(check_sns_or_email==1){//이메일 회원가입일때



        }


        return view

    }//oncreateview 끝



    override fun onPause() {
        super.onPause()
        Log.v("check_app_runnig_status","TermPagerThirdFragment onPause 실행 됨")


        //맨처음 포커싱 해서  키보드 올려주는  editext만  hide 를  적용
        //왜냐면, 애네는  inputmanager로  키보드를  올려놓은 상태이기 때문에
        //pause 단계에서도  그대로 유지가 되어있는 중이다.
        //그래서 혹시나  맨처음  키보드 올린상태에서 더이상 키보드 전환 과정이 없다면,
        //아래 코드로  적용  hide가 적용된다.
        if(check_sns_or_email==0){//sns 로그인시 -> 닉네임에  자동 포커스 되어있다.

            mInputMethodManager.hideSoftInputFromWindow(view!!.editxt_for_add_new_nickname.windowToken, 0);

        }else if(check_sns_or_email==1){//이메일 로그인시  -> 로그인 이메일 적는 곳에  자동 포커스 되어있음.

            mInputMethodManager.hideSoftInputFromWindow(view!!.editxt_for_make_new_login_email.windowToken, 0);

        }


    }



}//프래그먼트 끝.