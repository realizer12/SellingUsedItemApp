package com.example.leedonghun.sellinguseditemapp.Activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.leedonghun.sellinguseditemapp.Adapter.MakeNewLoginIdPagerAdapter
import com.example.leedonghun.sellinguseditemapp.Interface.CheckMakeIdPagerCompleteStatus
import com.example.leedonghun.sellinguseditemapp.R
import kotlinx.android.synthetic.main.make_login_id_activity.*


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

class MakeNewLoginIdActivity :AppCompatActivity(),CheckMakeIdPagerCompleteStatus {

     private var check_to_available_or_not:Boolean=false

    //회원가입  step을 위한  뷰페이져
    private  lateinit var  term_pager: ViewPager2

    //뷰페이져 포지션 값 -> 초기값 0
    private var current_pager_positon:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.make_login_id_activity)
        Log.v("check_app_runnig_status",localClassName+"의 onCreate() 실행 됨")


       //MainLoginActivity에서 email 또는 sns 회원가입 여부 체크 값을 받아옴.
       //값이  1일 경우 ->  email 로그인 회원 가입 ,   0일 경우 -> sns 로그인 회원가입이다.
       val intent_for_checking_sns_or_email_makeid=intent
       val check_sns_or_email=intent_for_checking_sns_or_email_makeid.getIntExtra("check_sns_or_email",-1)
       Log.v("check_app_runnig_status",localClassName+"의 넘어온 회원가입 종류 체크 값->"+check_sns_or_email)


       //이용 약관  ->  viewpager와  연결 시켜줌.
       term_pager=viewPager_for_make_new_id
       term_pager.adapter =MakeNewLoginIdPagerAdapter(this,check_sns_or_email)

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

                    term_pager.setCurrentItem(current_pager_positon, true)
                    txt_for_show_number_of_status.text = "${current_pager_positon + 1}/4"

                    if (current_pager_positon == 1) {

                        txt_for_show_make_id_status.text = "휴대폰 인증"

                        //다시 회원가입 정보 입력 완성도 체킹 여부 false 로 바꿈.
                        check_to_available_or_not = false
                        //넘어갈수 없다는걸  표현하기 위해서 버튼 색깔 흐리게 바꿔줌.
                        btn_for_check_status_in_make_login_id_activity.background=ContextCompat.getDrawable(this,R.drawable.custom_btn_for_no_radius)

                    } else if (current_pager_positon == 2) {

                        txt_for_show_make_id_status.text = "회원 가입"


                        //다시 회원가입 정보 입력 완성도 체킹 여부 false 로 바꿈.
                        check_to_available_or_not = false
                        //넘어갈수 없다는걸  표현하기 위해서 버튼 색깔 흐리게 바꿔줌.
                        btn_for_check_status_in_make_login_id_activity.background=ContextCompat.getDrawable(this,R.drawable.custom_btn_for_no_radius)

                    } else if (current_pager_positon == 3) {

                        txt_for_show_make_id_status.text = "선호하는 상품 카테고리"

                        //다시 회원가입 정보 입력 완성도 체킹 여부 false 로 바꿈.
                        check_to_available_or_not = false
                        //넘어갈수 없다는걸  표현하기 위해서 버튼 색깔 흐리게 바꿔줌.
                        btn_for_check_status_in_make_login_id_activity.background=ContextCompat.getDrawable(this,R.drawable.custom_btn_for_no_radius)


                        //마지막 포지션이므로  버튼 텍스트를 완료 텍스트로 바꿔줌.
                        btn_for_check_status_in_make_login_id_activity.text = "완 료"
                    }
                }

            }else{
                Log.v("check_app_runnig_status",localClassName+"에서  다음 버튼 눌림  = 입렵폼  덜 입력됨->false")
                Toast.makeText(this,"요구하는 정보를  입력해주세요!",Toast.LENGTH_SHORT).show()
            }
        }//다음 버튼 클릭 이벤트


        //뒤로가기 버튼 클릭 이벤트
        arrow_btn_for_back_to_login_activity.setOnClickListener {

            Log.v("check_app_runnig_status",localClassName+"의 뒤로 가기 버튼 클릭 이벤트")

            //현재 엑티비티 종료 시킴
            finish()
        }

    }//onCreate()끝




    //각 프래그먼트별  완성 여부 받음.
    override fun CheckMakeIdPagerComplete_all_or_not(status: Boolean,page_number:Int) {

       when(page_number){

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


}//MakeNewEmailLoginId 클래스 끝