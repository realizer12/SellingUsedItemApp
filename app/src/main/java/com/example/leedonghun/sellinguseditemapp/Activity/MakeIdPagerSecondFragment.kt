package com.example.leedonghun.sellinguseditemapp.Activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.Interface.CheckMakeIdPagerCompleteStatus
import com.example.leedonghun.sellinguseditemapp.R
import kotlinx.android.synthetic.main.term_pager_second_fragment.*
import kotlinx.android.synthetic.main.term_pager_second_fragment.view.*
import kotlinx.android.synthetic.main.term_pager_second_fragment.view.editxt_for_add_phone_number
import java.lang.RuntimeException

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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        //프래그먼트 뷰 연결
        val view: View = inflater.inflate(R.layout.term_pager_second_fragment, container, false)
        Log.v("check_app_runnig_status",fragment_name_for_Log+"의 onCreateView 실행 됨")



        //인증 번호 받기 버튼
        view.btn_for_get_certification_code.setOnClickListener {
            Log.v("check_app_runnig_status",fragment_name_for_Log+"의 휴대폰 인증번호 받기 버튼 눌림")

            //이제 여기서  해당  sms 인증 문자 제대로 가졌을때, 확인해서
            //조건문을 구별 ㄱㄱ
            view.btn_for_get_certification_code.text="다시 받기"
            view.txt_for_show_remain_count_of_input_code.visibility=View.VISIBLE
            view.linearlayout_for_input_certification_sms_code.visibility=View.VISIBLE


            //인증 번호에sms 보냈으니까  이번에는
            //인증 번호 쓰는 editext 포커스 넣어줌.
            view.editxt_for_add_certification_code.requestFocus()

        }



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