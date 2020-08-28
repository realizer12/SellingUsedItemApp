package com.example.leedonghun.sellinguseditemapp.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Util.KeyboardVisibilityUtils
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import kotlinx.android.synthetic.main.find_login_email_activity.*
import java.security.Key

/**
 * SellingUsedItemApp
 * Class: FindLoginEmailActivity.
 * Created by leedonghun.
 * Created On 2020-08-28.
 * Description:
 *
 * 로그인할때 사용하는 이메일을 찾기 위한
 * 엑티비티이다.
 * sns 인증을 통해  이메일을 찾을수 있다.
 *
 */
class FindLoginEmailActivity:AppCompatActivity() {

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils//키보드 visible 판단해주는  클래스  1-0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_login_email_activity)


        keyboardVisibilityUtils= KeyboardVisibilityUtils()//1-0


        //인증번호 입력 관련 뷰들  초기 설정  INVISIBLE
        txt_for_show_remain_count_of_input_code.visibility=View.INVISIBLE
        linearlayout_for_input_certification_sms_code.visibility=View.INVISIBLE


        complet_btn.setOnClickListener(clickListener)//엑티비티 완료 버튼 클릭  1-1
        find_email_login_activity_container.setOnClickListener(clickListener)//배경 클릭 1-3

        btn_for_get_certification_code.setOnClickListener(clickListener)//인증번호 받기 버튼 클릭 1-2
        btn_for_input_certification_code_complete.setOnClickListener(clickListener)//인증 코드 입력 완료 버튼 1-4



    }//onCreate()끝




    //뷰 클릭이벤트
    val clickListener=View.OnClickListener {

        when(it){

            complet_btn->{//1-1

                Logger.v("완료 버튼 클릭됨")
                finish()
            }


            btn_for_get_certification_code->{//1-2
                Logger.v("인증 번호 받기 버튼 클릭됨")

            }


            find_email_login_activity_container->{//1-3

                Logger.v("배경화면 클릭됨")
                keyboardVisibilityUtils.hideKeyboard(this,it)

            }

            btn_for_input_certification_code_complete->{//1-4
                Logger.v("인증번호 입력 완료 버튼 클릭 ")
            }

        }//when끝

    }//클릭 리스너 끝



}