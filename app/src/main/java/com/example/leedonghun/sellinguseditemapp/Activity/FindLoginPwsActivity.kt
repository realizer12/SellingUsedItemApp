package com.example.leedonghun.sellinguseditemapp.Activity

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.leedonghun.sellinguseditemapp.Dialog.LoadingDialog
import com.example.leedonghun.sellinguseditemapp.PrivateInfo.ServerIp
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.ApiService
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import com.example.leedonghun.sellinguseditemapp.Util.KeyboardVisibilityUtils
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import com.example.leedonghun.sellinguseditemapp.Util.MakePassWordSecurity
import kotlinx.android.synthetic.main.find_login_pws_activity.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    //retrofit 객체
    lateinit var retrofitClient: RetrofitClient

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

    //현재 엑티비티  사용 상태  true false 여부
    private var check_activity_working:Boolean=false


    //비밀번호 정규식 입력 판단 결과  default 값 false
    var check_pws_regex_result:Boolean = false

    //패스워드랑  패스워드 체크 부분  같은지 여부 체크
    private var check_double_password_check:Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_login_pws_activity)
        Logger.v("실행")

        auth_view_visible(false)
        password_view_visible(false)


        mInputMethodManager= this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager//2-4
        keyboardVisibilityUtils=KeyboardVisibilityUtils()//1-0
        loadingDialog=LoadingDialog(this)//2-2
        shake= AnimationUtils.loadAnimation(this,R.anim.shake)//2-1



        back_btn.setOnClickListener(clickListener)//뒤로가기 버튼 클릭  1-1
        complet_btn.setOnClickListener(clickListener)//완료 버튼 클릭 1-2
        find_pws_activity_container.setOnClickListener(clickListener)//배경화면 클릭 1-3
        btn_for_get_certification_code.setOnClickListener(clickListener)//인증 번호 받기 버튼 클릭 1-4
        btn_for_input_certification_code_complete.setOnClickListener(clickListener)//인증번호 체크 버튼 클릭 1-5


        //패스워드 관련 textwatcher
        editxt_for_add_new_pwd.addTextChangedListener(textWatcher)
        editxt_for_double_check_new_pwd.addTextChangedListener(textWatcher)



    }//oncreate() 끝


    //클릭 이벤트들 모음
    private val clickListener= View.OnClickListener {

        when(it){

            back_btn->{//1-1
                Logger.v("뒤로가기 이미지 버튼 클릭됨")
                onBackPressed()
            }


            complet_btn->{//1-1
                Logger.v("완료 버튼 클릭됨")

                //패스워드 정규식 및  패스워드 확인 값과  일치할때
                if(check_pws_regex_result && check_double_password_check){

                    val email=editext_for_write_use_email.text.toString()
                    val password=editxt_for_add_new_pwd.text.toString()

                    //비밀번호 변경 진행
                    change_password(password = password,login_email = email)

                }else{

                    onBackPressed()
                }

            }

            find_pws_activity_container->{//1-3
                Logger.v("배경화면 클릭됨 -> 키보드 내리기 진행")
                keyboardVisibilityUtils.hideKeyboard(this,it)
            }

            btn_for_get_certification_code->{//1-4
                Logger.v("인증번호 받기 버튼 클릭")

                val phone_number=editxt_for_add_phone_number.text.toString()//핸드폰 번호
                val email=editext_for_write_use_email.text.toString()//이메일

                //이메일 정규식이 맞을떄
                if(check_email_regex_function(email = email)){

                    //핸드폰 번호 정규식 및 이메일 정규식 모두 맞음
                    if(check_phone_regex_function(phone_number = phone_number)){

                        Logger.v("이메일 핸드폰 정규식 모두 맞음  인증번호 보내기 진행")
                        send_sms_auth_code(phone_number = phone_number,email = email)
                        

                    }else{//핸드폰 번호 정규식 틀릴때
                        Toast.makeText(this,R.string.string_for_wrong_regex_of_phone,Toast.LENGTH_SHORT).show()
                        make_clear_editext_with_wrong_value(editxt_for_add_phone_number)
                    }

                }else{//이메일 정규식이 틀릴때
                      Toast.makeText(this,R.string.string_for_wrong_regex_of_email,Toast.LENGTH_SHORT).show()
                      make_clear_editext_with_wrong_value(editext_for_write_use_email)
                }


            }

            
            
            btn_for_input_certification_code_complete->{//1-5
                
                Logger.v("인증번호 확인 버튼 클릭됨")
                check_sms_auth_code(editxt_for_add_phone_number.text.toString(),editxt_for_add_certification_code.text.toString())
         
            }

        }

    }//clickListener 끝



    //받은 인증 코드 맞는지 확인
    fun check_sms_auth_code(phone_number: String,auth_key:String){


        if(auth_key.length==5 && timer_job.isActive){//인증키가  5개이고, 타이머가  작동하고 있을 경우

            Logger.v("인증번호 5자리 맞음")

            retrofitClient= RetrofitClient(ServerIp.baseurl)
            retrofitClient.apiService.send_sms_auth_key(phone_number,2,auth_key)
                .enqueue(object :Callback<ResponseBody>{

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                        //인증키 체크 결과
                        val check_auth_key_result:String?=response.body()?.string()
                        

                        if(check_auth_key_result == "1"){//성공적으로 끝났을때

                            Logger.v("회원가입용  인증키  체크 결과 -> 인증 성공 email값 -> $check_auth_key_result")


                            //입련된 인증번호  인증 성공시, 더이상  입력할께 없으므로,  키보드 없애주고,  editexxt 에 포커스 clear 해준다.
                            mInputMethodManager.hideSoftInputFromWindow(editxt_for_add_phone_number.windowToken, 0)
                            editxt_for_add_certification_code.clearFocus()


                            timer_job.cancel()//타이머코루틴 job 취소

                            txt_for_show_remain_count_of_input_code.text="인증 됨"
                            txt_for_show_remain_count_of_input_code.setTextColor(Color.GREEN)


                            //인증되었으니까  editext 입력 못하게 막음.
                            editxt_for_add_certification_code.isEnabled=false

                            //인증이 된 경우니까 초록색으로 인증됨을 가시적으로 보여준다.
                            linearlayout_for_input_certification_sms_code.setBackgroundResource(R.drawable.custom_view_radius_with_green_bacground)



                            password_view_visible(true)


                            //인증 완료 여부 true로
                            check_auth_correct=true

                        }else if(check_auth_key_result.equals("2")){//인증키가 틀린경우
                            Logger.v("회원가입용  인증키  체크 결과 -> 인증 실패")

                            editxt_for_add_certification_code.requestFocus()
                            editxt_for_add_certification_code.text.clear()
                            linearlayout_for_input_certification_sms_code.startAnimation(shake)

                            Toast.makeText(this@FindLoginPwsActivity,"인증 코드가 틀립니다!",Toast.LENGTH_SHORT).show()


                        }else if(check_auth_key_result.equals("3")){//해당 값이 존재 하지 않을때
                            Logger.v("회원가입용  인증키  체크 결과 -> 해당 번호의 데이터 존재하지 않음")

                            Toast.makeText(this@FindLoginPwsActivity,"인증 코드를 한번더 요청해 보세요",Toast.LENGTH_SHORT).show()

                        }else if(check_auth_key_result.equals("4")){//조회 커리문 실패시
                            Logger.v("회원가입용  인증키  체크 결과 -> 조회문 실패")

                            Toast.makeText(this@FindLoginPwsActivity,"서버 인증 조회 에러",Toast.LENGTH_SHORT).show()

                        }else{

                            Logger.v("회원가입용  인증키  체크 결과 -> 인증상태 1(인증함)으로 업데이트 실패")
                            Toast.makeText(this@FindLoginPwsActivity,"서버 에러 error: 232",Toast.LENGTH_SHORT).show()

                        }

                    }


                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                        Logger.v("회원가입용  인증키  체크 실패 결과 ->  ${t.message}")
                    }


                })



        }else if(timer_job.isActive && auth_key.length!=5){// (타이머 작동중) 인증키는 5자리인데  그게 아닌경우는  토스트로 알림

            Toast.makeText(this,"인증키는 5자리입니다.",Toast.LENGTH_SHORT).show()
            editxt_for_add_certification_code.requestFocus()
            editxt_for_add_certification_code.text.clear()
            linearlayout_for_input_certification_sms_code.startAnimation(shake)

        }else if(!timer_job.isActive){

            //인증이 되어있지 않은 경우에는 아래 토스트와 애니메이션 진행
            //인증이 된 경우는 버튼 눌러도 아무 변화 없음으로 남기자.
            if(!check_auth_correct){

                Toast.makeText(this,"인증키 요청을 다시 하세요!",Toast.LENGTH_SHORT).show()
                btn_for_get_certification_code.startAnimation(shake)
            }

        }




    }




    //뒤로가기 버튼 클릭 이벤트
    override fun onBackPressed() {

        if(check_activity_working){//count가  진행중일때는   한번더  alert 로 물어본다.
            Logger.v(" 아직  count 진행중이므로,  dialog 띄움")

            AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(R.string.string_for_back_btn_click)
                .setPositiveButton(R.string.string_for_yes){dialog_for_finish_activity, which ->

                    timer_job.cancel()
                    finish()
                }
                .setNegativeButton(R.string.string_for_no){dialog_for_finish_activity,which->

                    dialog_for_finish_activity.dismiss()

                }.show()


        }else{//count진행 아닐때는  바로 finish

            Logger.v("완료 버튼 클릭됨 -> 엑티비티 종료 가능")
            finish()
        }

    }



    //패스워드 체크용 텍스트 watcher
    val textWatcher:TextWatcher = object:TextWatcher{

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            Logger.v("패스워드 입력 beforetextchanged 실행 -> $s")
        }


        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Logger.v("패스워드 입력 onTextChanged 실행 -> $s")
        }

        override fun afterTextChanged(s: Editable?) {
            Logger.v("패스워드 더블 체크 입력 afterTextChanged 실행 -> $s")

           //hashcode()를 통해 aftertext에 넘어오는 값이 새 패스워드 적는 editext의 값인지 확인
           if(s.hashCode()==editxt_for_add_new_pwd.text.hashCode()){

               //check_pws_regex_funtion 리턴값으로  change된  텍스트 값 넣어주고 확인
               check_pws_regex_result=check_pws_regex_funtion(s.toString())

               //사용이 가능할때
               if(check_pws_regex_result){
                   Logger.v("비밀번호 =>$s   정규식 체크 ->  사용 가능")

                   txt_for_show_new_pwd_available_or_not.text="사용 가능"
                   txt_for_show_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_green_bacground)

               }else{//사용이 불가능 할때
                   Logger.v("비밀번호 => $s   정규식 체크 ->  사용 불가능")

                   if( editxt_for_add_new_pwd.length()>0){//뭐라도 하나 써있을 경우

                       txt_for_show_new_pwd_available_or_not.text="사용 불가"
                       txt_for_show_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_red_background)

                   }else{//아무것도 안써있을때

                       txt_for_show_new_pwd_available_or_not.text="입력 대기 중"
                       txt_for_show_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_login_btn_for_email_login_in_email_login_activity)
                   }
               }



               //입력한 패스워드
               val written_password= editxt_for_add_new_pwd.text.toString()

               //더블 체크 값이랑  입력한 패스워드가 일치 할 경우
               if(written_password == editxt_for_double_check_new_pwd.text.toString()){

                   if(editxt_for_double_check_new_pwd.length()>0) {
                       Logger.v("비밀번호 더블 체크  일치 함")
                       txt_for_show_double_check_new_pwd_available_or_not.text = "일치"
                       txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_green_bacground)

                   }else{
                       txt_for_show_double_check_new_pwd_available_or_not.text="입력 대기 중"
                       txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_login_btn_for_email_login_in_email_login_activity)
                   }
                   check_double_password_check = true

               }else{//일치 하지 않을 경우

                   //일치 하지 않지만 뭐라도 쓴 경우
                   if(editxt_for_add_new_pwd.length()>0){
                       Logger.v("비밀번호 더블 체크  일치 하지 않음")
                       txt_for_show_double_check_new_pwd_available_or_not.text="불일치"
                       txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_red_background)
                   }

                   check_double_password_check=false


               }

           }else if(s.hashCode()==editxt_for_double_check_new_pwd.text.hashCode()){//패스워드 더블 체크 editxt

               //입력한 패스워드
               val written_password= editxt_for_add_new_pwd.text.toString()

               //더블 체크 값이랑  입력한 패스워드가 일치 할 경우
               if(written_password == editxt_for_double_check_new_pwd.text.toString()){

                   if(editxt_for_double_check_new_pwd.length()>0) {
                       Logger.v("비밀번호 더블 체크  일치 함")
                       txt_for_show_double_check_new_pwd_available_or_not.text = "일치"
                       txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_green_bacground)

                   }else{
                   txt_for_show_double_check_new_pwd_available_or_not.text="입력 대기 중"
                   txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_login_btn_for_email_login_in_email_login_activity)
                  }
                   check_double_password_check = true

               }else{//일치 하지 않을 경우

                   //일치 하지 않지만 뭐라도 쓴 경우
                   if(editxt_for_double_check_new_pwd.length()>0){
                       Logger.v("비밀번호 더블 체크  일치 하지 않음")
                       txt_for_show_double_check_new_pwd_available_or_not.text="불일치"
                       txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_red_background)
                   }else{//일치 하지 않고 아무것도 안쓴 경우
                       Logger.v("비밀번호 더블 체크 아무것도 안 써져잇음")
                       txt_for_show_double_check_new_pwd_available_or_not.text="입력 대기 중"
                       txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_login_btn_for_email_login_in_email_login_activity)
                   }
                   check_double_password_check = false
               }
           }

            //패스워드 정규식 및  패스워드 확인 값과  일치할때
            if(check_pws_regex_result && check_double_password_check){
                complet_btn.setText(R.string.string_for_complete_btn_password_change_available)
                complet_btn.setBackgroundResource(R.drawable.custom_btn_for_no_radius_with_complete_check_color)

            }else{
                complet_btn.setText(R.string.string_for_complete_btn)
                complet_btn.setBackgroundResource(R.drawable.custom_btn_for_no_radius)
            }

        }
      }//textwatcher끝



    //비밀번호  정규식 체크 하는 기능
    fun check_pws_regex_funtion(password:String):Boolean{

        //영어랑  숫자 조합 7자리까지만 가능하다.  -> 영어가 먼저 들어가야됨
        val expression:String= "^[a-zA-Z](?=.{0,28}[0-9])[0-9a-zA-Z]{6}$"
        val pattern:Pattern= Pattern.compile(expression,Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(password)

        //true false 형태로 결과 값 return 함
        return matcher.matches()
    }




    //인증 코드 받기 진행
    fun send_sms_auth_code(phone_number:String,email:String) {

        if(!check_sms_request_ing) {
            Logger.v("인증 코드 받기 진행됨 -> 핸드폰 번호 -> $phone_number, 이메일 -> $email")

            //로딩 다이얼로그 보여줌.
            loadingDialog.show_dialog()

            retrofitClient = RetrofitClient(ServerIp.baseurl)
            retrofitClient.apiService.send_sms_auth_code_find_email_pws(phone_number, 2, email)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {

                        val result = response.body()?.string()

                        if (result.equals("1")) {//일반 로그인 이메일일때 일때

                            Logger.v("일반 이메일 로그인")
                            // TODO: 2020-08-31 일반 이메일 로그인이어서  인증코드 발행됨 -> 이제 인증코드 group visible 로 만들고 인증 코드 확인 하는거 진행하기
                            auth_view_visible(visible = true)//인증 코드 받는  뷰그룹 visible로
                            btn_for_get_certification_code.text = "변경 하기"

                            //핸드폰 번호 쓰는  ediitext와 이메일 적는  editext는 더이상 글을 쓰지 못하게 막는다.
                            editxt_for_add_phone_number.isEnabled = false
                            editext_for_write_use_email.isEnabled = false


                            //sms 인증 코드 요청 중 상태로 바꿔줌
                            check_sms_request_ing = true

                            //현재 엑티비티 진행 체크  true로 바꿔줌.
                            check_activity_working= true

                            //인증 번호에sms 보냈으니까  이번에는
                            //인증 번호 쓰는 editext 포커스 넣어줌.
                            editxt_for_add_certification_code.requestFocus()

                            timer_job = Job()
                            //타이머 실행
                            timer(
                                txt_for_show_remain_count_of_input_code,
                                timer_job,
                                editxt_for_add_certification_code
                            )

                        } else if (result.equals("2")) {//sns 로그인 이메일일때

                            Logger.v("sns용  로그인 이메일 일때 -> 이때는 비밀번호 필요 x")
                            Toast.makeText(
                                this@FindLoginPwsActivity,
                                R.string.string_for_sns_email,
                                Toast.LENGTH_SHORT
                            ).show()


                        } else if (result.equals("5")) {//중복되는  로그인 이메일이 없음.

                            Logger.v("보낸  핸드폰 번호와 이메일과 일치하는 정보가 없음")
                            Toast.makeText(
                                this@FindLoginPwsActivity,
                                R.string.string_for_empty_result,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        //로딩 다이얼로그 보여줌.
                        loadingDialog.dismiss_dialog()


                    }


                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                        Logger.v("비밀번호 찾기 인증코드 실패 -> ${t.message}")
                        //로딩 다이얼로그 보여줌.
                        loadingDialog.dismiss_dialog()

                    }

                })
        }else if(check_sms_request_ing){//요청중인 상태일때 버튼이 눌리면 다시 요청을 새로 받던지, 현 상태를 유지해야함 그래서 다이얼로그 로 의사 물어봄.

            Logger.v("요청중인 상태여서 다이얼로그로  의사 한번더 물어봄.")

            AlertDialog.Builder(this)
                .setMessage(R.string.string_for_rewrite_auth_phone_number)
                .setPositiveButton(R.string.string_for_yes){dialog_for_ask_sms_auth_status, which ->

                    auth_view_visible(false)
                    password_view_visible(false)

                    //새로운 인증과정을 하겠다 선택했으니  인증코드 받는 부분  다시 없애줌
                    btn_for_get_certification_code.text=getString(R.string.string_for_btn_get_certification_code)


                    check_sms_request_ing=false//인증 중 상태 초기화
                    check_activity_working=false


                    timer_job.cancel()//현재  인증 코드 넣기위한  타이머는 cancel 시켜줌.


                    //무효화 된거니까 다시  핸드폰 번호 쓰는  edittext enable-> true 로 풀어줌
                    editxt_for_add_phone_number.isEnabled=true
                    editext_for_write_use_email.isEnabled=true
                    editxt_for_add_certification_code.isEnabled=true

                    //다시 진행할 때는 원래  background를 유지해야하므로,  다시  기존 bacground 적용해준다.
                    linearlayout_for_input_certification_sms_code.setBackgroundResource(R.drawable.custom_view_radius_with_white_background)


                    //다시 입력하는 부분  clear 뒤  먼저 입력되는 email editxt에  포커스 줌
                    editxt_for_add_phone_number.text.clear()
                    editext_for_write_use_email.requestFocus()
                    editext_for_write_use_email.text.clear()

                    //기존 인증코드 넣는 부분도 clear
                    editxt_for_add_certification_code.text.clear()


                    //인증 여부  무효화 시켜줌.
                    check_auth_correct=false

                    dialog_for_ask_sms_auth_status.dismiss()

                }
                .setNegativeButton(R.string.string_for_no){dialog_for_ask_sms_auth_status, which ->

                    dialog_for_ask_sms_auth_status.dismiss()

                }.show()

        }
    }



    fun password_view_visible(visible:Boolean){
        if(visible){
            linearlayout_for_double_check_new_login_pwd.visibility=View.VISIBLE
            linearlayout_for_add_new_login_pwd.visibility=View.VISIBLE
            textView2.visibility=View.VISIBLE
        }else{
            linearlayout_for_double_check_new_login_pwd.visibility=View.INVISIBLE
            linearlayout_for_add_new_login_pwd.visibility=View.INVISIBLE
            textView2.visibility=View.INVISIBLE
        }
    }


    fun auth_view_visible(visible: Boolean){
        if(visible){
            linearlayout_for_input_certification_sms_code.visibility=View.VISIBLE
            txt_for_show_remain_count_of_input_code.visibility=View.VISIBLE
        }else{
            linearlayout_for_input_certification_sms_code.visibility=View.INVISIBLE
            txt_for_show_remain_count_of_input_code.visibility=View.INVISIBLE
        }
    }


    //서버에 등록된 패스워드를 바꿔준다.
    fun change_password(password:String,login_email:String){
        Logger.v("패스워드 바뀌기 실행")


        //패스워드 해쉬 처리하고 salt 값 같이 넣어주기위해 패스워드보안 처리 클래스 가져옴
        val password_with_salt_json=MakePassWordSecurity()

        retrofitClient= RetrofitClient(ServerIp.baseurl)
        retrofitClient.apiService.change_login_password( password_with_salt_json.make_sha_256_hash_value(password),login_email)
            .enqueue(object:Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                    //결과 값  1 = 성공,  2 = 실패
                    val result=response.body()?.string()
                     if(result.equals("1")){
                         Logger.v("비밀번호 변경 성공")

                          Toast.makeText(this@FindLoginPwsActivity,R.string.string_for_change_password_success,Toast.LENGTH_SHORT).show()

                         //현재 엑티비티 종료
                         finish()

                     }else{
                         Logger.v("비밀번호 변경 실패")
                         Toast.makeText(this@FindLoginPwsActivity,R.string.string_For_error_on_change_password,Toast.LENGTH_SHORT).show()
                     }

                }


                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Logger.v("패스워드 변경 실패 -> ${t.message}")
                    Toast.makeText(this@FindLoginPwsActivity,R.string.string_For_error_on_change_password,Toast.LENGTH_SHORT).show()
                }
            })

    }//change_password()끝


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

    //sms  문자 인증후  3분에 시간안에 문자로 받은
    //인증코드를 써야하는데, 이때  보여지는  timer 역할을 해주는 함수
    //코루틴을 사용해서  진행한다.
    fun timer(show_timer_txt: TextView, timer_job:Job,
              input_auth_key_editext: EditText
    ) {

        var i:Int=180//180초  3분

        Logger.v("timer 실행됨")

        //타이머 시작시 시간초 text를 빨간색으로 바꿔준 이유는
        //기존에 인증 성공시  초록색으로 바꿔주는데,
        //이때  다시 번호 변경을 누를경우  시간초 txt가  초록색이 되기 때문이다.
        show_timer_txt.setTextColor(Color.RED)




        //코루틴  실행됨
        CoroutineScope(Dispatchers.Main +timer_job).launch {

            while(i>0){

                i--//-1 초씩  진행됨

                show_timer_txt.text=String.format("%01d분 : %02d초", (i / 3600 * 60 + ((i % 3600) / 60)), (i % 60))//분초 형태로 보여줌.
                delay(1000)//1초씩 delay
            }

            //3분  끝났을때  아무것도 안되었다면, 진행되는 코드
            show_timer_txt.text="시간 초과 "

            //시간 초과 니까 인증키 넣는  editet  막음
            input_auth_key_editext.isEnabled=false


            check_activity_working=false
            timer_job.cancel()//시간 초과 됬으니까  job cancel 해줌
        }



    }//timer() 끝



}