package com.example.leedonghun.sellinguseditemapp.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.example.leedonghun.sellinguseditemapp.Data.Login.AutoLoginCallback
import com.example.leedonghun.sellinguseditemapp.Data.Login.GetNaverLoginResponse
import com.example.leedonghun.sellinguseditemapp.Data.Login.LoginCallback
import com.example.leedonghun.sellinguseditemapp.Dialog.LoadingDialog
import com.example.leedonghun.sellinguseditemapp.PrivateInfo.ServerIp
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import com.example.leedonghun.sellinguseditemapp.Singleton.GlobalClass
import com.example.leedonghun.sellinguseditemapp.Singleton.SnsEmailValue
import com.example.leedonghun.sellinguseditemapp.Util.DeleteSnsData
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import kotlinx.android.synthetic.main.main_login_activity.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ResponseCache


/**
* SellingUsedItemApp
* Class: MainLoginActivity.
* Created by leedonghun.
* Created On 2020-06-08.
* Description:
*
* splash 엑티비티에서  로고 보여주고,  로그인이  안된 상태라면  현재 엑티비티로 넘어온다.
* sns 로그인 과 이메일 로그인 엑티비티로 넘어갈수 있으며,
* 회원가입 엑티비티로도 넘어갈수 있다.
*
**/
class MainLoginActivity : AppCompatActivity() {


    //구글 로그인 startactivity for result ->  아이디 고르는 창  띄우고  값
    //받아오기 위한 request code
    private val GOOGLE_LOGIN_REQUEST:Int=100

    //구글 로그인 client
    private lateinit var googleSignInClient: GoogleSignInClient

    //페이스북 로그인 응답 처리 callback manager
    private lateinit var callbackManager: CallbackManager

    //네이버 oauth 로그인
    private lateinit var nhnOAuthLoginModule: OAuthLogin

    //retrofit2 기본 설정된 class 객체
    lateinit var retrofitClient:RetrofitClient

    //sns  로그인 버튼 눌렀을때
    //딜레이가 조금 있어서  로딩 다이얼로그 띄어주기로 함
    lateinit var loadingDialog:LoadingDialog

    //회원가입 엑티비티로 감
    lateinit var intent_to_go_to_MakeNewEmailLoginId:Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_login_activity)
        Logger.v("실행됨")


        //쉐어드에 저장된  uid가져오기
        val sharedPreferences=this.getSharedPreferences(getString(R.string.shared_preference_name_for_store_uid), Context.MODE_PRIVATE)
        val uid=sharedPreferences.getString(getString(R.string.shared_key_for_auto_login),null)//없으면 null

        //shared에  저장된 uid가 있을떄 자동로그인 진행
        if(uid != null){
            
            Logger.v("uid null -> 자동로그인 그냥 넘어감")
            check_auto_login(uid)
        }


        //로그인 다이얼로그 용
        loadingDialog = LoadingDialog(this)


        //구글 로그인에서 클라이언트 id보내고,
        //토큰이랑  사용자 이메일 정보 받아오는 형태로 build
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()


        //위 build 내용 적용
        googleSignInClient= GoogleSignIn.getClient(this,gso)

        //페이스북용 응답처리 callback매니져
        callbackManager = CallbackManager.Factory.create()



        nhnOAuthLoginModule = OAuthLogin.getInstance()
        nhnOAuthLoginModule.init(this,
            getString(R.string.nhn_oauth_client_id) ,
            getString(R.string.nhn_oauth_client_secret) ,
            getString(R.string.nhn_oauth_client_name))



        //glide 라이브러리를 통해 중고마켓 gif 로고 파일을 넣어줌.
        Glide.with(this).load(R.drawable.main_login_activity__gif_logo).into(imgView_for_app_logo_gif)


        //이거는  이제  sns 로그인 다 구현 하기 전에 값 지워주기 용으로
        //sign_out 진행
        imgView_for_app_logo_gif.setOnClickListener {


            val sharedPreferences=this.getSharedPreferences(getString(R.string.shared_preference_name_for_store_uid),Context.MODE_PRIVATE)
            val uid=sharedPreferences.getString(getString(R.string.shared_key_for_auto_login),null)

            Logger.v("싱글톤으로 받은  sns login email-> ${SnsEmailValue.sns_login_email}")

            if(uid !=null) {
                Logger.v("shared에 담긴 uid-> $uid")
            }

            //sns 로그아웃
            DeleteSnsData(this).Sns_login_signOut()
            with(sharedPreferences.edit()){
                remove(getString(R.string.shared_key_for_auto_login))
                commit()
            }

        }




        //처음 이신 가요? 회원 가입 부분에서 회원가입을  bold 처리 해주기 위해서  아래와 같이
        //각 위치 별로 style을  다르게 적용 시켜서 텍스트에  settext시킴.
        //그런데 폰트 적용은 되지 않음...
        val stylestring:SpannableString= SpannableString("처음 이신 가요? 회원가입")
        stylestring.setSpan(StyleSpan(ResourcesCompat.getFont(this, R.font.cookierun_regular)!!.style),0,10,0)
        stylestring.setSpan(StyleSpan(ResourcesCompat.getFont(this, R.font.cookierun_bold)!!.style),10,stylestring.length,0)
        txt_to_go_make_id.text = stylestring


        //회원가입 텍스트 클릭 이벤트
        txt_to_go_make_id.setOnClickListener {

            Logger.v("이메일 로그인 회원가입 텍스트 클릭됨 -> MakeNewEmailLoginId로 가짐")

            //회원가입 엑티비티로 감
            intent_to_go_to_MakeNewEmailLoginId=Intent(this,MakeNewLoginIdActivity::class.java)

            //보내는 값이 1-> email 로그인 아이디를  생성 할때 , 0->  sns 로그인 아이디 생성 할때
            //값을 토대로 회원가입 용 뷰페이져의 4개 프래그먼트에서  3번째 프래그먼트(title -> 회원가입) 형태가 바뀌게됨.
            intent_to_go_to_MakeNewEmailLoginId.putExtra("check_sns_or_email",1)
            startActivity(intent_to_go_to_MakeNewEmailLoginId)

        }






        //로그인 이메일 버튼 이벤트
        btn_for_email_login.setOnClickListener {
            Logger.v("이메일 로그인 버튼 클릭됨")

            //이메일 로그인 엑티비티로 가기 위한 인텐트
            val intent_to_go_email_login_activity:Intent=Intent(this,EmailLoginActivity::class.java)
            startActivity(intent_to_go_email_login_activity)

        }





        //구글 버튼 클릭 이벤트
        btn_for_google_login.setOnClickListener {
            Logger.v("구글 로그인 버튼 클릭됨")

            //구글 로그인  처리하는데
            //시간이 걸릴때가 있어서 로딩 화면  띄어줌.
            loadingDialog.show_dialog()


            //구글 accesstoken 받기 진행
            google_sign_in()

        }




        //네이버 버튼 클릭 이벤트
        btn_for_naver_login.setOnClickListener {
            Logger.v("네이버 로그인 버튼 클릭됨")


            //로딩 다이얼로그 보여줌
            loadingDialog.show_dialog()

            //네이버 accesstoken 받아오기 진행
            naver_signin(nhnOAuthLoginModule)

        }





        //페이스북 버튼 클릭 이벤트
        btn_for_facebook_login.setOnClickListener {
            Logger.v("페이스북 로그인 버튼 클릭됨")


            facebook_login_api_btn.performClick()

            //페이스북 로그인  처리하는데
            //시간이 걸릴때가 있어서 로딩 화면  띄어줌.
            loadingDialog.show_dialog()

            //페이스북 accesstoken 받기 진행
            face_book_sign_in(facebook_login_api_btn)

        }


    }//onCreate() 끝


    //자동로그인에 필요한  uid가 저장되어 있을떄
    //자동로그인을  진행한다.
    fun check_auto_login(uid:String){

        retrofitClient = RetrofitClient(ServerIp.baseurl)
        retrofitClient.apiService.auto_login_check(uid, FirebaseInstanceId.getInstance().id)
            .enqueue(object : Callback<AutoLoginCallback> {

                override fun onResponse(call: Call<AutoLoginCallback>, response: Response<AutoLoginCallback>) {

                    //response 성공시
                    if(response.isSuccessful) {
                        val result = response.body()?.auto_login_response
                        val status = response.body()?.auto_login_callback_status

                        //result상태로 화면 분기해서 넘어감.
                        when (result) {

                            //자동로그인 가능
                            true -> {

                                Logger.v("자동로그인 성공  메인 화면으로 넘어간다.")
                                //메인 화면으로 넘어가기
                                val intent = Intent(this@MainLoginActivity, SellingUsedMainActivity::class.java)
                                startActivity(intent)
                                finish()//로그인 엑티비티는 종료


                            }

                            //자동로그인 실패일때
                            //상태 분기로 나눠서 원인 파악
                            false -> {

                                if(status==2){//맞는 개수가 1이 아님

                                    Logger.v("자동로그인 실패 -> status 값 ->  $status  auth_token check 일치 하지 않음")
                                    //sns 로그아웃
                                    DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()

                                }else if(status==3){//체크 쿼리 날리는데서 실패

                                    Logger.v("자동로그인 실패 -> status 값 ->  $status  auth_token check 쿼리 진행과정에서 에러")
                                    //sns 로그아웃
                                    DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()
                                }

                            }

                            //서버 지정된 에러 이외  알수 없는 에러  error body보아야됨
                            else -> {

                                Logger.v("자동로그인 실패 ->  ${response.errorBody()}")

                                //sns 로그아웃
                                DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()
                            }

                        }
                    }

                }
                override fun onFailure(call: Call<AutoLoginCallback>, t: Throwable) {
                    Logger.v("자동로그인 결과 실패-> ${t.message}")

                    //sns 로그아웃
                    DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()

                }

            })

    }//check_auto_login 끝



    //sns  로그인 여부 체크 해서
    //회원가입 창으로 넘어간다.
    //일반 회원 가입의 경우는 그냥  넘어가고,
    //sns 회원 가입의 경우는 서버에서 해당  로그인  이메일을 체크해서
    //넘어가기 여부를 체크 한다.
    fun move_make_id_with_sns_login_check(sns_login:Int,sns_login_email:String){

        Logger.v("sns 로그인 체크 메소드 실행됨 ->  sns 로그인 체크 값 -> $sns_login, 로그인 이메일 -> $sns_login_email")

        //회원가입 엑티비티로 감
        intent_to_go_to_MakeNewEmailLoginId=Intent(this,MakeNewLoginIdActivity::class.java)



        //여기서는 우선 해당 이메일이 회원기록에 있는지 체크부터 해준다.
        //회원 기록에 있으면, 바로 accesstoken 발행만 해서
        //메인으로 넘겨준다.
        //회원기록이 없으면  이제  새 회원으로 인식해서 회원가입 진행

        //서버로  이메일  보내서 확인
        retrofitClient= RetrofitClient(ServerIp.baseurl)
        retrofitClient.apiService.sns_user_login(sns_login,sns_login_email, GlobalClass.uniqueID)
            .enqueue(object:Callback<LoginCallback>{
                override fun onResponse(call: Call<LoginCallback>, response: Response<LoginCallback>) {

                    //이메일 중복 체크 결과
                    val result: LoginCallback? =response.body()

                    //callback받은 값  null 아닌 경우
                    if(result !=null) {
                        when {

                            result.resultcode == 1 -> {//이미  가입한 회원임을 확인함
//
                                Logger.v("해당 sns 로그인으로 가입한 유저 맞음 -> $result")

                                //유저 uid 받은거 sharepreference에 넣어주자
                                //room을 공부할겸 써볼가 했는데 여기에 쓰기에는 너무 과함.
                                //다른 곳에서 쓸곳이있을거야  채팅도 하니까...

                                //이제 로그인 할때마다  이 uid랑  uuid로 해쉬값 만들어서  보낸다음에
                                //맞으면  자동로그인 틀리면,  로그인창 다시 띄우는  처리 진행하자
                                val user_uid=result.userUid//서버의 등록된 해당 유저의 uid


                                //getpreference를  쓰기에는 로그인엑티비티에서도 로그인이 진행되므로..
                                //서버로부터 받은 uid  shared에 commit 함.
                                val store_user_uid:SharedPreferences=this@MainLoginActivity.getSharedPreferences(
                                    getString(R.string.shared_preference_name_for_store_uid),Context.MODE_PRIVATE)

                                with(store_user_uid.edit()){
                                    putString(getString(R.string.shared_key_for_auto_login),user_uid)
                                    commit()
                                }


                                //메인 엑티비티로  넘어감감
                                val intent_to_go_main_activity=Intent(this@MainLoginActivity,SellingUsedMainActivity::class.java)
                                startActivity(intent_to_go_main_activity)

                            }

                            //이메일은 가입이 되어잇지만, 회원가입 경로가 다름
                            //(ex 구글 로그인으로 진행했는데 네이버로 가입한  회원 기록이 있음 등..)
                            result.resultcode==2 -> {

                                Logger.v("누군가 사용중인 이메일 -요청 유저와  이메일은 같은데 가입경로가 다름-> 사용 불가")

                                Toast.makeText(this@MainLoginActivity,R.string.string_for_duplicate_email,Toast.LENGTH_SHORT).show()

                                //sns 로그아웃
                                DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()
                            }

                            result.resultcode==3 -> {//중복되는 이메일이 없어 중복 가능능

                               Logger.v("사용중인 이메일이 아니라서  회원가입으로 넘어감")

                                //sns 로그아웃
                                DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()

                                //sns 이메일 싱글톤 객체에 가져온 이메일 넣어줌.
                                SnsEmailValue.get_sns_email(sns_login_email)

                                //사용 가능한 이메일 이므로  인텐트로 넘긴다.
                                //보내는 값이 1-> email 로그인 아이디를  생성 할때 , 0->  sns 로그인 아이디 생성 할때
                                //값을 토대로 회원가입 용 뷰페이져의 4개 프래그먼트에서  3번째 프래그먼트(title -> 회원가입) 형태가 바뀌게됨.
                                intent_to_go_to_MakeNewEmailLoginId.putExtra("check_sns_or_email", 0)
                                startActivity(intent_to_go_to_MakeNewEmailLoginId)


                            }

                            //회원이어서 로그인 처리 위해  auth_token 발급 해서 넣어줬는데
                            // update 과정에서 실패함
                            //이렇게되도 로그인 못하도록 진행하자.
                            result.resultcode==-1->{


                                Logger.v("로그인 도중 auth_token db update부분에서 에러남")

                                //sns 로그아웃
                                DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()

                                Toast.makeText(this@MainLoginActivity,R.string.string_for_duplicate_email_check_server_error,Toast.LENGTH_SHORT).show()

                            }

                            else -> {

                                Toast.makeText(this@MainLoginActivity,R.string.string_for_duplicate_email_check_server_error,Toast.LENGTH_SHORT).show()
                                Logger.v("sns로그인 도중  에러 나옴 -> $result")

                            }

                        }

                    }


                    //서버로부터 callback왔으니까 다이얼로그는 꺼줌
                    loadingDialog.dismiss_dialog()

                }

                override fun onFailure(call: Call<LoginCallback>, t: Throwable) {

                    Logger.v("해당 sns email 체크  서버 통신 fail -> ${t.message}")

                    Toast.makeText(this@MainLoginActivity,R.string.string_for_duplicate_email_check_server_error,Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss_dialog()
                }

            })




    }



    //=================================================================================
    // 네이버 로그인 관련
    //=================================================================================

    //네이버 로그인  access token 방아오기
    private fun naver_signin(nhnOAuthLoginModule:OAuthLogin){

            nhnOAuthLoginModule.startOauthLoginActivity(this, @SuppressLint("HandlerLeak")

            object : OAuthLoginHandler() {

                override fun run(success: Boolean) {

                    //oauth 로그인  성공
                    if (success) {

                        val Naver_accessToken = nhnOAuthLoginModule.getAccessToken(this@MainLoginActivity)
                        val refreshToken = nhnOAuthLoginModule.getRefreshToken(this@MainLoginActivity)
                        val expiresAt = nhnOAuthLoginModule.getExpiresAt(this@MainLoginActivity)
                        val tokenType = nhnOAuthLoginModule.getTokenType(this@MainLoginActivity)


                        Logger.v("nhn Login Access Token : $Naver_accessToken")
                        Logger.v("nhn Login refresh Token : $refreshToken")
                        Logger.v("nhn Login expiresAt : $expiresAt")
                        Logger.v("nhn Login Token Type : $tokenType")
                        Logger.v("nhn Login Module State : " + nhnOAuthLoginModule.getState(this@MainLoginActivity).toString())


                        //엑세스 토큰이  존재 하면
                        //해당 토큰으로  로그인 정보 가져오기 진행
                        if(Naver_accessToken !=null) {

                            Logger.v("토큰 값-> $Naver_accessToken")

                            //네이버 로그인 진행
                            get_naver_login_info(" Bearer $Naver_accessToken")

                        }else{

                            //실패시  다이얼로그  없애줌.
                            loadingDialog.dismiss_dialog()


                            //토큰 다 없애줌.
                            //sns 로그아웃
                            DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()
                        }


                    } else {//oauth 로그인 실패


                        val errorCode = nhnOAuthLoginModule.getLastErrorCode(this@MainLoginActivity).code
                        val errorDesc = nhnOAuthLoginModule.getLastErrorDesc(this@MainLoginActivity)
                        Logger.v("nhn login errorCode -> $errorCode , errorDesc -> $errorDesc")

                        //실패시  다이얼로그  없애줌.
                        loadingDialog.dismiss_dialog()

                    }

                }

            })


    }




    //받아온  accesstoken 으로  로그인 유저 정보 가져오기.
    fun get_naver_login_info(accessToken:String){

        retrofitClient= RetrofitClient(ServerIp.naver_login_url)//네이버 로그인 정보 받는 api url
        retrofitClient.apiService.get_naver_login_user_email(accessToken)
            .enqueue(object :Callback<GetNaverLoginResponse>{

                override fun onResponse(call: Call<GetNaverLoginResponse>, response: Response<GetNaverLoginResponse>) {

                    if(response.isSuccessful) {

                        //네이버 로그인 유저정보 api 에서 받아온 값
                        val naver_user_info = response.body()?.resultcode
                        val naver_user_email=naver_user_info?.email


                       Logger.v("네이버 로그인 유저 이메일 -> $naver_user_email")

                        //네이버 유저  이메일 정보가 null 이 아닐때
                        if(naver_user_email != null) {

                            //처음 가입 하는 회원인지 판별
                            move_make_id_with_sns_login_check(
                                sns_login = 2,
                                sns_login_email = naver_user_email
                            )

                        }else{//null 일 경우

                            //실패시  다이얼로그  없애줌.
                            loadingDialog.dismiss_dialog()



                            //sns 로그아웃
                            DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()

                        }

                     }
                }

                override fun onFailure(call: Call<GetNaverLoginResponse>, t: Throwable) {

                    Logger.v("네이버 accesstoken 받기 에러 나옴-> ${t.message}")
                    loadingDialog.dismiss_dialog()
                }

            })

    }





    //=================================================================================
    // 구글 로그인 관련
    //=================================================================================

    //구글 access_token  받아오기
    private fun google_sign_in(){

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_REQUEST)

    }



    //구글 access_token  받아오기 및  페이스북 의 경우는
    //callbackmamnager 로 넘김.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        //페이스북 용 로그인 callback
        //로그인 요청시 여기로  결과 받아옴 -> 페이스북 callbackmanager 로 넘김
        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        //파이어베이스 구글로그인  진행을 위한  request code 100
        if (requestCode == GOOGLE_LOGIN_REQUEST) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{

                //파이어베이스  구글 로그인 성공
                val account = task.getResult(ApiException::class.java)!!
                Logger.v("firebaseAuthWithGoogle:" + account.id)

                //구글  siginin 으로  받아온  토큰  파이어베이스 구글  인증으로  넘겨줌.
                firebaseAuthWithGoogle(account.idToken!!)

            }catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Logger.v("Google sign in failed-> $e")

                //실패시  다이얼로그  없애줌.
                loadingDialog.dismiss_dialog()

                //sns 로그아웃
                DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()

            }
        }//GOOGLE_LOGIN_REQUEST 끝



    }



    //파이어베이스 구글 accesstoken  등록
    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    val user = FirebaseAuth.getInstance().currentUser
                    val google_user_email=user?.email
                    Logger.v("user  값 -> $google_user_email")


                    //구글 유저  이메일 정보가 null 이 아닐때
                    if(google_user_email != null) {

                        //처음 가입 하는 회원인지 판별
                        move_make_id_with_sns_login_check(
                            sns_login = 1,
                            sns_login_email = google_user_email
                        )

                    }else{//null 일 경우

                        //실패시  다이얼로그  없애줌.
                        loadingDialog.dismiss_dialog()


                        //sns 로그아웃
                        DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()


                    }



                } else {

                    // If sign in fails, display a message to the user.
                    Logger.v( "signInWithCredential:failure  -> ${task.exception}")

                    //뭐가 되었든 result를 받았으니까 로딩  없애줌.
                    loadingDialog.dismiss_dialog()
                    //sns 로그아웃
                    DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()
                }


            }
    }



    //=================================================================================
    // 페이스북  로그인 관련
    //=================================================================================


    //페이스북 로그인 진행 -accesstoken 받기
    private fun face_book_sign_in(facebook_login_api_btn:LoginButton) {

        facebook_login_api_btn.setPermissions("email")//유저의 이메일 정보만 요청

        //페이스북 아이디 등록  callback
        facebook_login_api_btn.registerCallback(callbackManager,object :FacebookCallback<LoginResult>{

            override fun onSuccess(result: LoginResult?) {
                Logger.v("페이스북 로그인 성공 내용-> $result")

                if(result !=null) {

                    //페이스북에서  받은 acesstoken 파이어베이스로 넘김
                    handleFacebookAccessToken(result.accessToken)
                }else{

                    //뭐가 되었든 result를 받았으니까 로딩  없애줌.
                    loadingDialog.dismiss_dialog()

                    //sns 로그아웃
                    DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()
                    Toast.makeText(this@MainLoginActivity,R.string.face_login_fail,Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancel() {
                Logger.v("페이스북 로그인 취소")

                loadingDialog.dismiss_dialog()
                //sns 로그아웃
                DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()
                Toast.makeText(this@MainLoginActivity,R.string.face_login_fail,Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                Logger.v("페이스북 로그인 실패   내용-> $error")
                loadingDialog.dismiss_dialog()

                //sns 로그아웃
                DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()
                Toast.makeText(this@MainLoginActivity,R.string.face_login_fail,Toast.LENGTH_SHORT).show()

            }

        })
    }


    //파이어베이스에  페이스북 엑세스 토큰 등록
    private fun handleFacebookAccessToken(token: AccessToken) {
        Logger.v("페이스북 access toke -> :$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                //파이어베이스에  페이스북  accesstoken  등록 성공
                if (task.isSuccessful) {

                    Logger.v("파이어베이스에  페이스북 accesstoken 등록 성공")

                    //유저의  정보 중  이메일 정보를 받아서 null체크를 해준다.
                    //만약에  null 값이라면,
                    val user = FirebaseAuth.getInstance().currentUser
                    val facebook_user_email=user?.email

                    if(facebook_user_email==null){

                        //뭐가 되었든 result를 받았으니까 로딩  없애줌.
                        loadingDialog.dismiss_dialog()


                        //sns 로그아웃
                        DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()

                        //페이스북 같은 경우에는  핸드폰  가입도 있어서  이경우에는
                        //토스트로 알리고
                        Toast.makeText(this,R.string.face_book_login_email_null_check,Toast.LENGTH_SHORT).show()

                    }else{

                        Logger.v("페이스북  user 이메일 값 -> ${user.email}")

                        //처음 가입 하는 회원인지 판별
                        move_make_id_with_sns_login_check(
                            sns_login = 3,
                            sns_login_email = facebook_user_email
                        )

                    }





                } else {//파이어베이스에 accesstoken 등록 실패

                    Logger.v("파이어베이스에  페이스북 accesstoken 등록 실패 -> ${task.exception}")

                    Toast.makeText(this, R.string.face_book_login_fail_to_register_firbase, Toast.LENGTH_SHORT).show()

                    //뭐가 되었든 result를 받았으니까 로딩  없애줌.
                    loadingDialog.dismiss_dialog()

                    //sns 로그아웃
                    DeleteSnsData(this@MainLoginActivity).Sns_login_signOut()

                }



            }
    }



}//Main 끝
