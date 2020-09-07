package com.example.leedonghun.sellinguseditemapp.Data.GetUserInFo

import com.google.gson.annotations.SerializedName

/**
 * SellingUsedItemApp
 * Class: GetBasicUserInFo.
 * Created by leedonghun.
 * Created On 2020-09-06.
 * Description:
 *
 * mysapce프래그먼트에서  유저의
 * 프로필 이미지와  닉네임 그리고 보유 코인량을
 * 서버로 부터 json으로 받아 직력화 한다.
 */
data class GetBasicUserInFo(

    //유저의 닉네임
    @SerializedName("nickname")
    val nickname: String? =null,

    //유저의  프로필 이미지 url이다.
    @SerializedName("img_url")
    val image_url:String?=null,

    //유저의  코인량이다. -> default가 0으로 지정되어
    //null이어도 0으로  들어옴.
    @SerializedName("coin")
    val coin_amount:Int
)