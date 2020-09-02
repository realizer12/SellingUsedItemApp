package com.example.leedonghun.sellinguseditemapp.Activity

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.leedonghun.sellinguseditemapp.Dialog.LoadingDialog
import com.example.leedonghun.sellinguseditemapp.PrivateInfo.ServerIp
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import com.example.leedonghun.sellinguseditemapp.Singleton.AuthPoneNum
import com.example.leedonghun.sellinguseditemapp.Util.DeleteSnsData
import com.example.leedonghun.sellinguseditemapp.Util.KeyboardVisibilityUtils
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import kotlinx.android.synthetic.main.find_login_email_activity.*
import kotlinx.android.synthetic.main.find_login_email_activity.btn_for_get_certification_code
import kotlinx.android.synthetic.main.find_login_email_activity.btn_for_input_certification_code_complete
import kotlinx.android.synthetic.main.find_login_email_activity.editxt_for_add_certification_code
import kotlinx.android.synthetic.main.find_login_email_activity.editxt_for_add_phone_number
import kotlinx.android.synthetic.main.find_login_email_activity.linearlayout_for_input_certification_sms_code
import kotlinx.android.synthetic.main.find_login_email_activity.linearlayout_for_input_phone_number_for_certification
import kotlinx.android.synthetic.main.find_login_email_activity.txt_for_show_remain_count_of_input_code
import kotlinx.android.synthetic.main.term_pager_second_fragment.*
import kotlinx.android.synthetic.main.term_pager_second_fragment.view.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

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

    //retrofit 객체
    lateinit var retrofitClient:RetrofitClient

    //뷰에 흔들림 효과를 주는 애니메이션
    lateinit var shake: Animation

    //inputmethodmanager ->  소프트 키보드 관련 조작 담당
    lateinit var mInputMethodManager: InputMethodManager

    //현재 sms 인증 번호 요청중인지 여부를 체크한다. -> false =  요청 전 상태,  true = 현재 sms 인증코드 요청중인 상태
    var check_sms_request_ing:Boolean = false

    //sms 인증시 띄어줄  커스텀 다이얼로그 -로딩용
    lateinit var loadingDialog: LoadingDialog

    //타이머  코루틴 job
    private lateinit var timer_job:Job  // 1

    //인증키 입력이 맞을 경우
    private var check_auth_correct:Boolean=false

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils//키보드 visible 판단해주는  클래스  1-0

    //현재 엑티비티  사용 상태  true false 여부
    private var check_activity_working:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_login_email_activity)



        mInputMethodManager= this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        shake= AnimationUtils.loadAnimation(this,R.anim.shake)
        keyboardVisibilityUtils= KeyboardVisibilityUtils()//1-0
        loadingDialog= LoadingDialog(this)

        //인증번호 입력 관련 뷰들  초기 설정  INVISIBLE
        txt_for_show_remain_count_of_input_code.visibility=View.INVISIBLE
        linearlayout_for_input_certification_sms_code.visibility=View.INVISIBLE


        complet_btn.setOnClickListener(clickListener)//엑티비티 완료 버튼 클릭  1-1
        find_email_login_activity_container.setOnClickListener(clickListener)//배경 클릭 1-3
        back_btn.setOnClickListener(clickListener)//뒤로가기 이미지 버튼 클릭 1-8
        btn_for_get_certification_code.setOnClickListener(clickListener)//인증번호 받기 버튼 클릭 1-2
        btn_for_input_certification_code_complete.setOnClickListener(clickListener)//인증 코드 입력 완료 버튼 1-4



    }//onCreate()끝




    //뷰 클릭이벤트
    val clickListener=View.OnClickListener {

        when(it){

            complet_btn->{//1-1
                onBackPressed()
            }//1-1 끝


            find_email_login_activity_container->{//1-3

                Logger.v("배경화면 클릭됨")
                keyboardVisibilityUtils.hideKeyboard(this,it)

            }//1-3끝

            btn_for_get_certification_code->{//1-2
                Logger.v("인증 번호 받기 버튼 클릭됨 -> 인증번호 보내기 진행")

                send_sms_auth_code(editxt_for_add_phone_number.text.toString())
            }//1-2 끝



            btn_for_input_certification_code_complete->{//1-4
                Logger.v("인증번호 입력 완료 버튼 클릭 -> 인증 번호 체크 진행 ")

                check_sms_auth_code(editxt_for_add_phone_number.text.toString(),editxt_for_add_certification_code.text.toString())
            }//1-4끝

            back_btn->{//1-8
                onBackPressed()
            }

        }//when끝

    }//클릭 리스너 끝
    

    
    //인증 코드 받기 진행
    fun send_sms_auth_code(phone_number:String){

        //phone_number가  비어있다면,
        if(TextUtils.isEmpty(phone_number)){

            Logger.v("phone_number edittext 비어 있음.")

            ////폰 번호 쓰라고 토스트 날리고, shake 애니메이션으로  editext 흔들어줌.
            //그리고 포커스 줌.
            Toast.makeText(this,R.string.string_for_plz_insert_phone_number, Toast.LENGTH_SHORT).show()
            linearlayout_for_input_phone_number_for_certification.startAnimation(shake)
            editxt_for_add_phone_number.requestFocus()

        }else if(!Pattern.matches("^01(?:0|1|[6-9])?(\\d{3}|\\d{4})?(\\d{4})$",phone_number)) {// 핸드폰 번호  정규식이 맞지 않을 경우.

            Logger.v("핸드폰 번호 특성이 아님")

            //토스트  날려주고  shake 애니메이션 효과 후,  edittext clear 하고  다시 focus 줌.
            Toast.makeText(this,R.string.string_for_not_match_phone_num_regular_expression, Toast.LENGTH_SHORT).show()
            linearlayout_for_input_phone_number_for_certification.startAnimation(shake)
            editxt_for_add_phone_number.text.clear()
            editxt_for_add_phone_number.requestFocus()

        }else if(!check_sms_request_ing){//핸드폰 형식이고, sms 요구 상태가 false 일때 -> 처음 번호 넣고  요청할때임(인증번호 받기 버튼).

            Logger.v("핸드폰 번호 형식 맞고,  인증번호 받기 버튼이 눌렸을때")

            //로딩 다이얼로그 보여줌.
            loadingDialog.show_dialog()

            //서버로  폰 번호랑  이유 보냄 -> 이유는 0 = 회원가입을 위한  인증.
            retrofitClient= RetrofitClient(ServerIp.baseurl)
            retrofitClient.apiService.send_sms_auth_code_find_email_pws(phone_number,1,null)
                .enqueue(object: Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                        //sms  인증 코드 보내기 결과 받는 변수.
                        val send_phone_num_result: String? = response.body()?.string()

                        Logger.v("의  핸드폰 인증 콜백값 -> $send_phone_num_result")

                        if (send_phone_num_result.equals("1")) {//결과 성공시


                            //다이얼로그로  인증 번호 보냄을 알려준다.
                            //sms 보내기 성공 했으니,  이제  시간 초안에
                            //문자로 받은  인증 코드를 적어줘야되게  숨겨 놨던 인증 코드 입력 뷰 보여 줌
                            btn_for_get_certification_code.text = "변경 하기"
                            txt_for_show_remain_count_of_input_code.visibility =
                                View.VISIBLE
                            linearlayout_for_input_certification_sms_code.visibility =
                                View.VISIBLE


                            //핸드폰 번호 쓰는  ediitext는  더이상 글을 쓰지 못하게 막는다.
                            editxt_for_add_phone_number.isEnabled = false

                            //sms 요청 중 상태로 바꿔줌
                            check_sms_request_ing = true


                            //현재 엑티비티 진행 체크  true로 바꿔줌.
                            check_activity_working= true


                            //인증 번호에sms 보냈으니까  이번에는
                            //인증 번호 쓰는 editext 포커스 넣어줌.
                            editxt_for_add_certification_code.requestFocus()


                            //코루틴으로 시간초 진행
                            timer_job = Job()
                            timer(
                                txt_for_show_remain_count_of_input_code,
                                timer_job,
                                editxt_for_add_certification_code
                            )


                        }else if(send_phone_num_result.equals("5")){//중복값이 있는 경우이다.

                            Toast.makeText(this@FindLoginEmailActivity,R.string.string_for_no_one_use_phone_number, Toast.LENGTH_SHORT).show()
                            linearlayout_for_input_phone_number_for_certification.startAnimation(shake)
                            editxt_for_add_phone_number.text.clear()
                            editxt_for_add_phone_number.requestFocus()

                        }else{//서버에서  해당 폰  번호 넘기는 과정에서  문제가 생김.

                            Toast.makeText(this@FindLoginEmailActivity,R.string.string_for_problem_on_http_communication, Toast.LENGTH_SHORT).show()

                        }

                        //서버 응답이 왔으므로 로딩 다이얼로그 없앰
                        loadingDialog.dismiss_dialog()


                    }//onResponse()끝

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                        Toast.makeText(this@FindLoginEmailActivity,R.string.string_for_problem_on_http_communication, Toast.LENGTH_SHORT).show()
                        Logger.v("인증번호 받기 -> 서버 통신 에러 남-> "+t.message)

                        //서버 응답이 왔으므로 로딩 다이얼로그 없앰
                        loadingDialog.dismiss_dialog()

                    }//onFailure() 끝끝


                })//retrofit 끝

        }// 핸드폰 번호  맞을때 여부 끝.
        else if(check_sms_request_ing){//요청중인 상태일때 버튼이 눌리면 다시 요청을 새로 받던지, 현 상태를 유지해야함 그래서 다이얼로그 로 의사 물어봄.


            AlertDialog.Builder(this)
                .setMessage(R.string.string_for_rewrite_auth_phone_number)
                .setPositiveButton(R.string.string_for_yes){dialog_for_ask_sms_auth_status, which ->


                    //새로운 인증과정을 하겠다 선택했으니  인증코드 받는 부분  다시 없애줌
                    btn_for_get_certification_code.text=getString(R.string.string_for_btn_get_certification_code)
                    txt_for_show_remain_count_of_input_code.visibility=View.INVISIBLE
                    linearlayout_for_input_certification_sms_code.visibility=View.INVISIBLE
                    //로그인 이메일 보여주는  뷰  visible 상태로 바꿔줌.
                    txt_for_show_email_title.visibility=View.INVISIBLE
                    txt_for_login_email.visibility=View.INVISIBLE

                    check_sms_request_ing=false//인증 중 상태 초기화
                    check_activity_working=false


                    timer_job.cancel()//현재  인증 코드 넣기위한  타이머는 cancel 시켜줌.

                    dialog_for_ask_sms_auth_status.dismiss()

                    //다시 번호 적는 부분  포커스 줌
                    editxt_for_add_phone_number.text.clear()
                    editxt_for_add_phone_number.requestFocus()

                    //기존 인증코드 넣는 부분도 clear
                    editxt_for_add_certification_code.text.clear()

                    //무효화 된거니까 다시  핸드폰 번호 쓰는  edittext enable-> true 로 풀어줌
                    editxt_for_add_phone_number.isEnabled=true
                    editxt_for_add_certification_code.isEnabled=true

                    //다시 진행할 때는 원래  background를 유지해야하므로,  다시  기존 bacground 적용해준다.
                    linearlayout_for_input_certification_sms_code.setBackgroundResource(R.drawable.custom_view_radius_with_white_background)

                    //인증 여부  무효화 시켜줌.
                    check_auth_correct=false

                }
                .setNegativeButton(R.string.string_for_no){dialog_for_ask_sms_auth_status, which ->

                    dialog_for_ask_sms_auth_status.dismiss()

                }.show()

        }
    }//인증번호 보내기 function


    //받은 인증 코드 맞는지 확인
    fun check_sms_auth_code(phone_number: String,auth_key:String){


        if(auth_key.length==5 && timer_job.isActive){//인증키가  5개이고, 타이머가  작동하고 있을 경우

            Logger.v("인증번호 5자리 맞음")

            retrofitClient= RetrofitClient(ServerIp.baseurl)
            retrofitClient.apiService.send_sms_auth_key(phone_number,1,auth_key)
                .enqueue(object :Callback<ResponseBody>{

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                        //인증키 체크 결과
                        val check_auth_key_result:String?=response.body()?.string()
                        var return_email:JSONObject=JSONObject()
                        var result:String=""
                        var email:String =""
                      
                       try {
                           return_email=JSONObject(check_auth_key_result)
                           result=return_email.get("return_status").toString()
                           email=return_email.get("return_email").toString()     
                        
                        }catch(e:JSONException){
                      
                           Logger.v("$e")
                           
                        }
                       

                        if(result == "1"){//성공적으로 끝났을때

                            Logger.v("회원가입용  인증키  체크 결과 -> 인증 성공 email값 -> $return_email")


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

                            
                            //로그인 이메일 보여주는  뷰  visible 상태로 바꿔줌.
                            txt_for_show_email_title.visibility=View.VISIBLE
                            txt_for_login_email.visibility=View.VISIBLE
                            txt_for_login_email.text = email


                            //모든 프로세스가 끝났으니 false로 처리
                            check_activity_working=false

                            //인증 완료 여부 true로
                            check_auth_correct=true

                        }else if(check_auth_key_result.equals("2")){//인증키가 틀린경우
                            Logger.v("회원가입용  인증키  체크 결과 -> 인증 실패")

                            editxt_for_add_certification_code.requestFocus()
                            editxt_for_add_certification_code.text.clear()
                            linearlayout_for_input_certification_sms_code.startAnimation(shake)

                            Toast.makeText(this@FindLoginEmailActivity,"인증 코드가 틀립니다!",Toast.LENGTH_SHORT).show()


                        }else if(check_auth_key_result.equals("3")){//해당 값이 존재 하지 않을때
                            Logger.v("회원가입용  인증키  체크 결과 -> 해당 번호의 데이터 존재하지 않음")

                            Toast.makeText(this@FindLoginEmailActivity,"인증 코드를 한번더 요청해 보세요",Toast.LENGTH_SHORT).show()

                        }else if(check_auth_key_result.equals("4")){//조회 커리문 실패시
                            Logger.v("회원가입용  인증키  체크 결과 -> 조회문 실패")

                            Toast.makeText(this@FindLoginEmailActivity,"서버 인증 조회 에러",Toast.LENGTH_SHORT).show()

                        }else{

                            Logger.v("회원가입용  인증키  체크 결과 -> 인증상태 1(인증함)으로 업데이트 실패")
                            Toast.makeText(this@FindLoginEmailActivity,"서버 에러 error: 232",Toast.LENGTH_SHORT).show()

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

        Logger.v("뒤로 가기 버튼 클릭 이벤트")

        if(check_activity_working){//count가  진행중일때는   한번더  alert 로 물어본다.
            Logger.v("완료 버튼 클릭됨 -> 아직  count 진행중이므로,  dialog 띄움")

            AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(R.string.string_for_ask_finish_activity)
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