package com.example.leedonghun.sellinguseditemapp.Activity

import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.leedonghun.sellinguseditemapp.R
import kotlinx.android.synthetic.main.email_login_activity.*

/**
 * SellingUsedItemApp
 * Class: EmailLoginActivity.
 * Created by leedonghun.
 * Created On 2020-06-10.
 * Description: 이메일 로그인을 하는 엑티비티이다.
 * 기존에 가입한 로그인 이메일을 통해서 로그인을 하거나, 아이디나  비밀 번호를 찾는 엑티비티로
 * 넘어가는 버튼이 있다.
 * 그리고 비밀 번호 의  경우  눈 아이콘을  눌러서  내가 쓴 비밀 번호를 볼수 있는 처리가 되어있다.
 */
class EmailLoginActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_login_activity)
        Log.v("check_app_runnig_status","EmailLoginActivity의 onCreate() 실행됨")



        //뒤로 가기 버튼 클릭
        arrow_btn_for_back_to_login_activity.setOnClickListener {
            Log.v("check_app_runnig_status","뒤로가기 버튼 클릭됨-> MainLoginActivity로 이동 됨")

            //현재 엑티비티 종료 시킴
            finish()
        }




        //비밀번호 보여짐 상태 아이콘  클릭 이벤트
        frame_for_handle_pwd_shown.setOnClickListener {

            Log.v("check_app_runnig_status","비밀번호 보여짐 상태여부  설정 됨")

            //보이기 여부 설정하는 함수 1-1
            show_pwd_shown_status(img_for_pwd_shown,img_for_pwd_not_shown)
        }


        //로그인 버튼 눌렀을때
        btn_for_login.setOnClickListener {
            Log.v("check_app_runnig_status","로그인 버튼 눌림")
            
        }

        //아이디 찾기 버튼 눌렸을때
        btn_for_find_id.setOnClickListener {
            Log.v("check_app_runnig_status","아이디 찾기 버튼 눌림")

        }

        //비밀번호 찾기 버튼 눌림.
        btn_for_find_pwd.setOnClickListener {
            Log.v("check_app_runnig_status","비밀번호 찾기 버튼 눌림")

        }


    }//onCreate()끝



    //이메일 로그인 비밀번호  보이기 여부 설정 1-1
    fun show_pwd_shown_status(show_icon:ImageView,not_show_icon:ImageView){

        //보여지는 상태로 체크 되어있을 경우
        if(show_icon.isVisible){

            //아이콘 visible 처리
            show_icon.visibility=View.INVISIBLE
            not_show_icon.visibility=View.VISIBLE

            //에딧 텍스트 -패스워드 보여지게 처리 ->1234
            editxt_for_add_login_password.setTransformationMethod(PasswordTransformationMethod.getInstance())

        }else{//안보여짐 상태로 체크된 경우

            //아이콘 visible 처리
            show_icon.visibility=View.VISIBLE
            not_show_icon.visibility=View.INVISIBLE

            //에딧 텍스트 패스워드 안보여지게 처리 -> ****
            editxt_for_add_login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        }

        //icon보이는 여부 바뀔때 패스워드를 하나라도 썼을경우 ->( 0>조건 넣음 )focus 가 마지막 위치를  가르치고 있어야 하낟.
        //아래 setSelection(edittext 길이-> 마지막 위치) 처리를 해주지 않으면,  계속해서 포커스가 처음위치로 가지게 된다.
        if(editxt_for_add_login_password.length()>0){
            editxt_for_add_login_password.setSelection(editxt_for_add_login_password.length())
        }

    }//show_pwd_shown_status끝



}//EmailLoginActivity 클래스 끝