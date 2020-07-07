package com.example.leedonghun.sellinguseditemapp.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.text.format.Time
import android.text.format.Time.getCurrentTimezone
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.Dialog.LoadingDialog
import com.example.leedonghun.sellinguseditemapp.Interface.CheckMakeIdPagerCompleteStatus
import com.example.leedonghun.sellinguseditemapp.Interface.SendPhonNumberToAnotherFragment
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import kotlinx.android.synthetic.main.term_pager_second_fragment.*
import kotlinx.android.synthetic.main.term_pager_second_fragment.view.*
import kotlinx.android.synthetic.main.term_pager_second_fragment.view.editxt_for_add_phone_number
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.ticker
import okhttp3.ResponseBody
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.util.regex.Pattern
import kotlin.RuntimeException

/**
 * SellingUsedItemApp
 * Class: TermPagerSecondFragment.
 * Created by leedonghun.
 * Created On 2020-06-15.
 *
 * Description: 회원가입 뷰페이져에서  두번째 뷰페이져이다.
 * 이곳에서는 핸드폰 번호를 쓰고  인증 번호를  문자로 받고,
 * 인증번호를 정확히 적었을때  다음으로 넘어갈수 있다.
 */
class MakeIdPagerSecondFragment(context: Context):Fragment() {

    //로그 쓸때  편하게 쓰기 위해서..
    val fragment_name_for_Log:String="TermPagerSecondFragment"

//    //inputmethodmanager ->  소프트 키보드 관련 조작 담당
     val mInputMethodManager:InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    //현재  프래그먼트에서 요구하는 사항들 모두 진행 했는지 체크해서
    //parent layout로 값 보내는 인터페이스
    lateinit var check_complete: CheckMakeIdPagerCompleteStatus

    lateinit var authed_phone_num: SendPhonNumberToAnotherFragment


    val retrofitClient:RetrofitClient= RetrofitClient()

    //뷰에 흔들림 효과를 주는 애니메이션
    val shake: Animation = AnimationUtils.loadAnimation(context,R.anim.shake)

    //현재 sms 인증 번호 요청중인지 여부를 체크한다. -> false =  요청 전 상태,  true = 현재 sms 인증코드 요청중인 상태
    var check_sms_request_ing:Boolean = false

    //sms 인증시 띄어줄  커스텀 다이얼로그 -로딩용
    val loadingDialog:LoadingDialog = LoadingDialog(context)

    //타이머  코루틴 job
    private lateinit var timer_job:Job  // 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        //프래그먼트 뷰 연결
        val view: View = inflater.inflate(R.layout.term_pager_second_fragment, container, false)
        Log.v("check_app_runnig_status",fragment_name_for_Log+"의 onCreateView 실행 됨")


        var phone_number:String//인증할 핸드폰 번호


        //배경 클릭시  키보드 내려오는  이벤트
        view.entire_layout_of_termpager_second_fragment.setOnClickListener {

            mInputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0);

        }


        //인증 번호 받기 버튼
        view.btn_for_get_certification_code.setOnClickListener {
            Log.v("check_app_runnig_status",fragment_name_for_Log+"의 휴대폰 인증번호 받기 버튼 눌림")

            phone_number= view.editxt_for_add_phone_number.text.toString()//휴대폰 번호 입력란에 적은 핸드폰 번호.


            //phone_number가  비어있다면,
            if(TextUtils.isEmpty(phone_number)){

                Log.v("check_app_runnig_status","$fragment_name_for_Log 의 phone_number edittext 비어 있음.")

                ////폰 번호 쓰라고 토스트 날리고, shake 애니메이션으로  editext 흔들어줌.
                //그리고 포커스 줌.
                Toast.makeText(activity,"핸드폰 번호를  입력하세요",Toast.LENGTH_SHORT).show()
                view.linearlayout_for_input_phone_number_for_certification.startAnimation(shake)
                view.editxt_for_add_phone_number.requestFocus()

            }else if(!Pattern.matches("^01(?:0|1|[6-9])?(\\d{3}|\\d{4})?(\\d{4})$",phone_number)) {// 핸드폰 번호  정규식이 맞지 않을 경우.

                Log.v("check_app_runnig_phone", "$fragment_name_for_Log -> 핸드폰 번호 특성이 아님")

                //토스트  날려주고  shake 애니메이션 효과 후,  edittext clear 하고  다시 focus 줌.
                Toast.makeText(activity,"핸드폰 번호 특성이 아닙니다.",Toast.LENGTH_SHORT).show()
                view.linearlayout_for_input_phone_number_for_certification.startAnimation(shake)
                view.editxt_for_add_phone_number.text.clear()
                view.editxt_for_add_phone_number.requestFocus()

            }else if(!check_sms_request_ing){//핸드폰 형식이고, sms 요구 상태가 false 일때 -> 처음 번호 넣고  요청할때임(인증번호 받기 버튼).

                Log.v("check_app_runnig_phone", "$fragment_name_for_Log ->  핸드폰 번호 형식 맞고,  인증번호 받기 버튼이 눌렸을때")

                 //로딩 다이얼로그 보여줌.
                 loadingDialog.show_dialog()


                 //서버로  폰 번호랑  이유 보냄 -> 이유는 0 = 회원가입을 위한  인증.
                 retrofitClient.apiService.send_phone_number_for_auth_own(phone_number,0)
                    .enqueue(object:Callback<ResponseBody>{
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                            //sms  인증 코드 보내기 결과 받는 변수.
                            val send_phone_num_result: String? = response.body()?.string()

                            Log.v(
                                "check_app_runnig_phone",
                                fragment_name_for_Log + "의  핸드폰 인증 콜백값 -> " + send_phone_num_result
                            )

                            if (send_phone_num_result.equals("1")) {//결과 성공시


                                //다이얼로그로  인증 번호 보냄을 알려준다.
                                //sms 보내기 성공 했으니,  이제  시간 초안에
                                //문자로 받은  인증 코드를 적어줘야되게  숨겨 놨던 인증 코드 입력 뷰 보여 줌
                                view.btn_for_get_certification_code.text = "다시 받기"
                                view.txt_for_show_remain_count_of_input_code.visibility = View.VISIBLE
                                view.linearlayout_for_input_certification_sms_code.visibility = View.VISIBLE

                                //sms 요청 중 상태로 바꿔줌
                                check_sms_request_ing = true

                                //인증 번호에sms 보냈으니까  이번에는
                                //인증 번호 쓰는 editext 포커스 넣어줌.
                                view.editxt_for_add_certification_code.requestFocus()


                                //코루틴으로 시간초 진행
                                timer_job= Job()
                                timer(view.txt_for_show_remain_count_of_input_code,timer_job)


                            }else{//서버에서  해당 폰  번호 넘기는 과정에서  문제가 생김.

                                Toast.makeText(activity,"서버 통신 과정에서 문제가 생겼습니다.",Toast.LENGTH_SHORT).show()

                            }

                            //서버 응답이 왔으므로 로딩 다이얼로그 없앰
                            loadingDialog.dismiss_dialog()


                        }//onResponse()끝

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.v("check_app_runnig_phone",fragment_name_for_Log+"의  서버 통신 에러 남-> "+t.message)

                            //서버 응답이 왔으므로 로딩 다이얼로그 없앰
                            loadingDialog.dismiss_dialog()

                        }//onFailure() 끝끝


                    })//retrofit 끝

            }// 핸드폰 번호  맞을때 여부 끝.
            else if(check_sms_request_ing){//요청중인 상태일때 버튼이 눌리면 다시 요청을 새로 받던지, 현 상태를 유지해야함 그래서 다이얼로그 로 의사 물어봄.


                AlertDialog.Builder(activity)
                    .setMessage("번호 변경시, \n현재 보낸 인증코드는 무효화 됩니다.\n정말 번호 변경을 하시겠습니까? ")
                    .setPositiveButton("네"){dialog_for_ask_sms_auth_status, which ->


                        //새로운 인증과정을 하겠다 선택했으니  인증코드 받는 부분  다시 없애줌
                        view.btn_for_get_certification_code.text=getString(R.string.string_for_btn_get_certification_code)
                        view.txt_for_show_remain_count_of_input_code.visibility=View.INVISIBLE
                        view.linearlayout_for_input_certification_sms_code.visibility=View.INVISIBLE

                        check_sms_request_ing=false//인증 중 상태 초기화

                        timer_job.cancel()//현재  인증 코드 넣기위한  타이머는 cancel 시켜줌.

                        dialog_for_ask_sms_auth_status.dismiss()

                        //다시 번호 적는 부분  포커스 줌
                        view.editxt_for_add_phone_number.text.clear()
                        view.editxt_for_add_phone_number.requestFocus()

                    }
                    .setNegativeButton("아니오"){dialog_for_ask_sms_auth_status, which ->

                        dialog_for_ask_sms_auth_status.dismiss()

                    }.show()

            }

        }//인증번호 받기 버튼 클릭 이벤트 끝.



        //문자로 받은 인증 번호 확인 버튼
        view.btn_for_input_certification_code_complete.setOnClickListener {
            Log.v("check_app_runnig_status",fragment_name_for_Log+"의 인증번호 입력완료 버튼 눌림.")

            //sms 로 받아서  editext에  넣은 인증키
            var auth_key:String=editxt_for_add_certification_code.text.toString()

            phone_number= view.editxt_for_add_phone_number.text.toString()//휴대폰 번호 입력란에 적은 핸드폰 번호.

            if(auth_key.length==5 && timer_job.isActive){//인증키가  5개이고, 타이머가  작동하고 있을 경우

                Log.v("check_app_runnig_status",fragment_name_for_Log+"의  인증번호 5자리 맞음")

                retrofitClient.apiService.send_sms_auth_key(phone_number,0,auth_key)
                    .enqueue(object :Callback<ResponseBody>{

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                            //인증키 체크 결과
                           val check_auth_key_result:String?=response.body()?.string()

                            if(check_auth_key_result.equals("1")){//성공적으로 끝났을때

                                Log.v("check_app_runnig_status","회원가입용  인증키  체크 결과 -> 인증 성공")


                                //입련된 인증번호  인증 성공시, 더이상  입력할께 없으므로,  키보드 없애주고,  editexxt 에 포커스 clear 해준다.
                                mInputMethodManager.hideSoftInputFromWindow(view.editxt_for_add_phone_number.windowToken, 0);
                                view.editxt_for_add_certification_code.clearFocus()


                                //넘어갈수 있는 상태임을 엑티비티에 알림 -> 일단 넣음.
                                check_complete.CheckMakeIdPagerComplete_all_or_not(true,1);

                                timer_job.cancel()//타이머코루틴 job 취소

                                view.txt_for_show_remain_count_of_input_code.text="인증 됨"
                                view.txt_for_show_remain_count_of_input_code.setTextColor(Color.GREEN)


                                authed_phone_num.send_authed_phone_num(phone_number)


                            }else if(check_auth_key_result.equals("2")){//인증키가 틀린경우
                                Log.v("check_app_runnig_status","회원가입용  인증키  체크 결과 -> 인증 실패")

                                view.editxt_for_add_certification_code.requestFocus()
                                view.editxt_for_add_certification_code.text.clear()
                                view.linearlayout_for_input_certification_sms_code.startAnimation(shake)

                                Toast.makeText(activity,"인증 코드가 틀립니다!",Toast.LENGTH_SHORT).show()


                            }else if(check_auth_key_result.equals("3")){//해당 값이 존재 하지 않을때
                                Log.v("check_app_runnig_status","회원가입용  인증키  체크 결과 -> 해당 번호의 데이터 존재하지 않음")

                                Toast.makeText(activity,"인증 코드를 한번더 요청해 보세요",Toast.LENGTH_SHORT).show()

                            }else if(check_auth_key_result.equals("4")){//조회 커리문 실패시
                                Log.v("check_app_runnig_status","회원가입용  인증키  체크 결과 -> 조회문 실패")

                                Toast.makeText(activity,"서버 인증 조회 에러",Toast.LENGTH_SHORT).show()

                            }else{

                                Log.v("check_app_runnig_status","회원가입용  인증키  체크 결과 -> 인증상태 1(인증함)으로 업데이트 실패")
                                Toast.makeText(activity,"서버 에러 error: 232",Toast.LENGTH_SHORT).show()

                            }

                        }


                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                            Log.v("check_app_runnig_status","회원가입용  인증키  체크 실패 결과 ->  ${t.toString()}")
                        }


                })



            }else if(auth_key.length!=5){//인증키는 5자리인데  그게 아닌경우는  토스트로 알림

                Toast.makeText(activity,"인증키는 5자리입니다.",Toast.LENGTH_SHORT).show()
                view.editxt_for_add_certification_code.requestFocus()
                view.editxt_for_add_certification_code.text.clear()
                view.linearlayout_for_input_certification_sms_code.startAnimation(shake)

            }else if(!timer_job.isActive){

                Toast.makeText(activity,"인증키 요청을 다시 하세요!",Toast.LENGTH_SHORT).show()
                view.btn_for_get_certification_code.startAnimation(shake)

            }


         }



        return view

    }//onCreateView 끝



    override fun onAttach(context: Context) {
        super.onAttach(context)

        Log.v("check_app_runnig_status",fragment_name_for_Log+"의 onAttach() 실행 됨")

        //진행사항  체크 인터페이스 객체 initialize 함.
        if(context is CheckMakeIdPagerCompleteStatus){

            check_complete= context

        }else{
            throw RuntimeException(context.toString())
        }


        if(context is SendPhonNumberToAnotherFragment){

            authed_phone_num=context
        }else{
            throw  RuntimeException(context.toString())
        }


    }



    //sms  문자 인증후  3분에 시간안에 문자로 받은
    //인증코드를 써야하는데, 이때  보여지는  timer 역할을 해주는 함수
    //코루틴을 사용해서  진행한다.
    fun timer(show_timer_txt:TextView,timer_job:Job) {

        var i:Int=180//180초  3분

        Log.v("check_app_runnig_status", "timer 실행됨")

        //타이머 시작시 시간초 text를 빨간색으로 바꿔준 이유는
        //기존에 인증 성공시  초록색으로 바꿔주는데,
        //이때  다시 번호 변경을 누를경우  시간초 txt가  초록색이 되기 때문이다.
        show_timer_txt.setTextColor(Color.RED)

        //코루틴  실행됨
        CoroutineScope(Main+timer_job).launch {

                while(i>0){

                    i--//-1 초씩  진행됨

                    show_timer_txt.text=String.format("%01d분 : %02d초", (i / 3600 * 60 + ((i % 3600) / 60)), (i % 60))//분초 형태로 보여줌.
                    delay(1000)//1초씩 delay
                }

            //3분  끝났을때  아무것도 안되었다면, 진행되는 코드
            show_timer_txt.text="시간 초과 "
            timer_job.cancel()//시간 초과 됬으니까  job cancel 해줌
        }



    }//timer() 끝


    override fun onPause() {
        super.onPause()
        Log.v("check_app_runnig_status",fragment_name_for_Log+"의 onpause() 실행 됨")


        //맨처음 포커싱 해서  키보드 올려주는  editext만  hide 를  적용
        //왜냐면, 애네는  inputmanager로  키보드를  올려놓은 상태이기 때문에
        //pause 단계에서도  그대로 유지가 되어있는 중이다.
        //그래서 혹시나  맨처음  키보드 올린상태에서 더이상 키보드 전환 과정이 없다면,
        //아래 코드로  적용  hide가 적용된다.
        mInputMethodManager.hideSoftInputFromWindow(view!!.editxt_for_add_phone_number.windowToken, 0);

    }


}//현재 class  끝