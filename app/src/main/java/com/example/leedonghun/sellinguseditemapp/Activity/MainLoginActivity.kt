package com.example.leedonghun.sellinguseditemapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.leedonghun.sellinguseditemapp.R


/** speakenglish
* Class: ApiService.
* Created by leedonghun.
* Created On 2019-01-10.
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





    }//onCreate() 끝


}//Main 끝
