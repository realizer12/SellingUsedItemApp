package com.example.leedonghun.sellinguseditemapp.Activity

import android.app.AlertDialog
import android.content.Context
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
import com.example.leedonghun.sellinguseditemapp.Interface.CheckMakeIdPagerCompleteStatus
import com.example.leedonghun.sellinguseditemapp.Interface.NewMemberInfo
import com.example.leedonghun.sellinguseditemapp.PrivateInfo.ServerIp
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import com.example.leedonghun.sellinguseditemapp.Singleton.AuthPoneNum
import com.example.leedonghun.sellinguseditemapp.Util.MakePassWordSecurity
import kotlinx.android.synthetic.main.term_pager_third_fragment.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    //가입하려는 멤버  이메일
    lateinit var member_email:String

    //가입하려는 닉네임
    lateinit var member_nickname:String


    //비밀번호  256해쉬값 만드는 클래스 객체
    val make_hash256_and_sort_value:MakePassWordSecurity=MakePassWordSecurity()

    //retrofir 통신을 하기위한 클래스 객체
    val retrofitClient:RetrofitClient= RetrofitClient(ServerIp.baseurl)

    //뷰에 흔들림 효과를 주는 애니메이션
    val shake: Animation = AnimationUtils.loadAnimation(context,R.anim.shake)

    //sms 인증시 띄어줄  커스텀 다이얼로그 -로딩용
    val loadingDialog: LoadingDialog = LoadingDialog(context)



    //로그인 이메일 중복 체크 여부 -> default 값 false
    var check_duplicate_login_email:Boolean=false

    //비밀 번호  체크  여부 -> default 값 false
    var check_pws_double_check_status:Boolean=false

    //닉네임 중복 체크 여부 -> default 값 false
    var check_duplicate_nickname:Boolean=false





    //비밀번호 정규식 입력 판단 결과  default 값 false
    var check_pws_regex_result:Boolean = false

    //적은  비밀번호
    lateinit var written_password:String

    //현재  프래그먼트에서 요구하는 사항들 모두 진행 했는지 체크해서
    //parent layout로 값 보내는 인터페이스
    lateinit var check_complete: CheckMakeIdPagerCompleteStatus


    //멤버 정보 취합 위한 인터페이스
    lateinit var member_info: NewMemberInfo


    //member 정보들 취합할 json 객체
    var member_info_json:JSONObject= JSONObject()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {


        //프래그먼트 뷰 연결
        val view: View = inflater.inflate(R.layout.term_pager_third_fragment, container, false)

        Log.v("check_app_runnig_status","TermPagerThirdFragment onCreateView 실행 됨 -> sns 로그인 여부 0=sns , 1=이메일    결과=> $check_sns_or_email")




        //그래서 해당 구별 값을 생성자를 통해 보내줌. 0-> sns 회원가입 1-> 이메일 회원가입
        if(check_sns_or_email==0){//-> sns  회원 가입일때는  닉네임만  적으면 됨.

            //로그인 이메일, 비밀번호, 비밀번호 체크  모두 gone
            view.txt_for_make_new_login_email.visibility=View.GONE
            view.txt_for_make_login_pwd.visibility=View.GONE
            view.linearlayout_for_add_new_login_email.visibility=View.GONE
            view.linearlayout_for_add_new_login_pwd.visibility=View.GONE
            view.linearlayout_for_double_check_new_login_pwd.visibility=View.GONE

        }


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


                            //요구사항 입력 여부  체크
                            detect_status_of_requirement_info(check_duplicate_login_email,check_pws_regex_result,check_pws_double_check_status,check_duplicate_nickname)


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

                                    //중복 체크 실패니까 false로
                                    check_duplicate_login_email=false


                                }else if(result.equals("-1")){//쿼리 실패함
                                    Log.v("check_app_runnig_status","${log_for_class}의 이메일 중복 쿼리문 실패")

                                    Toast.makeText(activity,"서버에 문제 발생 erro -1",Toast.LENGTH_SHORT).show()

                                    //edit text처리
                                    make_clear_editext_with_wrong_value(view.editxt_for_make_new_login_email)


                                    //중복 체크 실패니까 false로
                                    check_duplicate_login_email=false

                                }

                                //요구사항 입력 여부  체크
                                detect_status_of_requirement_info(check_duplicate_login_email,check_pws_regex_result,check_pws_double_check_status,check_duplicate_nickname)

                                //처리 끝났으므로, 다이얼로그 꺼줌.
                                loadingDialog.dismiss_dialog()
                            }//onResponse() 끝

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.v("check_app_runnig_status","${log_for_class}에서 아이디 중복 체크 에러남 결과 -> ${t.message}")


                                Toast.makeText(activity,"서버에 문제 발생 erro -3",Toast.LENGTH_SHORT).show()

                                //edit text처리
                                make_clear_editext_with_wrong_value(view.editxt_for_make_new_login_email)

                                //중복 체크 실패니까 false로
                                check_duplicate_login_email=false

                                //요구사항 입력 여부  체크
                                detect_status_of_requirement_info(check_duplicate_login_email,check_pws_regex_result,check_pws_double_check_status,check_duplicate_nickname)


                                //처리 끝났으므로, 다이얼로그 꺼줌.
                                loadingDialog.dismiss_dialog()

                            }//onFailure() 끝


                        })

                }//이메일 중복 체크 안되어 있을때


            }else{//이메일 형식이 아닌 경우

                Log.v("check_app_runnig_status","${log_for_class}에서 이메일 형식 아님")

                Toast.makeText(activity,"이메일 형식이 아닙니다. ",Toast.LENGTH_SHORT).show()
                make_clear_editext_with_wrong_value(view.editxt_for_make_new_login_email)

                //중복 체크 실패니까 false로
                check_duplicate_login_email=false

                //요구사항 입력 여부  체크
                detect_status_of_requirement_info(check_duplicate_login_email,check_pws_regex_result,check_pws_double_check_status,check_duplicate_nickname)


            }


        }//아이디 중복 체크 버튼 클릭됨







        //사용할 패스워드 입력하는 editext 텍스트 watcher
        view.editxt_for_add_new_pwd.addTextChangedListener(object : TextWatcher{
            //text change 이전
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.v("check_app_runnig_status","패스워드 입력 beforetextchanged 실행 -> $s")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.v("check_app_runnig_status","패스워드 입력 onTextChanged 실행 -> $s")

            }
            //edit된 상태의  텍스트를  감지해야하므로  여기에다가
            //정규식 체크 함수를 넣어줌.
            override fun afterTextChanged(s: Editable?) {
                Log.v("check_app_runnig_status","패스워드 입력 afterTextChanged 실행 -> $s")

                 //check_pws_regex_funtion 리턴값으로  change된  텍스트 값 넣어주고 확인
                check_pws_regex_result=check_pws_regex_funtion(s.toString())

                //사용이 가능할때
                if(check_pws_regex_result){
                    Log.v("check_app_runnig_status","$log_for_class 의  비밀번호 =>$s   정규식 체크 ->  사용 가능")

                    view.txt_for_show_new_pwd_available_or_not.text="사용 가능"
                    view.txt_for_show_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_green_bacground)

                }else{//사용이 불가능 할때
                    Log.v("check_app_runnig_status","$log_for_class 의  비밀번호 =>$s   정규식 체크 ->  사용 불가능")

                   if( view.editxt_for_add_new_pwd.length()>0){//뭐라도 하나 써있을 경우

                       view.txt_for_show_new_pwd_available_or_not.text="사용 불가"
                       view.txt_for_show_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_red_background)

                   }else{//아무것도 안써있을때

                       view.txt_for_show_new_pwd_available_or_not.text="입력 대기 중"
                       view.txt_for_show_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_login_btn_for_email_login_in_email_login_activity)
                   }
                }
                //혹시나  패스워드 입력하고,  다시 입력도 한 상태에서
                //비밀번호 입력을 바꿀시에는 다시 입력한곳도 바뀌여야 한다.
                //그래서 아래와 같이  코드를  더 추가시켜줌.
                //입력한 패스워드
                written_password= view.editxt_for_add_new_pwd.text.toString()

                //더블 체크 값이랑  입력한 패스워드가 일치 할 경우
                if(written_password == view.editxt_for_double_check_new_pwd.text.toString()){

                    Log.v("check_app_runnig_status","$log_for_class 의  비밀번호 더블 체크  일치 함")

                    view.txt_for_show_double_check_new_pwd_available_or_not.text="일치"
                    view.txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_green_bacground)

                    check_pws_double_check_status=true

                }else{//일치 하지 않을 경우

                    //일치 하지 않지만 뭐라도 쓴 경우
                    if(view.editxt_for_double_check_new_pwd.length()>0){

                        Log.v("check_app_runnig_status","$log_for_class 의  비밀번호 더블 체크  일치 하지 않음")
                        view.txt_for_show_double_check_new_pwd_available_or_not.text="불일치"
                        view.txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_red_background)


                    }else{//일치 하지 않고 아무것도 안쓴 경우
                        Log.v("check_app_runnig_status","$log_for_class 의  비밀번호 더블 체크 아무것도 안 써져잇음")

                        view.txt_for_show_double_check_new_pwd_available_or_not.text="입력 대기 중"
                        view.txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_login_btn_for_email_login_in_email_login_activity)

                    }

                    check_pws_double_check_status=false

                } //입력한  패스워드  수정에 따른   다시 입력하는 패스워드 수정

                //요구사항 입력 여부  체크
                detect_status_of_requirement_info(check_duplicate_login_email,check_pws_regex_result,check_pws_double_check_status,check_duplicate_nickname)

            }
        })//패스워드 입력 editext 텍스트 watcher 끝







        //패스워드  더블 체크  하는 부분  텍스트  watcher
        view.editxt_for_double_check_new_pwd.addTextChangedListener(object :TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.v("check_app_runnig_status","패스워드 더블 체크 입력 beforetextchanged 실행 -> $s")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.v("check_app_runnig_status","패스워드 더블 체크 입력 onTextChanged 실행 -> $s")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.v("check_app_runnig_status","패스워드 더블 체크 입력 afterTextChanged 실행 -> $s")

                //입력한 패스워드
                written_password= view.editxt_for_add_new_pwd.text.toString()

                //더블 체크 값이랑  입력한 패스워드가 일치 할 경우
                if(written_password == view.editxt_for_double_check_new_pwd.text.toString()){

                    Log.v("check_app_runnig_status","$log_for_class 의  비밀번호 더블 체크  일치 함")
                    view.txt_for_show_double_check_new_pwd_available_or_not.text="일치"
                    view.txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_green_bacground)

                    check_pws_double_check_status=true

                }else{//일치 하지 않을 경우


                    //일치 하지 않지만 뭐라도 쓴 경우
                    if(view.editxt_for_double_check_new_pwd.length()>0){
                        Log.v("check_app_runnig_status","$log_for_class 의  비밀번호 더블 체크  일치 하지 않음")
                        view.txt_for_show_double_check_new_pwd_available_or_not.text="불일치"
                        view.txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_view_radius_with_red_background)
                    }else{//일치 하지 않고 아무것도 안쓴 경우
                        Log.v("check_app_runnig_status","$log_for_class 의  비밀번호 더블 체크 아무것도 안 써져잇음")
                        view.txt_for_show_double_check_new_pwd_available_or_not.text="입력 대기 중"
                        view.txt_for_show_double_check_new_pwd_available_or_not.setBackgroundResource(R.drawable.custom_login_btn_for_email_login_in_email_login_activity)

                    }

                    check_pws_double_check_status=false


                }
                //요구사항 입력 여부  체크
                detect_status_of_requirement_info(check_duplicate_login_email,check_pws_regex_result,check_pws_double_check_status,check_duplicate_nickname)

            }

        })//패스워드 더블 체크  텍스트 watcher 끝



        //닉네임 중복 체크 버튼
        view.btn_for_dupulicate_check_new_nickname.setOnClickListener {
             
            Log.v("check_app_runnig_status", "$log_for_class 의 닉네임 중복 체크 버튼 클릭 됨")

            //사용자가 사용하려는  닉네임
            member_nickname=view.editxt_for_add_new_nickname.text.toString()


            //닉네임 체크가 되어있을 경우
            if(check_duplicate_nickname){

                //alert를  띄어서 취소 여부를  물어본다.
                AlertDialog.Builder(activity)
                    .setMessage("닉네임을 정말 바꾸시겠습니까??")
                    .setPositiveButton("네"){dialog, which ->

                        Log.v("check_app_runnig_status" ,"$log_for_class 닉네임 취소 ok 버튼  눌림")

                        //버튼 text변경
                        view.btn_for_dupulicate_check_new_nickname.text="중복 확인"

                        //닉네임 입력 막음
                        view.editxt_for_add_new_nickname.isEnabled=true

                        //사용 가능함을  초록색으로 보여줌.
                        view.linearlayout_for_add_new_nickname.setBackgroundResource(R.drawable.custom_view_radius_with_white_background)

                        //중복체크 여부 다시 false로
                        check_duplicate_nickname=false

                        //요구사항 입력 여부  체크
                        detect_status_of_requirement_info(check_duplicate_login_email,check_pws_regex_result,check_pws_double_check_status,check_duplicate_nickname)

                        dialog.dismiss()

                    }//setpositivebutton 끝

                    .setNegativeButton("아니오"){dialog, which ->

                        Log.v("check_app_runnig_status" ,"$log_for_class 닉네임 취소 취소 버튼  눌림")

                        dialog.dismiss()

                    }.show()

            }else{//닉네임 체크가  아직 안되어있을 경우

                Log.v("check_app_runnig_status","$log_for_class 의 닉네임 중복 체크 상태 false")

                //중복확인 동안  다이얼로그 보여줌.
                loadingDialog.show_dialog()

                //닉네임 서버로 보내서  중복 체크
                retrofitClient.apiService.check_duplicate_user_nickname(member_nickname)
                    .enqueue(object :Callback<ResponseBody>{

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                           //닉네임 중복 체크 결과 값
                           val result:String?=response.body()?.string()
                            Log.v("check_app_runnig_status","$log_for_class 의  닉네임 중복체크 결과 값-> $result")

                           when(result){

                               "1"->{//중복값 없음
                                   Log.v("check_app_runnig_status","$log_for_class 의  닉네임 중복체크 결과 -> 사용 가능")
                                   Toast.makeText(activity,"사용 가능한 닉네임 입니다.",Toast.LENGTH_SHORT).show()

                                   //버튼 text변경
                                   view.btn_for_dupulicate_check_new_nickname.text="다시 입력"

                                   //닉네임 입력 막음
                                   view.editxt_for_add_new_nickname.isEnabled=false
                                   //사용 가능함을  초록색으로 보여줌.
                                   view.linearlayout_for_add_new_nickname.setBackgroundResource(R.drawable.custom_view_radius_with_green_bacground)


                                   //중복체크 여부 true
                                   check_duplicate_nickname=true

                               }//1일때 끝

                               "-2"->{//중복 있음
                                   Log.v("check_app_runnig_status","$log_for_class 의  닉네임 중복체크 결과 -> 중복값 있음")
                                   Toast.makeText(activity,"이미 사용중인 닉네임 입니다.",Toast.LENGTH_SHORT).show()

                                   //중복체크 여부 false
                                   check_duplicate_nickname=false

                               }//-2 일때 끝

                               "-1"->{//쿼리 에러
                                   Log.v("check_app_runnig_status","$log_for_class 의  닉네임 중복체크 결과 -> 서버 쿼리 에러")
                                   Toast.makeText(activity,"서버 에러 code 1",Toast.LENGTH_SHORT).show()

                                   //중복체크 여부 false
                                   check_duplicate_nickname=false

                               } //-1 일때 끝

                               else ->{//서버 에러
                                   Log.v("check_app_runnig_status","$log_for_class 의  닉네임 중복체크 결과 -> 서버 에러")
                                   Toast.makeText(activity,"서버 에러 code 2",Toast.LENGTH_SHORT).show()

                                   //중복체크 여부 false
                                   check_duplicate_nickname=false

                               }//그밖에 끝

                           }//when() 절 끝


                            //중복확인 끝났으니  다이얼로그 없애줌
                            loadingDialog.dismiss_dialog()


                            //요구사항 입력 여부  체크
                            detect_status_of_requirement_info(check_duplicate_login_email,check_pws_regex_result,check_pws_double_check_status,check_duplicate_nickname)


                        }//onResponse() 끝


                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                             Log.v("check_app_runnig_status","$log_for_class 의  닉네임 중복체크 결과  서버  에러-> ${t.message}")


                            //중복확인 끝났으니  다이얼로그 없애줌
                            loadingDialog.dismiss_dialog()

                            //중복체크 여부 false
                            check_duplicate_nickname=false

                            //요구사항 입력 여부  체크
                            detect_status_of_requirement_info(check_duplicate_login_email,check_pws_regex_result,check_pws_double_check_status,check_duplicate_nickname)


                        }//onFailure() 끝


                    })

            }//이전에 닉네임 중복 체크가 안되어있을 경우


        }//닉네임 중복 체크 버튼  끝

        return view

    }//oncreateview 끝




    //전체 입력사항  체크 해서 parent
    // 엑티비티에 넘어가도 되는지 여부  정보 넘겨준다.
    fun detect_status_of_requirement_info(login_email_status:Boolean,password_status:Boolean,password_double_check:Boolean,nickname_status:Boolean){

        //전체  요구사항들  true -> 사용자가 요구사항대로 입력했을 경우
        if(login_email_status && password_status && nickname_status && password_double_check) {
            Log.v("check_app_runnig_status","$log_for_class 의 요구 입력사항 모두 체크 됨")


            member_info_json.put("login_email",member_email)//멤버 로그인 이메일
            member_info_json.put("nick_name",member_nickname)//멤버 닉네임
            member_info_json.put("password",make_hash256_and_sort_value.make_sha_256_hash_value(view?.editxt_for_add_new_pwd?.text.toString()))//패스워드
            member_info_json.put("phone_num",AuthPoneNum.auth_phonnumber)//핸드폰 번호


            //다음 페이져로 넘어가기 가능함.
            check_complete.CheckMakeIdPagerComplete_all_or_not(true,3)

            //위에서 넣어준 멤버 정보들 넘겨줌.
            member_info.new_member_info_with_json(member_info_json)

        }else{//하나라도  안되어있을 경우

            //다음 페이져로 넘어가기 불가능 함.
            check_complete.CheckMakeIdPagerComplete_all_or_not(false,3)
        }

    }//detect_status_of_requirement_info() 끝



    override fun onAttach(context: Context) {
        super.onAttach(context)

        Log.v("check_app_runnig_status","$log_for_class 의 onAttach() 실행 됨")


        //진행사항  체크 인터페이스 객체 initialize 함.
        if(context is CheckMakeIdPagerCompleteStatus){
            check_complete= context
        }else{
            throw RuntimeException(context.toString())
        }

        //진행사항  체크 인터페이스 객체 initialize 함.
        if(context is  NewMemberInfo){
            member_info= context
        }else{
            throw RuntimeException(context.toString())
        }
    }//onattach() 끝




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