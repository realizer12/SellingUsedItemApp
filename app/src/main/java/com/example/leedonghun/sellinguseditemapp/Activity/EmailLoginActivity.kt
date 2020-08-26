package com.example.leedonghun.sellinguseditemapp.Activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.leedonghun.sellinguseditemapp.Data.Login.LoginCallback
import com.example.leedonghun.sellinguseditemapp.PrivateInfo.ServerIp
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import com.example.leedonghun.sellinguseditemapp.Singleton.GlobalClass
import com.example.leedonghun.sellinguseditemapp.Util.KeyboardVisibilityUtils
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import kotlinx.android.synthetic.main.email_login_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * SellingUsedItemApp
 * Class: EmailLoginActivity.
 * Created by leedonghun.
 * Created On 2020-06-10.
 * Description: 이메일 로그인을 하는 엑티비티이다.
 * 기존에 가입한 로그인 이메일을 통해서 로그인을 하거나, 아이디나  비밀 번호를 찾는 엑티비티로
 * 넘어가는 버튼이 있다.
 * 그리고 비밀 번호 의  경우  눈 아이콘을  눌러서  내가 쓴 비밀 번호를 볼수 있는 처리가 되어있다.
 */
class EmailLoginActivity :AppCompatActivity() {

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils//키보드 visible 판단해주는  클래스

    private lateinit var retrofitClient: RetrofitClient
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_login_activity)
        Logger.v("실행됨")


        //일반 회원가입후  로그인 이메일 왔을때
        //로그인 입력 edittext에 가입한 이메일을 넣저준다.
        val intent= intent
        val email=intent.getStringExtra("maked_email")
        if(email != null) {
            editxt_for_add_login_email.setText(email)
        }


        //뒤로 가기 버튼 클릭
        arrow_btn_for_back_to_login_activity.setOnClickListener {
            Logger.v("뒤로가기 버튼 클릭됨-> MainLoginActivity로 이동 됨")

            //현재 엑티비티 종료 시킴
            finish()
        }



         //현재 레이아웃에서 로그인 버튼의  위치를  받아옴.
         val parameter = btn_for_login.layoutParams as ConstraintLayout.LayoutParams

         //현재 xml에  지정된 로그인 버튼의  margin -bottm 값을  변수에 넣어둠.
         //이유는 키보드 올라올때 margin-bottom값을  키보드 높이- 300 지정하므로,
         //다시 키보드를 내릴때  로그인 버튼이 원래 자리를  찾아가지 못함.
         //그러므로,  원래 margin-bottom값을 넣어놓음
         val bottm_margin:Int=parameter.bottomMargin



        //키보드 shown /hide 여부 감지
        keyboardVisibilityUtils= KeyboardVisibilityUtils(window,

            // 키보드가 올라올 때의 동작
            onShowKeyboard = { keyboardHeight, visibleDisplayFrameHeight ->
                Logger.v("키보드 올라옴")

                //로그인 버튼 위치 조정-> bottom margin 만  키보드 높이 -300으로 바꿔준다.
                parameter.setMargins(parameter.leftMargin , parameter.topMargin, parameter.rightMargin, keyboardHeight-300) // left, top, right, bottom
                btn_for_login.layoutParams = parameter

            },

            // 키보드가 내려갈 때의 동작
            onHideKeyboard = {
                Logger.v("키보드 내려감")


                //로그인 버튼 위치  다시  원상 복귀
                parameter.setMargins(parameter.leftMargin , parameter.topMargin, parameter.rightMargin, bottm_margin) // left, top, right, bottom
                btn_for_login.layoutParams = parameter

            }
        )//키보드 감지 끝.


        //배경을 터치시 올라와있던 키보드를 숨겨줌
        email_login_activity_entire_layout.setOnClickListener{

            keyboardVisibilityUtils.hideKeyboard(this,it)

        }



        //비밀번호 보여짐 상태 아이콘  클릭 이벤트
        frame_for_handle_pwd_shown.setOnClickListener {

            Logger.v("비밀번호 상태  보여짐으로  설정 됨")

            //보이기 여부 설정하는 함수 1-1
            show_pwd_shown_status(img_for_pwd_shown,img_for_pwd_not_shown)
        }


        //로그인 버튼 눌렀을때
        btn_for_login.setOnClickListener {
            Logger.v("로그인 버튼 눌림")

            //이메일로그인 실행
            email_login(email = editxt_for_add_login_email.text.toString() ,
                        password = editxt_for_add_login_password.text.toString() )

        }

        //아이디 찾기 버튼 눌렸을때
        btn_for_find_id.setOnClickListener {
            Logger.v("아이디 찾기 버튼 눌림")

        }

        //비밀번호 찾기 버튼 눌림.
        btn_for_find_pwd.setOnClickListener {
            Logger.v("비밀번호 찾기 버튼 눌림")

        }


    }//onCreate()끝


    //이메일 로그인을 진행한다.
    //회원 확인후, 로그인이 되면, 메인으로 넘어간다.
    //실패시  토스트와 함께 알려준다. 
    fun email_login(email:String, password:String){
        
        Logger.v("email_login 실행됨")
        retrofitClient= RetrofitClient(ServerIp.baseurl)
        retrofitClient.apiService.email_user_login(email,password,GlobalClass.uniqueID)
            .enqueue(object: Callback<LoginCallback>{

                override fun onResponse(call: Call<LoginCallback>, response: Response<LoginCallback>) {
                
                    val response_value=response.body()
                    val result_code=response_value?.resultcode
                    val uid= response_value?.userUid

                    Logger.v("값이 옴 -> $response_value   ${response.message()}")

                    when(result_code){

                        1-> { Logger.v("가입한 내역이 없는 이메일")
                            Toast.makeText(this@EmailLoginActivity,R.string.string_for_no_history_for_join_email,Toast.LENGTH_SHORT).show()
                        }

                        2->{ Logger.v("이메일 로그인용 이메일이 아닌  sns 로그인 용임")
                            Toast.makeText(this@EmailLoginActivity,R.string.string_for_different_email_status,Toast.LENGTH_SHORT).show()
                        }


                        3->{ Logger.v("성공적으로 로그인 가능함")
                            // TODO: 2020-08-26 여기서  uid저장 하고 메인으로 넘어가기  그리고 flag 정리 해줘야됨
                            Toast.makeText(this@EmailLoginActivity,R.string.string_for_alert_login_success,Toast.LENGTH_SHORT).show()

                            //getpreference를  쓰기에는 로그인엑티비티에서도 로그인이 진행되므로..
                            //서버로부터 받은 uid  shared에 commit 함.
                            val store_user_uid: SharedPreferences =this@EmailLoginActivity.getSharedPreferences(
                                getString(R.string.shared_preference_name_for_store_uid), Context.MODE_PRIVATE)

                            with(store_user_uid.edit()){
                                putString(getString(R.string.shared_key_for_auto_login),uid)
                                commit()
                            }


                            //메인 엑티비티로  넘어감감
                            val intent_to_go_main_activity= Intent(this@EmailLoginActivity,SellingUsedMainActivity::class.java)
                            intent_to_go_main_activity.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK//이제  task 모두  clear시켜줌.
                            startActivity(intent_to_go_main_activity)

                            finish()
                        }

                        4->{ Logger.v("로그인 가능한데 auth_token 업데이트 에서 실패함.")
                            Toast.makeText(this@EmailLoginActivity,R.string.string_for_fail_update_auth_token,Toast.LENGTH_SHORT).show()

                        }


                        5->{ Logger.v("로그인 비밀번호가 틀림")
                            Toast.makeText(this@EmailLoginActivity,R.string.string_for_not_match_password,Toast.LENGTH_SHORT).show()

                        }

                    }

                }


                override fun onFailure(call: Call<LoginCallback>, t: Throwable) {
                    Logger.v("이메일로그인  fail 값 callback 됨  -> ${t.message}")
                }
                
            })
        
    }





    //이메일 로그인 비밀번호  보이기 여부 설정 1-1
    fun show_pwd_shown_status(show_icon:ImageView,not_show_icon:ImageView){

        Logger.v("show_pwd_shown_status() 실행 됨")

        //보여지는 상태로 체크 되어있을 경우
        if(show_icon.isVisible){


            //아이콘 visible 처리
            show_icon.visibility=View.INVISIBLE
            not_show_icon.visibility=View.VISIBLE

            //에딧 텍스트 -패스워드 보여지게 처리 ->1234
            editxt_for_add_login_password.transformationMethod = PasswordTransformationMethod.getInstance()

        }else{//안보여짐 상태로 체크된 경우

            //아이콘 visible 처리
            show_icon.visibility=View.VISIBLE
            not_show_icon.visibility=View.INVISIBLE

            //에딧 텍스트 패스워드 안보여지게 처리 -> ****
            editxt_for_add_login_password.transformationMethod = HideReturnsTransformationMethod.getInstance()

        }

        //icon보이는 여부 바뀔때 패스워드를 하나라도 썼을경우 ->( 0>조건 넣음 )focus 가 마지막 위치를  가르치고 있어야 하낟.
        //아래 setSelection(edittext 길이-> 마지막 위치) 처리를 해주지 않으면,  계속해서 포커스가 처음위치로 가지게 된다.
        if(editxt_for_add_login_password.length()>0){
            editxt_for_add_login_password.setSelection(editxt_for_add_login_password.length())
        }

    }//show_pwd_shown_status끝



}//EmailLoginActivity 클래스 끝

