package com.example.leedonghun.sellinguseditemapp.Retrofit

import com.example.leedonghun.sellinguseditemapp.SNSLogin.GetNaverLoginResponse

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
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
    @POST("account/util/map.php")
    fun send_phone_number_for_auth_own(@Field("phone_number")phone_number: String,
                                       @Field("map_reason")reason:Int):Call<ResponseBody>


    //핸드폰 인증 시 받은 인증번호를
    //서버로 보내주는 역할을 한다.
    //callback 은 response body로
    @FormUrlEncoded
    @POST("account/check_user_info/check_map_key.php")
    fun send_sms_auth_key(@Field("phone_number")phone_number: String,
                          @Field("map_reason")reason: Int,
                          @Field("map_auth_key")auth_key:String):Call<ResponseBody>


    //이메일 중복 체크를 위해
    //사용자가 입력한 이메일을  서버로 보내 체크한다.
    //callback은  response body로
    @FormUrlEncoded
    @POST("account/check_user_info/duplicate_check_login_email.php")
    fun check_duplicate_login_email(@Field("login_email")login_email:String):Call<ResponseBody>

    //닉네임 중복 체크를 위해
    //사용자가 입력한 닉네임을 서버로 보내 체크 한다.
    //callback은 response body로
    //닉네임은  굳이  중요한  개인 정보가 아니라서 GET으로 보냄
    @GET("account/check_user_info/duplicate_check_nickname.php")
    fun check_duplicate_user_nickname(@Query("nickname")nickname:String):Call<ResponseBody>


    //회원등록할 new 회원 정보들을  json형태로
    //서버에 보낸다.
    //서버에 회원 등록  성공 여부는 response body로 진행한다.
    //callback은 response body로 진행
    @FormUrlEncoded
    @POST("account/register/upload_new_member_info.php")
    fun upload_new_member_info(@Field("new_ember_info")new_member_info:JSONObject):Call<ResponseBody>



    //네이버 로그인의 경우은  access token 을 이용해서
    //아래  주소로  사용자 정보를  받아와야한다.
    //토큰은 hearder 에  넣어주면 된다.
    //그외 파라미터는 없음
    @GET("v1/nid/me")
    fun get_naver_login_user_email(@Header("Authorization")token:String):Call<GetNaverLoginResponse>



}