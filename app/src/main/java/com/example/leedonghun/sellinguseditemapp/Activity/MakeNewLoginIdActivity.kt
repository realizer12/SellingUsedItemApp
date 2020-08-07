package com.example.leedonghun.sellinguseditemapp.Activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.leedonghun.sellinguseditemapp.Adapter.MakeNewLoginIdPagerAdapter
import com.example.leedonghun.sellinguseditemapp.Interface.CheckMakeIdPagerCompleteStatus
import com.example.leedonghun.sellinguseditemapp.Interface.NewMemberInfo
import com.example.leedonghun.sellinguseditemapp.PrivateInfo.ServerIp
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import com.example.leedonghun.sellinguseditemapp.Util.KeyboardVisibilityUtils
import kotlinx.android.synthetic.main.make_login_id_activity.*
import kotlinx.android.synthetic.main.make_login_id_activity.arrow_btn_for_back_to_login_activity
import kotlinx.android.synthetic.main.term_pager_second_fragment.*
import kotlinx.android.synthetic.main.term_pager_third_fragment.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * SellingUsedItemApp
 *
 * Class: MakeNewEmailid.
 * Created by leedonghun.
 * Created On 2020-06-15.
 *
 * Description:  새로운 이메일 로그인용 아이디를 만드는 엑티비티이다.
 * 총 4가지의  스텝으로  회원의  정보를  받아  회원가입을 진행하며,
 * 각각의 단계는 프래그먼트로 구성되어있으며,
 * 뷰페이져를 통해 실행되고, 각각 단계 요구사항을  완벽히 이행했을 경우에
 * 뷰페이져가 넘어간다.
 */

class MakeNewLoginIdActivity :AppCompatActivity(),CheckMakeIdPagerCompleteStatus,NewMemberInfo {


     //다음 프래그먼트로 넘어갈수 있는지 여부를 판단
     private var check_to_available_or_not:Boolean=false

    //키보드 visible 판단해주는  클래스
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    //회원가입  step을 위한  뷰페이져
    private  lateinit var  term_pager: ViewPager2

    //뷰페이져 포지션 값 -> 초기값 0
    private var current_pager_positon:Int=0

    private var check_sns_or_email:Int = 0



    //멤버 패스워드 256 단방향 암호화 hash와  sort값 들어가 json
    private var memberInfo_for_upload:JSONObject= JSONObject()


    //retrofit 객체
    val retrofitClient:RetrofitClient= RetrofitClient(ServerIp.baseurl)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.make_login_id_activity)
        Log.v("check_app_runnig_status",localClassName+"의 onCreate() 실행 됨")


        //키보드 관련 input 매니져
        val mInputMethodManager:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

       //MainLoginActivity에서 email 또는 sns 회원가입 여부 체크 값을 받아옴.
       //값이  1일 경우 ->  email 로그인 회원 가입 ,   0일 경우 -> sns 로그인 회원가입이다.
       val intent_for_checking_sns_or_email_makeid=intent
       check_sns_or_email=intent_for_checking_sns_or_email_makeid.getIntExtra("check_sns_or_email",-1)
       Log.v("check_app_runnig_status",localClassName+"의 넘어온 회원가입 종류 체크 값->"+check_sns_or_email)

        //뷰에 흔들림 효과를 주는 애니메이션
        val shake:Animation=AnimationUtils.loadAnimation(this,R.anim.shake)

       //이용 약관  ->  viewpager와  연결 시켜줌.
       term_pager=viewPager_for_make_new_id
       term_pager.adapter =MakeNewLoginIdPagerAdapter(this,check_sns_or_email,this)

       //현재 포지션 0으로 지정함.
       term_pager.setCurrentItem(current_pager_positon,true)

       //유저가 뷰페이져를  터치로 넘길수 없게 설정.
       term_pager.isUserInputEnabled=false



        //다음 버튼 클릭 이벤트
        btn_for_check_status_in_make_login_id_activity.setOnClickListener {


            //정보 입력 여부가 true일때만 다음으로 넘어갈수 있다.
            if(check_to_available_or_not) {
                Log.v("check_app_runnig_status",localClassName+"에서  다음 버튼 눌림  = 입렵폼  모두 입력 됨->true")


                //다음으로 넘어가야 함으로 포지션 값을  1씩 올려준다.
                current_pager_positon++


                Log.v(
                    "check_app_runnig_status",
                    localClassName + "에서  뷰페이져 현재 포지션->$current_pager_positon"
                )

                //현재 포지션이 itemcount 보다 작을때 -> 0,1,2,3 일떄
                if (current_pager_positon < (term_pager.adapter as MakeNewLoginIdPagerAdapter).itemCount) {



                    if (current_pager_positon == 1) {


                        term_pager.setCurrentItem(current_pager_positon, true)
                        txt_for_show_number_of_status.text = "${current_pager_positon + 1}/4"

                        txt_for_show_make_id_status.text = "휴대폰 인증"

                        //다시 회원가입 정보 입력 완성도 체킹 여부 false 로 바꿈.
                        check_to_available_or_not = false

                        //넘어갈수 없다는걸  표현하기 위해서 버튼 색깔 흐리게 바꿔줌.
                        btn_for_check_status_in_make_login_id_activity.background=ContextCompat.getDrawable(this,R.drawable.custom_btn_for_no_radius)

                    } else if (current_pager_positon == 2) {


                        term_pager.setCurrentItem(current_pager_positon, true)
                        txt_for_show_number_of_status.text = "${current_pager_positon + 1}/4"

                        txt_for_show_make_id_status.text = "회원 가입"


                        //다시 회원가입 정보 입력 완성도 체킹 여부 false 로 바꿈.
                        check_to_available_or_not = false

                        //넘어갈수 없다는걸  표현하기 위해서 버튼 색깔 흐리게 바꿔줌.
                        btn_for_check_status_in_make_login_id_activity.background=ContextCompat.getDrawable(this,R.drawable.custom_btn_for_no_radius)

                    } else if (current_pager_positon == 3) {


                        AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setMessage("여기까지 입력한 정보로 \n회원가입이 진행됩니다\n\n회원가입을 진행하시겠습니까??\n\n" +
                                    "\n※ 다음 페이지는 사용자              ※" +
                                    "\n※ 선호 상품 카테고리 조사입니다      ※" +
                                    "\n※ 회원가입 후 꼭 해야하는 조사입니다  ※")
                            .setPositiveButton("네"){ dialog, which ->//이경우 현재  정한 페이지로,



                                 retrofitClient.apiService.upload_new_member_info(memberInfo_for_upload).enqueue(object :Callback<ResponseBody>{

                                     override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                                          Log.v("check_member_info",response.body()?.string())

                                         dialog.dismiss()


                                     }//onResponse() 끝


                                     override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                         Log.v("check_member_info",t.message)

                                     }//onFailure() 끝

                                 })


                                //다음 선호도 조사 페이지로 넘어가줌.
                                term_pager.setCurrentItem(current_pager_positon, true)
                                txt_for_show_number_of_status.text = "${current_pager_positon + 1}/4"

                                txt_for_show_make_id_status.text = "선호하는 상품 카테고리"


                                //다시 회원가입 정보 입력 완성도 체킹 여부 false 로 바꿈.
                                check_to_available_or_not = false


                                //마지막 포지션이므로  버튼 텍스트를 완료 텍스트로 바꿔줌.
                                btn_for_check_status_in_make_login_id_activity.text = "완 료"


                                //넘어갈수 없다는걸  표현하기 위해서 버튼 색깔 흐리게 바꿔줌.
                                btn_for_check_status_in_make_login_id_activity.background=ContextCompat.getDrawable(this,R.drawable.custom_btn_for_no_radius)



                            }
                            .setNegativeButton("아니오"){ dialog, which ->

                                //넘어가지 않았으니까 다시  뷰페이져 포지션 2로 지정한다.
                                current_pager_positon=2

                                dialog.dismiss()


                            }.show()


                    }

                }else{//이제 완료 버튼을 눌러서  끝날때이다. 이때  서버에  회원 정보를  모두 저장한다.


                    //완료 버튼이  눌렸을때,


                }

            }else{
                Log.v("check_app_runnig_status",localClassName+"에서  다음 버튼 눌림  = 입렵폼  덜 입력됨->false")
                Toast.makeText(this,"혹시 빠트린게 없나요?",Toast.LENGTH_SHORT).show()
            }
        }//다음 버튼 클릭 이벤트





        //뒤로가기 버튼 클릭 이벤트
        arrow_btn_for_back_to_login_activity.setOnClickListener {

            Log.v("check_app_runnig_status",localClassName+"의 뒤로 가기 버튼 클릭 이벤트")


            if(current_pager_positon<3){


                //다이얼로그로  작성된 내용 저장되지 않는다고 말해주는 거 띄우기
                AlertDialog.Builder(this)
                    .setMessage("뒤로가기를 누르면,\n회원가입 취소로 간주됩니다.\n정말 뒤로 가시겠습니까??")
                    .setCancelable(false)
                    .setPositiveButton("네"){dialog, which ->

                        dialog.dismiss()
                        finish()//취소하면  회원가입을  취소-> 메인으로 돌아가기

                    }
                    .setNegativeButton("아니오"){dialog, which ->

                        dialog.dismiss()

                    }.show()

            }else{

                //다이얼로그로  작성된 내용 저장되지 않는다고 말해주는 거 띄우기
                AlertDialog.Builder(this)
                    .setMessage("선호 상품 카테고리 선택은 \n꼭 해야하는 절차입니다.\n지금 안하더라도,\n로그인시 하셔야 됩니다.\n\n그냥 넘어 가시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("네"){dialog, which ->

                        dialog.dismiss()
                        finish()//취소하면  회원가입을  취소-> 메인으로 돌아가기

                    }
                    .setNegativeButton("아니오"){dialog, which ->

                        dialog.dismiss()

                    }.show()


            }



        }//뒤로가기 버튼 클릭 이벤트





        //뷰페이져  페이지 전환  리스너(결과 값 콜백 메소드)
        term_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                Log.v("check_app_runnig_status",localClassName+"뷰 페이지 체인지됨  현재  페이져 포지션 ->"+position)

                if(position==1||position==2){//1= sms 인증하는 페이지 , 2= 회원가입 페이지

                    //inputmethodmanager ->  소프트 키보드 관련 조작 담당
                    mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

                }

            }
        })



        //키보드 shown /hide 여부 감지
        keyboardVisibilityUtils= KeyboardVisibilityUtils(window,

            // 키보드가 올라올 때의 동작
            onShowKeyboard = { keyboardHeight, visibleDisplayFrameHeight ->
                Log.v("check_app_runnig_status", localClassName+"에서 키보드 올라옴")

                //뷰페이져가  sms 인증  프래그먼트를 실행했을때
                //프래그먼트에  키보드가 올라오는데,  edittext에  포커스가 사라져서 이렇게 하기로 함.
                if(term_pager.currentItem==1){

                    //맨처음  번호 입력 editext에  아무것도 안적혀있다면,
                    //포커스를  준다.
                    if(editxt_for_add_phone_number.length()<=0){
                        editxt_for_add_phone_number.requestFocus()
                    }

                }//sms 인증 프래그먼트 실행시
                else if(term_pager.currentItem==2){

                    when(check_sns_or_email){//로그인 방법으로 나눔.-> 각각 처음 자동 포커스 되는  edittext 가 다르기 때문.

                        0->{//sns로그인 일때

                            if(editxt_for_add_new_nickname.length()<=0){//sns 로그인 일때는  닉네임에  자동 포커스

                                editxt_for_add_new_nickname.requestFocus()
                            }
                        }//sns 로그인 끝


                        1->{//이메일 로그인일때

                            if(editxt_for_make_new_login_email.length()<=0){// 이메일 로그인 일때는 이메일 입력에 자동포커스

                                //이메일 로그인에 입력을 위한 자동포커스이므로 애니메이션으로
                                //이곳을 입력하라고 알려준다.
                                linearlayout_for_add_new_login_email.startAnimation(shake)
                                editxt_for_make_new_login_email.requestFocus()

                            }
                        }//이메일 로그인일때 끝.

                    }//when끝

                }// 회원가입 프래그먼트 실행시  끝

            },//키보드 올라갈때 이벤트 끝

            // 키보드가 내려갈 때의 동작
            onHideKeyboard = {
                Log.v("check_app_runnig_status", localClassName+"에서 키보드 내려감")


            }//키보드 내려갈때 이벤트 끝,

        )//키보드 감지 끝.

    }//onCreate()끝


    //뒤로가기 버튼 클릭 이벤트
    override fun onBackPressed() {

        Log.v("check_app_runnig_status",localClassName+"의 뒤로 가기 버튼 클릭 이벤트")

        if(current_pager_positon<3){


            //다이얼로그로  작성된 내용 저장되지 않는다고 말해주는 거 띄우기
            AlertDialog.Builder(this)
                .setMessage("뒤로가기를 누르면,\n회원가입 취소로 간주됩니다.\n정말 뒤로 가시겠습니까??")
                .setCancelable(false)
                .setPositiveButton("네"){dialog, which ->

                    dialog.dismiss()
                    finish()//취소하면  회원가입을  취소-> 메인으로 돌아가기

                }
                .setNegativeButton("아니오"){dialog, which ->

                    dialog.dismiss()

                }.show()

        }else{

            //다이얼로그로  작성된 내용 저장되지 않는다고 말해주는 거 띄우기
            AlertDialog.Builder(this)
                .setMessage("선호 상품 카테고리 선택은 \n꼭 해야하는 절차입니다.\n지금 안하더라도,\n로그인시 하셔야 됩니다.\n\n그냥 넘어 가시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네"){dialog, which ->

                    dialog.dismiss()
                    finish()//취소하면  회원가입을  취소-> 메인으로 돌아가기

                }
                .setNegativeButton("아니오"){dialog, which ->

                    dialog.dismiss()

                }.show()


        }


    }



    //각 프래그먼트별  완성 여부 받음.
    override fun CheckMakeIdPagerComplete_all_or_not(status: Boolean,check_page_number:Int) {

       when(check_page_number){

           //1,2,3 페이지 일때
           1,2,3-> {
               //status -> true 이때는 다음 페이지로 넘어갈수 있음.
               if(status){

                   //넘어갈수 있다는걸  표현하기 위해서 버튼 색깔 진하게 바꿔줌.
                   btn_for_check_status_in_make_login_id_activity.background=ContextCompat.getDrawable(this,R.drawable.custom_btn_for_no_radius_with_complete_check_color)
                   check_to_available_or_not=status

               }else{//status-> false 일때는 다음 페이지로 넘어갈 수 없음.

                   //넘어갈수 없다는걸  표현하기 위해서 버튼 색깔 흐리게 바꿔줌.
                   btn_for_check_status_in_make_login_id_activity.background=ContextCompat.getDrawable(this,R.drawable.custom_btn_for_no_radius)
                   check_to_available_or_not=status

               }
           }


           4->{//네번째 페이지 일때


           }

       }//when  끝


    }//CheckMakeIdPagerComplete_all_or_not끝

    //회원가입 세번째 프래그먼트에서 새 회원 정보 입력이 모두 끝나면,
    //그 정보를 json으로 받아오는 역할을 한다.
    override fun new_member_info_with_json(memberInfo: JSONObject) {

        Log.v("checkjson_info",memberInfo.toString())

        //서버로 업로드할  멤버 정보 json으로 받음.
        memberInfo_for_upload=memberInfo


    }//new_member_info_with_json 끝


}//MakeNewEmailLoginId 클래스 끝