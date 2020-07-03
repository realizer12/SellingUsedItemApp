package com.example.leedonghun.sellinguseditemapp.Retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * SellingUsedItemApp
 * Class: ApiService.
 * Created by leedonghun.
 * Created On 2020-06-30.
 * Description:
 *
 * retrofit http 통신에서 사용할 http api들을 제공하는 인터페이스
 *
 */
interface ApiService {


    //retrofit 서버 통신 되는지  test용
    @POST("retrofitt_server_connection_check.php")
    fun server_connetion_test():Call<String>


    //핸드폰 번호를 이용한  본인 인증
    //폰번호를 넣어서 보내면, 서버측에서 인증키를  만들어서 sms를 보냄.
    //map(member_auth_phone_number)reason -> 0 =  회원가입 ,   1= 아이디 찾기 ,   2=  비밀번호 찾기
    @FormUrlEncoded
    @POST("map.php")
    fun send_phone_number_for_join_member(@Field("phone_number")phone_number: String,@Field("map_reason")reason:Int):Call<ResponseBody>



}