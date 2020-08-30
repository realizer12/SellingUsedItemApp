package com.example.leedonghun.sellinguseditemapp.Activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.leedonghun.sellinguseditemapp.Dialog.LoadingDialog
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Util.KeyboardVisibilityUtils
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import kotlinx.android.synthetic.main.find_login_pws_activity.*
import kotlinx.coroutines.Job
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * SellingUsedItemApp
 * Class: FindLoginPwsActivity.
 * Created by leedonghun.
 * Created On 2020-08-30.
 * Description:
 *
 * 패스워드를  찾기위한 엑티비티
 * 이메일과  핸드폰 번호를 받아서
 * 측정한다.
 *
 * 이메일로그인의 경우는 비밀번호가 필요하므로
 * 새로운 비밀번호 입력 뷰가 나오지만,
 * 그외  sns 로그인의 경우는 토스트로
 * sns 로그인임으로 비밀번호를 해당 플랫폼에서 알아내야한다고
 * 알려준다.
 */
class FindLoginPwsActivity :AppCompatActivity() {

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils//키보드 visible 판단해주는  클래스 1-0

    //sms 인증시 띄어줄  커스텀 다이얼로그 -로딩용
    lateinit var loadingDialog: LoadingDialog//2-2

    //뷰에 흔들림 효과를 주는 애니메이션
    private lateinit var shake: Animation//2-1

    //inputmethodmanager ->  소프트 키보드 관련 조작 담당
    lateinit var mInputMethodManager: InputMethodManager//2-4

    //타이머  코루틴 job
    private lateinit var timer_job: Job  // 2-3

    //현재 sms 인증 번호 요청중인지 여부를 체크한다. -> false =  요청 전 상태,  true = 현재 sms 인증코드 요청중인 상태
    var check_sms_request_ing:Boolean = false

    //인증키 입력이 맞을 경우
    private var check_auth_correct:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_login_pws_activity)
        Logger.v("실행")


        mInputMethodManager= this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager//2-4
        timer_job = Job()//2-3
        keyboardVisibilityUtils=KeyboardVisibilityUtils()//1-0
        loadingDialog=LoadingDialog(this)//2-2
        shake= AnimationUtils.loadAnimation(this,R.anim.shake)//2-1



        back_btn.setOnClickListener(clickListener)//뒤로가기 버튼 클릭  1-1
        complet_btn.setOnClickListener(clickListener)//완료 버튼 클릭 1-2
        find_pws_activity_container.setOnClickListener(clickListener)//배경화면 클릭 1-3
        btn_for_get_certification_code.setOnClickListener(clickListener)//인증 번호 받기 버튼 클릭 1-4

    }//oncreate() 끝


    //클릭 이벤트들 모음
    private val clickListener= View.OnClickListener {

        when(it){

            back_btn->{//1-1
                Logger.v("뒤로가기 이미지 버튼 클릭됨")
                finish()
            }


            complet_btn->{//1-1
                Logger.v("완료 버튼 클릭됨")
                finish()
            }

            find_pws_activity_container->{//1-3
                Logger.v("배경화면 클릭됨 -> 키보드 내리기 진행")
                keyboardVisibilityUtils.hideKeyboard(this,it)
            }

            btn_for_get_certification_code->{//1-4
                Logger.v("인증번호 받기 버튼 클릭")



                if(check_email_regex_function(editext_for_write_use_email.text.toString())){

                    if(check_phone_regex_function(editxt_for_add_phone_number.text.toString())){


                    }else{
                        Toast.makeText(this,"핸드폰 형식 챙기세요",Toast.LENGTH_SHORT).show()
                        make_clear_editext_with_wrong_value(editxt_for_add_phone_number)
                    }

                }else{
                      Toast.makeText(this,"이메일 형식 챙기세요!!",Toast.LENGTH_SHORT).show()
                    make_clear_editext_with_wrong_value(editext_for_write_use_email)
                }


            }

        }

    }//clickListener 끝

    //이메일 정규식 틀리거나, 핸드폰 특성 틀릳때
    //애니메이션으로 editext shake하고  text clear 해줌
    //동일한 처리여서  메소드 하나 따로 만듬.
    fun make_clear_editext_with_wrong_value(editText: EditText){

        //틀렸다는 애니메이션 주고, clear 한다음에 focus 처리
        editText.startAnimation(shake)
        editText.requestFocus()
        editText.text.clear()
    }


    //핸드폰 번호  정규식 체크
    fun check_phone_regex_function(phone_number:String):Boolean{

        val expression:String ="^01(?:0|1|[6-9])?(\\d{3}|\\d{4})?(\\d{4})$"
        val pattern: Pattern = Pattern.compile(expression)
        val matcher:Matcher=pattern.matcher(phone_number)

     return matcher.matches()
    }


    //이메일 형식  체크 하는 기능
    fun check_email_regex_function(email:String):Boolean{

        //이메일 형식 맞는 지 확인 하는 정규식
        val expression:String ="^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"

        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)

        return matcher.matches()
    }




}