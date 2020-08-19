package com.example.leedonghun.sellinguseditemapp.Data.Login

import com.google.gson.annotations.SerializedName

/**
 * SellingUsedItemApp
 * Class: LoginCallback.
 * Created by leedonghun.
 * Created On 2020-08-15.
 * Description:
 *
 * 로그인을 시도했을때  성공 여부 및
 * 나중에  로그인시  자동로그인시
 * 유저 정보를 체크 하기 위한 uid 를  return 해준다.
 */

data class LoginCallback(

    @SerializedName("response")
    var resultcode: Int,

    @SerializedName("uid")
    var userUid:String
)
