package com.example.leedonghun.sellinguseditemapp.Singleton

/**
 * SellingUsedItemApp
 * Class: auth_phon_num.
 * Created by leedonghun.
 * Created On 2020-07-07.
 *
 * Description: 회원가입 부분에서  핸드폰 번호 인증을 하고
 * 인증 성공시  다음 페이져로 넘어가는데 이때
 * 인증된  핸드폰를  같이 넘겨줘야된다.
 *
 * 서버에서  sms 인증 db 후,  아직 회원으로 결정이 안된
 * 핸드폰 번호를 회원 db에 넣기는 애메하므로,  이렇게 싱글톤으로 받아서
 * 다음 페이져 프래그먼트로 보내기로 결정함.
 */

object AuthPoneNum {

    var auth_phonnumber:String=""//인증한  폰 번호

    //인증한 폰 번호 받아서 넣어줌
    fun get_phone_number(auth_phonnumber:String){

        this.auth_phonnumber=auth_phonnumber

    }


    //singltone에 있던 값 없애줌.
    //필요한 부분이 끝나면,  더이상
    //해당 값을 가지고 있으면  유출 될수 있으므로,
    //없애준다,
    fun delete_phone_num(){

        this.auth_phonnumber=""

    }

}