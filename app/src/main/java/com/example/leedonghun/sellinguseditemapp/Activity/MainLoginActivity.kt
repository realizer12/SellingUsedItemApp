package com.example.leedonghun.sellinguseditemapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.example.leedonghun.sellinguseditemapp.R
import kotlinx.android.synthetic.main.main_login_activity.*


/**
* SellingUsedItemApp
* Class: MainLoginActivity.
* Created by leedonghun.
* Created On 2020-06-08.
* Description:
*
*  splash 엑티비티에서  로고 보여주고,  로그인이  안된 상태라면  현재 엑티비티로 넘어온다.
 * sns 로그인 과 이메일 로그인 엑티비티로 넘어갈수 있으며,
 * 회원가입 엑티비티로도 넘어갈수 있다.
 * */
class MainLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_login_activity)
        Log.v("check_app_runnig_status","MainLoginActivity onCreate() 실행됨")


        //glide 라이브러리를 통해 중고마켓 gif 로고 파일을 넣어줌.
        Glide.with(this).load(R.drawable.main_login_activity__gif_logo).into(imgView_for_app_logo_gif)
        





    }//onCreate() 끝


}//Main 끝
