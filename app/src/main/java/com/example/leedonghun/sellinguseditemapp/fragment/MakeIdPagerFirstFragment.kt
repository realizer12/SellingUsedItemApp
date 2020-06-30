package com.example.leedonghun.sellinguseditemapp.Activity


import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.Interface.CheckMakeIdPagerCompleteStatus
import com.example.leedonghun.sellinguseditemapp.R
import kotlinx.android.synthetic.main.term_pager_first_fragment.*
import kotlinx.android.synthetic.main.term_pager_first_fragment.view.*
import java.lang.RuntimeException


/**
 * SellingUsedItemApp
 *
 * Class: TermPagerFirstFragment.
 * Created by leedonghun.
 * Created On 2020-06-15.
 *
 * Description:  MakeNewLoginIdActivity에서 뷰페이저에서 맨처음
 * 보이는  프래그먼트 회원 정보를  받아가는 것에 대한  동의를  받기위한
 * 부분이다.  전체 동의를  해야 다음 버튼이 활성화 된다.
 *
 */
class MakeIdPagerFirstFragment : Fragment() {

    //현재  프래그먼트에서 요구하는 사항들 모두 진행 했는지 체크해서
    //parent layout로 값 보내는 인터페이스
    lateinit var check_complete: CheckMakeIdPagerCompleteStatus

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

      //프래그먼트 뷰 연결
      val view: View = inflater.inflate(R.layout.term_pager_first_fragment, container, false)
      Log.v("check_app_runnig_status","TermPagerFirstFragment의 onCreateView 실행 됨")


      //각각 텍스트뷰  스트링열  (필수) 부분을 빨간색으로 처리해줌.
      view.txt_for_policy_term_check.text=make_text_styling(0,7,"이용약관 동의 (필수)")
      view.txt_for_private_info_usable_check.text=make_text_styling(0,15,"개인정보 수집 및 이용 동의 (필수)")


        //전체 동의  아이콘 담은  프레임 레이아웃  클릭이벤트
       view.entire_checking_frame.setOnClickListener {
          Log.v("check_app_runnig_status","전체 동의 체크 프레임 눌림")

           if(entire_checking_checked.visibility==View.INVISIBLE){

               //전체 동의 아이콘 -> check 상태로
               icon_check(entire_checking_checked,entire_checking_unchecked,0)

               //이용약관 동의 -> check 상태로
               icon_check(usable_policy_checked,usable_policy_unchecked,0)

               //개인 정보 수집 및 이용 동의 -> check 상태로
               icon_check(private_info_checked,private_info_unchecked,0)

               //넘어갈수 있는 상태임을 엑티비티에 알림
               check_complete.CheckMakeIdPagerComplete_all_or_not(true,1);



           }else{

               //전체 동의 아이콘 -> uncheck 상태로
               icon_check(entire_checking_checked,entire_checking_unchecked,1)

               //이용약관 동의 -> uncheck 상태로
               icon_check(usable_policy_checked,usable_policy_unchecked,1)

               //개인 정보 수집 및 이용 동의 -> uncheck 상태로
               icon_check(private_info_checked,private_info_unchecked,1)

               //넘어갈수 없는 상태임을 엑티비티에 알림
               check_complete.CheckMakeIdPagerComplete_all_or_not(false,1);

           }

       }//전체 동의 끝

       //이용 약관 동의 프레임 레이아웃 클릭 이벤트
       view.usable_policy_check_frame.setOnClickListener {

           Log.v("check_app_runnig_status","이용 약관 동의 체크 프레임 눌림")

           //전체 동의가 되어있는 경우
           if(entire_checking_checked.visibility==View.VISIBLE){

               //전체 동의 아이콘 -> uncheck 상태로
               icon_check(entire_checking_checked,entire_checking_unchecked,1)

               //이용약관 동의 -> uncheck 상태로
               icon_check(usable_policy_checked,usable_policy_unchecked,1)

               //넘어갈수 없는 상태임을 엑티비티에 알림
               check_complete.CheckMakeIdPagerComplete_all_or_not(false,1);


           }else if(private_info_checked.visibility==View.VISIBLE && usable_policy_checked.visibility==View.INVISIBLE){

               //전체 동의 아이콘 -> check 상태로
               icon_check(entire_checking_checked,entire_checking_unchecked,0)

               //이용약관 동의 -> check 상태로
               icon_check(usable_policy_checked,usable_policy_unchecked,0)

               //개인 정보 수집 및 이용 동의 -> check 상태로
               icon_check(private_info_checked,private_info_unchecked,0)

               //넘어갈수 있는 상태임을 엑티비티에 알림
               check_complete.CheckMakeIdPagerComplete_all_or_not(true,1);


           }else if(private_info_checked.visibility==View.INVISIBLE && usable_policy_checked.visibility==View.VISIBLE) {

               //이용약관 동의 -> uncheck 상태로
               icon_check(usable_policy_checked,usable_policy_unchecked,1)

               //넘어갈수 없는 상태임을 엑티비티에 알림
               check_complete.CheckMakeIdPagerComplete_all_or_not(false,1);



           }else if(private_info_checked.visibility==View.INVISIBLE && usable_policy_checked.visibility==View.INVISIBLE){

               //이용약관 동의 -> check 상태로
               icon_check(usable_policy_checked,usable_policy_unchecked,0)

               //넘어갈수 없는 상태임을 엑티비티에 알림
               check_complete.CheckMakeIdPagerComplete_all_or_not(false,1);

           }


       }



       //개인 정보 수집 및 이용 동의 프레임 레이아웃 클릭 이벤트
        view.private_info_policy_check_frame.setOnClickListener {
            Log.v("check_app_runnig_status","개인 정보 수집 및 이용동의 체크 프레임 눌림")


            //전체 동의가 되어있는 경우
            if(entire_checking_checked.visibility==View.VISIBLE){

                //전체 동의 아이콘 -> uncheck 상태로
                icon_check(entire_checking_checked,entire_checking_unchecked,1)

                //개인 정보 수집 및 이용 동의 -> check 상태로
                icon_check(private_info_checked,private_info_unchecked,1)

                //넘어갈수 없는 상태임을 엑티비티에 알림
                check_complete.CheckMakeIdPagerComplete_all_or_not(false,1);


            }else if(usable_policy_checked.visibility==View.VISIBLE && private_info_checked.visibility==View.INVISIBLE){

                //전체 동의 아이콘 -> check 상태로
                icon_check(entire_checking_checked,entire_checking_unchecked,0)

                //이용약관 동의 -> check 상태로
                icon_check(usable_policy_checked,usable_policy_unchecked,0)

                //개인 정보 수집 및 이용 동의 -> check 상태로
                icon_check(private_info_checked,private_info_unchecked,0)

                //넘어갈수 있는 상태임을 엑티비티에 알림
                check_complete.CheckMakeIdPagerComplete_all_or_not(true,1);

            }else if(usable_policy_checked.visibility==View.INVISIBLE && private_info_checked.visibility==View.VISIBLE) {

                //이용약관 동의 -> uncheck 상태로
                icon_check(private_info_checked,private_info_unchecked,1)

                //넘어갈수 없는 상태임을 엑티비티에 알림
                check_complete.CheckMakeIdPagerComplete_all_or_not(false,1);


            }else if(usable_policy_checked.visibility==View.INVISIBLE && private_info_checked.visibility==View.INVISIBLE){

                //이용약관 동의 -> check 상태로
                icon_check(private_info_checked,private_info_unchecked,0)

                //넘어갈수 없는 상태임을 엑티비티에 알림
                check_complete.CheckMakeIdPagerComplete_all_or_not(false,1);

            }


        }




        return view

    }//onCreateView()끝


    override fun onAttach(context: Context) {
        super.onAttach(context)

        //진행사항  체크 인터페이스 객체 initialize 함.
        if(context is CheckMakeIdPagerCompleteStatus){
            check_complete= context
        }else{
            throw RuntimeException(context.toString())
        }

    }//onAttach() 끝




    //아이콘 체크 visible 상태 바꿔주는 메소드
    fun icon_check(checkicon:ImageView,uncheck_icon:ImageView,checking_status:Int){

        if(checking_status==0){//uncheck일때-> check상태로

            checkicon.visibility=View.VISIBLE
            uncheck_icon.visibility=View.INVISIBLE

        }else if(checking_status==1){//check일때-> uncheck상태로

            checkicon.visibility=View.INVISIBLE
            uncheck_icon.visibility=View.VISIBLE

        }

    }//icon_check() 끝



    //string 열의  부분  색상이 달라야 해서
    //아래와 같은 메소드를 만듬.
    //처음과 끝자리 숫자와 들어가는 string열을 받아서 -> 마지막 (필수) 부분을  빨간색으로
    //처리해주고 나머지 string 열은  검정색으로 처리한 후,  return 한다.
    //return값을  텍스트에 settext해주기만 하면 됨.
   fun make_text_styling(start:Int,end:Int,text:String):SpannableString{

       val stylestring: SpannableString = SpannableString(text)
       stylestring.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(resources,R.color.colorblack,null)),start,end,0)
       stylestring.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(resources,R.color.colorRed,null)),end,stylestring.length,0)

       return stylestring
   }//make_text_styling 끝



}