package com.example.leedonghun.sellinguseditemapp.Util

import android.content.Context
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Singleton.AuthPoneNum
import com.example.leedonghun.sellinguseditemapp.Singleton.SnsEmailValue
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.nhn.android.naverlogin.OAuthLogin

/**
 * SellingUsedItemApp
 * Class: DeleteSnsData.
 * Created by leedonghun.
 * Created On 2020-08-08.
 * Description:
 *
 * sns 로그인의 경우  각  로그인 api별
 * oauth 인증을 위한  accesstoken들이  받아와 저장이ㅇ되는데
 * 로그아웃시  다  지워주기  위한  클래스이다.
 */
class DeleteSnsData(private val context: Context) {


    //구글 로그인에서 클라이언트 id보내고,
    //토큰이랑  사용자 이메일 정보 받아오는 형태로 build
    private val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail().build()

    //구글 로그인 client
    private  var googleSignInClient: GoogleSignInClient= GoogleSignIn.getClient(context,gso)



    //모든 sns signout 시키기
    //accesstoken , 싱글톤으로 지정해놓은  이메일 value까지
     fun Sns_login_signOut() {


        //페이스북 로그아웃
        LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut()


        //naver sign out
        OAuthLogin.getInstance().logoutAndDeleteToken(context)


        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener {

            // Firebase sign out
            FirebaseAuth.getInstance().signOut()
        }



        //일단 signletone 지워줌.
        //기존에  회원가입용 signleton은  회원가입 끝날때  다 리셋 시키자.
        SnsEmailValue.delete_sns_email()
        //회원가입용  핸드폰 번호도 지워줌
        AuthPoneNum.delete_phone_num()

    }

}