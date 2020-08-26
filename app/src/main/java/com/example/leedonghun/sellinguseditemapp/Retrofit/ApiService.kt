package com.example.leedonghun.sellinguseditemapp.Retrofit

import com.example.leedonghun.sellinguseditemapp.Data.Login.AutoLoginCallback
import com.example.leedonghun.sellinguseditemapp.Data.Login.GetNaverLoginResponse
import com.example.leedonghun.sellinguseditemapp.Data.Login.LoginCallback
import com.example.leedonghun.sellinguseditemapp.Data.Register.UploadNewMemberCallback
import com.facebook.login.Login

import okhttp3.ResponseBody
import org.json.JSONObject
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


    //자동로그인 가능 여부 판단
    //서버로 auth_token  확인 할  정보를 보내서
    //서버에서 해쉬처리하여 저장된  auth_token과 맞는지 여부를 판단
    //맞으면,  로그인을 진행,  틀리면,
    @FormUrlEncoded
    @POST("account/login/check_auto_login.php")
    fun auto_login_check(@Field("user_uid")user_uid:String,
                         @Field("uuid")uuid:String):Call<AutoLoginCallback>





    //일반  이메일 사용 유저의 로그인을 진행
    //다음의 경우의 수가 있음.
    //1. 가입한 이력이 없는 경우 -> return result code =1  uid = null
    //2. 가입한 이력이 있는데, sns_check가 0이상 일때  (0은 일반 로그인 )  return result code =2 uid null
    //3. 성공적으로  로그인 -> return result code =3 uid = 해당 user의  uid
    //4. 성공적인 로그인인데 auth_token 업데이트 가 안됨. -> result code 4 uid null
    //5. 이메일은 맞는데  패스워드가 틀림.
    @FormUrlEncoded
    @POST("account/login/email_user_login.php")
    fun email_user_login(@Field("user_email")user_email:String,
                         @Field("user_password")user_password:String,
                         @Field("uuid")uuid:String):Call<LoginCallback>


    //sns 로그인처리를 진행
    //다음 경우의 수가 있을수 있다.
    // 1. 해당 sns  이메일이  중복 되고  sns 플랫폼 값이  일치한다면,   로그인 처리->  return 값은  해당 uid (auth_token 만들기 용)
    // 2. 해당 sns  이메일이  중복 되고  sns 플랫폼  값 일치 x-> 해당 메일은  다른 방식으로  사용 중이라고 return
    // 3. 해당 sns  이메일이  중복 없을때,  새롭게  회원가입  실행하고 return 값 uid 받고 로그인 처리
    //parameter 미터 설명
    //sns_login_status-> 어떤 sns 플랫폼을 이용한 로그인인지 -> 구글 =1, 네이버 =2, 페이스북=3
    //sns_email-> 이메일
    //uuid->  사용자(기기) 식별을 위한 uuid
    @FormUrlEncoded
    @POST("account/login/sns_user_login.php")
    fun sns_user_login(@Field("sns_login_status")sns_login_status:Int,
                       @Field("sns_email")sns_email:String,
                       @Field("uuid")uuid:String):Call<LoginCallback>


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
    fun upload_new_member_info(@Field("new_ember_info")new_member_info:JSONObject):Call<UploadNewMemberCallback>



    //네이버 로그인의 경우은  access token 을 이용해서
    //아래  주소로  사용자 정보를  받아와야한다.
    //토큰은 hearder 에  넣어주면 된다.
    //그외 파라미터는 없음
    @GET("v1/nid/me")
    fun get_naver_login_user_email(@Header("Authorization")token:String):Call<GetNaverLoginResponse>



}