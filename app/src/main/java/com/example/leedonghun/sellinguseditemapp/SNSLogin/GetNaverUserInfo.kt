package com.example.leedonghun.sellinguseditemapp.SNSLogin

import com.google.gson.annotations.SerializedName


/**
 * SellingUsedItemApp
 * Class: GetNaverUserInfo.
 * Created by leedonghun.
 * Created On 2020-08-06.
 * Description:
 *
 * 네이버 로그인후  유저  정보를
 * 받기 위한  dto 클래스
 */


//네이버에서 받아오는
//내용중에  response 안에  유저의 이메일
//들어있어 우선  reaponse를 받음
data class GetNaverLoginResponse (

    @SerializedName("response")
    var resultcode:GetNaverUserEmail

)

//response 안에  유저의
//네이버 이메일 받음
data class GetNaverUserEmail(

   @SerializedName("email")
   var email:String


)