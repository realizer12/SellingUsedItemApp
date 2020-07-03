package com.example.leedonghun.sellinguseditemapp.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.Dialog.LoadingDialog
import com.example.leedonghun.sellinguseditemapp.Interface.CheckMakeIdPagerCompleteStatus
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import kotlinx.android.synthetic.main.term_pager_second_fragment.*
import kotlinx.android.synthetic.main.term_pager_second_fragment.view.*
import kotlinx.android.synthetic.main.term_pager_second_fragment.view.editxt_for_add_phone_number
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import java.util.regex.Pattern

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

    val retrofitClient:RetrofitClient= RetrofitClient()

    //뷰에 흔들림 효과를 주는 애니메이션
    val shake: Animation = AnimationUtils.loadAnimation(context,R.anim.shake)

    //현재 sms 인증 번호 요청중인지 여부를 체크한다. -> false =  요청 전 상태,  true = 현재 sms 인증코드 요청중인 상태
    var check_sms_request_ing:Boolean = false

    //sms 인증시 띄어줄  커스텀 다이얼로그 -로딩용
    val loadingDialog:LoadingDialog = LoadingDialog(context)

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
                retrofitClient.apiService.send_phone_number_for_join_member("01073807810",0)
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
                                    view.btn_for_get_certification_code.text = "번호 변경"
                                    view.txt_for_show_remain_count_of_input_code.visibility = View.VISIBLE
                                    view.linearlayout_for_input_certification_sms_code.visibility = View.VISIBLE

                                    //sms 요청 중 상태로 바꿔줌
                                    check_sms_request_ing = true

                                    //인증 번호에sms 보냈으니까  이번에는
                                    //인증 번호 쓰는 editext 포커스 넣어줌.
                                    view.editxt_for_add_certification_code.requestFocus()


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


                    })


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

                        dialog_for_ask_sms_auth_status.dismiss()
                    }
                    .setNegativeButton("아니오"){dialog_for_ask_sms_auth_status, which ->

                        dialog_for_ask_sms_auth_status.dismiss()

                    }.show()

            }

        }//인증번호 받기 버튼 클릭 이벤트 끝.



        //문자로 받은 인증 번호 확인 버튼
        view.btn_for_input_certification_code_complete.setOnClickListener {
            Log.v("check_app_runnig_status",fragment_name_for_Log+"의 인증번호 입력완료 버튼 눌림.")

            //입련된 인증번호  인증 성공시, 더이상  입력할께 없으므로,  키보드 없애주고,  editexxt 에 포커스 clear 해준다.
            mInputMethodManager.hideSoftInputFromWindow(view.editxt_for_add_phone_number.windowToken, 0);
            view.editxt_for_add_certification_code.clearFocus()


            //넘어갈수 있는 상태임을 엑티비티에 알림 -> 일단 넣음.
            check_complete.CheckMakeIdPagerComplete_all_or_not(true,1);
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
    }

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