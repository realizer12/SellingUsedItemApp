package com.example.leedonghun.sellinguseditemapp.Data.Login

import com.google.gson.annotations.SerializedName

/**
 * SellingUsedItemApp
 * Class: AutoLoginCallback.
 * Created by leedonghun.
 * Created On 2020-08-20.
 * Description:
 *
 * 자동 로그인 관련 callback  을 받는 부분이다.
 * response에 따라  분기 처리하여,
 * 메인화면 또는   로그인 화면을  보여준다.
 *
 */


data class AutoLoginCallback(

    //자동 로그인이 성공일 때,   true 반환  실패일때는  false 반환
    @SerializedName("response")
    var auto_login_response:Boolean,

    //각  응답에  상태를 보여주는데
    //2일 경우  해당  정보에 맞는  row가 없을을 알려주는 거고
    //3일 경우 쿼리문에 실패를 알려준다.
    //그리고 response가 true시일때는  status 가 필요없어서  null 값을 보내줌.
    @SerializedName("status")
    var auto_login_callback_status:Int

)