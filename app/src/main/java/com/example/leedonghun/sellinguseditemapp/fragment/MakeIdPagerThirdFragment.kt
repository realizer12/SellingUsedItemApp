package com.example.leedonghun.sellinguseditemapp.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.Dialog.LoadingDialog
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import com.example.leedonghun.sellinguseditemapp.Singleton.auth_phon_num
import com.example.leedonghun.sellinguseditemapp.Util.MakePassWordSecurity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.term_pager_third_fragment.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * SellingUsedItemApp
 * Class: TermPagerThirdFragment.
 * Created by leedonghun.
 * Created On 2020-06-15.
 * Description:
 */
class MakeIdPagerThirdFragment(private val check_sns_or_email:Int,context: Context) :Fragment() {


    private var log_for_class:String="TermPagerThirdFragment"

    //inputmethodmanager ->  소프트 키보드 관련 조작 담당
    val mInputMethodManager:InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    //멤버  이메일
    lateinit var member_email:String

    //비밀번호  256해쉬값 만드는 클래스 객체
    val make_hash256_and_sort_value:MakePassWordSecurity=MakePassWordSecurity()

    //retrofir 통신을 하기위한 클래스 객체
    val retrofitClient:RetrofitClient= RetrofitClient()

    //뷰에 흔들림 효과를 주는 애니메이션
    val shake: Animation = AnimationUtils.loadAnimation(context,R.anim.shake)

    //sms 인증시 띄어줄  커스텀 다이얼로그 -로딩용
    val loadingDialog: LoadingDialog = LoadingDialog(context)

    //로그인 이메일 중복 체크 여부 -> default 값 false
    var check_duplicate_login_email:Boolean=false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {


        //프래그먼트 뷰 연결
        val view: View = inflater.inflate(R.layout.term_pager_third_fragment, container, false)

        Log.v("check_app_runnig_status","TermPagerThirdFragment onCreateView 실행 됨 -> sns 로그인 여부 0=sns , 1=이메일    결과=> $check_sns_or_email")


        //배경 눌렀을때도  키보드 내려갈수 있게 만듬
        view.entire_layout_of_termpager_third_fragment.setOnClickListener {
            Log.v("check_app_runnig_status","${log_for_class}에서 배경 눌림-> 키보드 올라와있을 시 내려감")

            mInputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0);

        }



        //아이디 중복 확인 리스너
        view.btn_for_dupulicate_check_new_login_email.setOnClickListener {


            Log.v("check_app_runnig_status","${log_for_class}에서 아이디 중복 확인 리스너  클릭됨")


            //이메일 형식이 맞는 경우
            if(check_email_regex_function(view.editxt_for_make_new_login_email.text.toString())){


                //로그인 이메일 중복 체크 여부 ->  true
                if(check_duplicate_login_email){

                    Log.v("check_app_runnig_status","${log_for_class}에서 이메일 형식 맞음")
                    AlertDialog.Builder(activity)
                        .setMessage("정말 이메일을 변경 하시겠습니까??")
                        .setPositiveButton("네"){dialog, which ->

                            //중복 체크 다시 리셋 하므로 체크 값 다시 false로 바꿔줌.
                            check_duplicate_login_email=false
                            view.btn_for_dupulicate_check_new_login_email.text="중복확인"

                            view.editxt_for_make_new_login_email.isEnabled=true
                            view.editxt_for_make_new_login_email.text.clear()
                            view.editxt_for_make_new_login_email.requestFocus()

                            //다시 색깔 원래 색으로 바꿔줌
                            view.linearlayout_for_add_new_login_email.setBackgroundResource(R.drawable.custom_view_radius_with_white_background)


                            dialog.dismiss()

                        }
                        .setNegativeButton("아니요"){dialog, which ->

                            dialog.dismiss()

                        }.show()

                }else{//이메일 로그인  중복체크 안되었었을때


                    //중복확인 동안  다이얼로그 보여줌.
                    loadingDialog.show_dialog()

                    //이메일 적는 editext의 값을 member_email에 넣어줌.
                    member_email=view.editxt_for_make_new_login_email.text.toString()

                    //서버로  이메일  보내서 확인
                    retrofitClient.apiService.check_duplicate_login_email(member_email)
                        .enqueue(object :Callback<ResponseBody>{

                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                                //이메일 중복 체크 결과
                                val result:String?=response.body()?.string()

                                Log.v("check_app_runnig_status","${log_for_class}에서 이메일 중복 확인 결과 -> $result")

                                if(result.equals("1")){// 중복 없음 -> 이메일 사용 가능
                                    Log.v("check_app_runnig_status","${log_for_class}의 이메일 중복 없음 -> 회원 가입 가능")

                                    Toast.makeText(activity,"사용 가능한 이메일 입니다!",Toast.LENGTH_SHORT).show  ()

                                    //이메일 확인 후,  다시 입력 하고 싶을수 있으므로
                                    view.btn_for_dupulicate_check_new_login_email.text="다시 입력"

                                    //확인 되었을 경우는 초록색으로  가시적으로 알려준다.
                                    view.linearlayout_for_add_new_login_email.setBackgroundResource(R.drawable.custom_view_radius_with_green_bacground)
                                    view.editxt_for_make_new_login_email.isEnabled=false//입력  막기

                                    //중복 체크 여부  true 로 바꿔줌.
                                    check_duplicate_login_email=true


                                }else if (result.equals("-2")){//중복 값이 있음 -> 이메일 사용 불가능
                                    Log.v("check_app_runnig_status","${log_for_class}의 이메일 중복 값 있음")

                                    Toast.makeText(activity,"이미 사용중인 이메일 입니다.",Toast.LENGTH_SHORT).show()

                                    //edit text처리
                                    make_clear_editext_with_wrong_value(view.editxt_for_make_new_login_email)


                                }else if(result.equals("-1")){//쿼리 실패함
                                    Log.v("check_app_runnig_status","${log_for_class}의 이메일 중복 쿼리문 실패")

                                    Toast.makeText(activity,"서버에 문제 발생 erro -1",Toast.LENGTH_SHORT).show()

                                    //edit text처리
                                    make_clear_editext_with_wrong_value(view.editxt_for_make_new_login_email)

                                }

                                //처리 끝났으므로, 다이얼로그 꺼줌.
                                loadingDialog.dismiss_dialog()
                            }//onResponse() 끝

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.v("check_app_runnig_status","${log_for_class}에서 아이디 중복 체크 에러남 결과 -> $t")


                                Toast.makeText(activity,"서버에 문제 발생 erro -3",Toast.LENGTH_SHORT).show()

                                //edit text처리
                                make_clear_editext_with_wrong_value(view.editxt_for_make_new_login_email)

                                //처리 끝났으므로, 다이얼로그 꺼줌.
                                loadingDialog.dismiss_dialog()

                            }//onFailure() 끝


                        })

                }//이메일 중복 체크 안되어 있을때


            }else{//이메일 형식이 아닌 경우

                Log.v("check_app_runnig_status","${log_for_class}에서 이메일 형식 아님")

                Toast.makeText(activity,"이메일 형식이 아닙니다. ",Toast.LENGTH_SHORT).show()
                make_clear_editext_with_wrong_value(view.editxt_for_make_new_login_email)

            }

        }//아이디 중복 체크 버튼 클릭됨


        view.btn_for_dupulicate_check_new_nickname.setOnClickListener {
            Log.v("check_app_runnig_status","${log_for_class}에서 닉네임 중복 확인 리스너  클릭됨")

        }



        //그래서 해당 구별 값을 생성자를 통해 보내줌. 0-> sns 회원가입 1-> 이메일 회원가입
        if(check_sns_or_email==0){//-> sns  회원 가입일때는  닉네임만  적으면 됨.

            //로그인 이메일, 비밀번호, 비밀번호 체크  모두 gone
            view.txt_for_make_new_login_email.visibility=View.GONE
            view.txt_for_make_login_pwd.visibility=View.GONE
            view.linearlayout_for_add_new_login_email.visibility=View.GONE
            view.linearlayout_for_add_new_login_pwd.visibility=View.GONE
            view.linearlayout_for_double_check_new_login_pwd.visibility=View.GONE

        }else if(check_sns_or_email==1){//이메일 회원가입일때



        }



        //사용할 패스워드 입력하는 editext 텍스트 watcher
        view.editxt_for_add_new_pwd.addTextChangedListener(object : TextWatcher{

            //text change 이전
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.v("checkadsda_before",s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.v("checkadsda_textchanged",s.toString())

            }

            override fun afterTextChanged(s: Editable?) {

                Log.v("checkadsda_after",s.toString())
                val boolean:Boolean=check_pws_regex_funtion(s.toString())

                if(boolean){

                    Log.v("cdddd","비밀번호 가능")
                }else{
                    Log.v("cdddd","비밀번호 불가능")

                }

            }




        })



        return view

    }//oncreateview 끝


    //비밀번호  정규식 체크 하는 기능
    fun check_pws_regex_funtion(password:String):Boolean{

        //영어랑  숫자 조합 7자리까지만 가능하다.  -> 영어가 먼저 들어가야됨
        val expression:String= "^[a-zA-Z](?=.{0,28}[0-9])[0-9a-zA-Z]{6}$"
        val pattern:Pattern= Pattern.compile(expression,Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(password)

        //true false 형태로 결과 값 return 함
       return matcher.matches()
    }


    //이메일 형식  체크 하는 기능
    fun check_email_regex_function(email:String):Boolean{

         //이메일 형식 맞는 지 확인 하는 정규식
         val expression:String ="^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"

         val pattern:Pattern= Pattern.compile(expression,Pattern.CASE_INSENSITIVE)
         val matcher: Matcher = pattern.matcher(email)

      return matcher.matches()
    }


    //이메일 중복 확인이랑 닉네임 중복 체크 할때 값이 잘못되면  해주는 처리
    //동일한 처리여서  메소드 하나 따로 만듬.
    fun make_clear_editext_with_wrong_value(editText: EditText){

        //틀렸다는 애니메이션 주고, clear 한다음에 focus 처리
        editText.startAnimation(shake)
        editText.requestFocus()
        editText.text.clear()
    }


    override fun onPause() {
        super.onPause()
        Log.v("check_app_runnig_status","TermPagerThirdFragment onPause 실행 됨")


        //맨처음 포커싱 해서  키보드 올려주는  editext만  hide 를  적용
        //왜냐면, 애네는  inputmanager로  키보드를  올려놓은 상태이기 때문에
        //pause 단계에서도  그대로 유지가 되어있는 중이다.
        //그래서 혹시나  맨처음  키보드 올린상태에서 더이상 키보드 전환 과정이 없다면,
        //아래 코드로  적용  hide가 적용된다.
        if(check_sns_or_email==0){//sns 로그인시 -> 닉네임에  자동 포커스 되어있다.

            mInputMethodManager.hideSoftInputFromWindow(view!!.editxt_for_add_new_nickname.windowToken, 0);

        }else if(check_sns_or_email==1){//이메일 로그인시  -> 로그인 이메일 적는 곳에  자동 포커스 되어있음.

            mInputMethodManager.hideSoftInputFromWindow(view!!.editxt_for_make_new_login_email.windowToken, 0);

        }


    }//onpause() 끝



}//프래그먼트 끝.