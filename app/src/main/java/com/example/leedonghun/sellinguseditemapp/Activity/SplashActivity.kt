package com.example.leedonghun.sellinguseditemapp.Activity

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.leedonghun.sellinguseditemapp.PrivateInfo.ServerIp
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import com.example.leedonghun.sellinguseditemapp.Singleton.SnsEmailValue
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.Call
import okhttp3.ResponseBody
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

/**
 * SellingUsedItemApp
 * Class: SplashActivity.
 * Created by leedonghun.
 * Created On 2020-06-08.
 *
 * Description: 맨처음에  시작 될때  보이게 되는
 *중고나라  로고가 style 태마로  적용된 엑티비티이다.
 * 앱  launcher 엑티비티로 설정해 놓았으며,  로고를 0.8 초 보여주고
 * 메인이나(로그인 기록 존재 시) ,  메인 로그인 화면(로그인 기록 x) 으로 넘어가진다.
 */
class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.v("실행")

        //우선 cold start 부분에서  바로 로고 가  뜨게 만들었음.
        //하지만,  이부분에서 로고가  너무 빨리 사라져서  다음 메인 화면 시작하는데에  딜레이(0.8초)를  줌.
        //참고한  부분은 페이스북 앱이랑, 네이버 임.  애네도 coldstart 를  없애고  딜레이를 조금씩 줘서 로고를 보여줌.
        Handler().postDelayed({


           //스플래쉬  엑티비티 실행후 ->   바로  메인으로  가짐.
           //그다음  현재 스플래쉬 엑티비티는  종료 시켜줌.
           val intent_to_go_main = Intent(this, MainLoginActivity::class.java)
           startActivity(intent_to_go_main)


            //다음  실행되는 엑티비티가  fade in fede out  효과를 내게 해준다.
            //기존에  오른쪽에서 새로운 엑티비티가 날라오는거  싫어서 넣음.
            overridePendingTransition(R.anim.fadein,R.anim.fadeout)
            finish()

        }, 800)


    }//onCreate()끝


}