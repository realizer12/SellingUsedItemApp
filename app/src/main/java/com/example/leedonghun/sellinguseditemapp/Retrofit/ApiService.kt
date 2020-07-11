package com.example.leedonghun.sellinguseditemapp.Retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

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
    //callback 은 response body로
    @FormUrlEncoded
    @POST("map.php")
    fun send_phone_number_for_auth_own(@Field("phone_number")phone_number: String,
                                       @Field("map_reason")reason:Int):Call<ResponseBody>


    //핸드폰 인증 시 받은 인증번호를
    //서버로 보내주는 역할을 한다.
    //callback 은 response body로
    @FormUrlEncoded
    @POST("check_map_key.php")
    fun send_sms_auth_key(@Field("phone_number")phone_number: String,
                          @Field("map_reason")reason: Int,
                          @Field("map_auth_key")auth_key:String):Call<ResponseBody>


    //이메일 중복 체크를 위해
    //사용자가 입력한 이메일을  서버로 보내 체크한다.
    //callback은  response body로
    @FormUrlEncoded
    @POST("duplicate_check_login_email.php")
    fun check_duplicate_login_email(@Field("login_email")login_email:String):Call<ResponseBody>


    //닉네임 중복 체크를 위해
    //사용자가 입력한 닉네임을 서버로 보내 체크 한다.
    //callback은 response body로
    //닉네임은  굳이  중요한  개인 정보가 아니라서 GET으로 보냄
    @GET("duplicate_check_nickname.php")
    fun check_duplicate_user_nickname(@Query("nickname")nickname:String):Call<ResponseBody>


}