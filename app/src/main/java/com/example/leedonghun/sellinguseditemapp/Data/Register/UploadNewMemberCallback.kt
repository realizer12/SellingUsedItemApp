package com.example.leedonghun.sellinguseditemapp.Data.Register

import com.google.gson.annotations.SerializedName

/**
 * SellingUsedItemApp
 * Class: UploadNewMemberCallback.
 * Created by leedonghun.
 * Created On 2020-08-25.
 * Description:
 *
 * 새로운 회원가입을 진행했을때
 * 서버로부터의  callback값과  일부 필요 value( sns로그인시 uid리턴-> 바로  로그인 하기 위해서 )
 * 를 받는다.
 *
 */
data class UploadNewMemberCallback(

    //업로드 성공 실패 여부를  받음.
    @SerializedName("success")
    var success:Boolean,


    //성공시에도  sns인지  일반 로그인인지
    //실패시에도  어떤  fail인지 여부를 나타내주는 상태값이다.
    @SerializedName("status")
    var status:Int,


    //sns 회원가입의 경우는 회원가입 성공시
    //바로  로그인을 하도록 만들기 위해서,
    @SerializedName("uid")
    var uid:String,

    @SerializedName("email")
    var email:String

)