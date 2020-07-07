package com.example.leedonghun.sellinguseditemapp.Interface

/**
 * SellingUsedItemApp
 * Class: SendPhonNumberToAnotherFragment.
 * Created by leedonghun.
 * Created On 2020-07-07.
 * Description:
 */
interface SendPhonNumberToAnotherFragment {


    //회원 가입시  두번째 페이지 프래그먼트에서
    //본인 확인 받은  폰 번호를 보내주기 위한 인터페이스이다.
    fun send_authed_phone_num(phone_num:String)
}