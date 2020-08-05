package com.example.leedonghun.sellinguseditemapp.Activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.example.leedonghun.sellinguseditemapp.Dialog.LoadingDialog
import com.example.leedonghun.sellinguseditemapp.R
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.main_login_activity.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


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


    //구글 로그인 client
    private lateinit var googleSignInClient: GoogleSignInClient

    //구글 로그인 startactivity for result ->  아이디 고르는 창  띄우고  값
    //받아오기 위한 request code
    private val GOOGLE_LOGIN_REQUEST:Int=100


    //페이스북 로그인 응답 처리 callback manager
    lateinit var callbackManager: CallbackManager


    lateinit var loadingDialog:LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_login_activity)

        Log.v("check_app_runnig_status","MainLoginActivity onCreate() 실행됨")


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




        //glide 라이브러리를 통해 중고마켓 gif 로고 파일을 넣어줌.
        Glide.with(this).load(R.drawable.main_login_activity__gif_logo).into(imgView_for_app_logo_gif)


        //처음 이신 가요? 회원 가입 부분에서 회원가입을  bold 처리 해주기 위해서  아래와 같이
        //각 위치 별로 style을  다르게 적용 시켜서 텍스트에  settext시킴.
        //그런데 폰트 적용은 되지 않음...
        var stylestring:SpannableString= SpannableString("처음 이신 가요? 회원가입")
        stylestring.setSpan(StyleSpan(ResourcesCompat.getFont(this, R.font.cookierun_regular)!!.style),0,10,0)
        stylestring.setSpan(StyleSpan(ResourcesCompat.getFont(this, R.font.cookierun_bold)!!.style),10,stylestring.length,0)
        txt_to_go_make_id.text = stylestring


        //회원가입 텍스트 클릭 이벤트
        txt_to_go_make_id.setOnClickListener {
            Log.v("check_app_runnig_status","이메일 로그인 회원가입 텍스트 클릭됨 -> MakeNewEmailLoginId로 가짐")

          //회원가입 엑티비티로 감
          val intent_to_go_to_MakeNewEmailLoginId=Intent(this,MakeNewLoginIdActivity::class.java)

          //보내는 값이 1-> email 로그인 아이디를  생성 할때 , 0->  sns 로그인 아이디 생성 할때
          //값을 토대로 회원가입 용 뷰페이져의 4개 프래그먼트에서  3번째 프래그먼트(title -> 회원가입) 형태가 바뀌게됨.
          intent_to_go_to_MakeNewEmailLoginId.putExtra("check_sns_or_email",1)
          startActivity(intent_to_go_to_MakeNewEmailLoginId)

        }

        //로그인 이메일 버튼 이벤트
        btn_for_email_login.setOnClickListener {
            Log.v("check_app_runnig_status","이메일 로그인 버튼 클릭됨")

            //이메일 로그인 엑티비티로 가기 위한 인텐트
            val intent_to_go_email_login_activity:Intent=Intent(this,EmailLoginActivity::class.java)
            startActivity(intent_to_go_email_login_activity)

        }

        //구글 버튼 클릭 이벤트
        btn_for_google_login.setOnClickListener {

            Log.v("check_app_runnig_status","구글 로그인 버튼 클릭됨")

            //구글 로그인  처리하는데
            //시간이 걸릴때가 있어서 로딩 화면  띄어줌.
            loadingDialog.show_dialog()


            //구글 로그인 진행
            google_sign_in()

        }

        //네이버 버튼 클릭 이벤트
        btn_for_naver_login.setOnClickListener {
            Log.v("check_app_runnig_status","네이버 로그인 버튼 클릭됨")

            signOut()
        }



        //페이스북 버튼 클릭 이벤트
        btn_for_facebook_login.setOnClickListener {
            Log.v("check_app_runnig_status","페이스북 로그인 버튼 클릭됨")

            //페이스북  api용 로그인 버튼으로 onclick 진행
            facebook_login_api_btn.performClick()

            //페이스북 로그인  처리하는데
            //시간이 걸릴때가 있어서 로딩 화면  띄어줌.
            loadingDialog.show_dialog()

        }

        face_book_sign_in(facebook_login_api_btn)



    }//onCreate() 끝



    override fun onPause() {
        super.onPause()

        Log.v("check_app_runnig_status", "유저 이메일 -> ${FirebaseAuth.getInstance().currentUser?.email}")
    }



    //페이스북 로그인 진행
    private fun face_book_sign_in(face_login_api_btn:LoginButton) {






        facebook_login_api_btn.setPermissions("email")//유저의 이메일 정보만 요청

        //페이스북 아이디 등록  callback
        facebook_login_api_btn.registerCallback(callbackManager,object :FacebookCallback<LoginResult>{

            override fun onSuccess(result: LoginResult?) {
                Log.v("check_app_runnig_status", "페이스북 로그인 성공 내용-> $result")

                if(result !=null) {

                    //페이스북에서  받은 acesstoken 파이어베이스로 넘김
                    handleFacebookAccessToken(result.accessToken)
                }

            }

            override fun onCancel() {
                Log.v("check_app_runnig_status", "페이스북 로그인 취소")

            }

            override fun onError(error: FacebookException?) {
                Log.v("check_app_runnig_status", "페이스북 로그인 실패   내용-> $error")
            }


        })




    }



    //구글 로그인 진행
    private fun google_sign_in(){

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_REQUEST)

    }


    //여기서는 signout 진행되지는 않지만 일단 추가 시켜놓음
    private fun signOut() {


        LoginManager.getInstance().logOut()
        // Firebase sign out
        FirebaseAuth.getInstance().signOut()


        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {

            // Firebase sign out
            FirebaseAuth.getInstance().signOut()

        }


    }


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
                Log.d("check_app_runnig_status", "firebaseAuthWithGoogle:" + account.id)

                //구글  siginin 으로  받아온  토큰  파이어베이스 구글  인증으로  넘겨줌.
                firebaseAuthWithGoogle(account.idToken!!)

            }catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("check_app_runnig_status", "Google sign in failed", e)


            }
        }//GOOGLE_LOGIN_REQUEST 끝


        //뭐가 되었든 result를 받았으니까 로딩  없애줌.
        loadingDialog.dismiss_dialog()
    }



    //파이어베이스 구글  인증
    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.v("check_app_runnig_status","user  값 -> ${user?.email}")

                } else {

                    // If sign in fails, display a message to the user.
                    Log.w("check_app_runnig_status", "signInWithCredential:failure", task.exception)

                }
            }
    }


    //파이어베이스 엑세스 토큰 등록
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("check_app_runnig_status", "페이스북 access toke -> :$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d("check_app_runnig_status", "signInWithCredential:success")

                    val user = FirebaseAuth.getInstance().currentUser

                    if(user?.email==null){
                        FirebaseAuth.getInstance().signOut()
                        LoginManager.getInstance().logOut()

                        //페이스북 같은 경우에는  핸드폰  가입도 있어서  이경우에는
                        //토스트로 알리고
                        Toast.makeText(this,R.string.face_book_login_email_null_check,Toast.LENGTH_SHORT).show()
                    }

                    Log.v("check_app_runnig_status","페이스북  user 이메일 값 -> ${user?.email}")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("check_app_runnig_status", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }



}//Main 끝
