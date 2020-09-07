package com.example.leedonghun.sellinguseditemapp.fragment.MainScreen

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.leedonghun.sellinguseditemapp.Activity.MainLoginActivity
import com.example.leedonghun.sellinguseditemapp.Activity.MyUploadedLisActivity
import com.example.leedonghun.sellinguseditemapp.Data.GetUserInFo.GetBasicUserInFo
import com.example.leedonghun.sellinguseditemapp.Dialog.UpdateUserInFoDialog
import com.example.leedonghun.sellinguseditemapp.PrivateInfo.ServerIp
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Retrofit.RetrofitClient
import com.example.leedonghun.sellinguseditemapp.Util.DeleteSnsData
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import kotlinx.android.synthetic.main.main_my_space_fragment.*
import kotlinx.android.synthetic.main.main_my_space_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * SellingUsedItemApp
 * Class: MainMySpaceFragment.
 * Created by leedonghun.
 * Created On 2020-09-01.
 * Description:
 *
 * 메인 화면에서 내공간  화면을
 * 보여준다.
 *
 */

class MainMySpaceFragment:Fragment() {

    lateinit var retrofitClient: RetrofitClient
    lateinit var myspace_fragment_dialog: UpdateUserInFoDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Logger.v("실행")
        //유저의 정보를 가지고 와서   기본정보 가져오기 진행
        val sharedPreferences=context.getSharedPreferences(getString(R.string.shared_preference_name_for_store_uid),
            Context.MODE_PRIVATE)
        val uid=sharedPreferences.getString(getString(R.string.shared_key_for_auto_login),null)

        //서버로부터 기본 정보 가져오기 진행
        get_user_profile_info(user_uid = uid)

    }//onattach() 끝



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         Logger.v("실행")  
        val view=inflater.inflate(R.layout.main_my_space_fragment,container,false)

        view.txt_for_logout.setOnClickListener(clickListener)//로그아웃 버튼 1-1
        view.txt_for_my_upload_product.setOnClickListener(clickListener)//내가 올린 물건들 보러가기 1-2
        view.btn_for_profile_edit.setOnClickListener(clickListener)//프로필 편집 클릭됨 1-3


        myspace_fragment_dialog= UpdateUserInFoDialog()


        return view
    }



    //클릭 이벤트 모음
    val clickListener=View.OnClickListener{

        when(it) {

            txt_for_logout->{//1-1

                //로그아웃진행
                AlertDialog.Builder(requireActivity())
                    .setMessage(R.string.string_for_logout_alert)
                    .setCancelable(false)
                    .setPositiveButton(R.string.string_for_yes){dialog_for_logout, which ->

                        Logger.v("로그아웃  yes")

                        //uid지워주고 다시 로그인 화면으로 ㄱㄱ
                       DeleteSnsData(requireActivity()).logot_erase_uid()
                        val intent_to_go_main_login= Intent(requireActivity(),MainLoginActivity::class.java)
                        startActivity(intent_to_go_main_login)
                        requireActivity().finish()


                    }
                    .setNegativeButton(R.string.string_for_no){dialog_for_logout, which ->
                        Logger.v("로그아웃 no")
                        dialog_for_logout.dismiss()
                    }.show()

            }//1-1 끝

            txt_for_my_upload_product->{//1-2

                Logger.v("내가 올린 물건 보러가기 탭 눌림")

                //내 업로드 엑티비티로 가기
                val intent_to_go_my_upload_activity=Intent(requireActivity(),MyUploadedLisActivity::class.java)
                startActivity(intent_to_go_my_upload_activity)

            }//1-2끝


            btn_for_profile_edit->{//1-3

                Logger.v("프로필 편집 눌림")
                myspace_fragment_dialog.show(requireActivity().supportFragmentManager,"update_info_dialog")


            }

        }

    }//clicklistner끝


    //유저의 프로필 정보들을 가지고 온다.
    fun get_user_profile_info(user_uid:String){

        retrofitClient= RetrofitClient(ServerIp.baseurl)
        retrofitClient.apiService.get_user_profile_info(user_uid = user_uid)
            .enqueue(object : Callback<GetBasicUserInFo>{


                override fun onResponse(call: Call<GetBasicUserInFo>, response: Response<GetBasicUserInFo>) {

                    if(response.isSuccessful){

                        Logger.v("기본 정보 가져오기 성공")

                        val nickname=profile_result_null_check(response.body()?.nickname,0)
                        val image_url=profile_result_null_check(response.body()?.image_url,1)
                        val coin_amount=response.body()?.coin_amount

                        txt_for_user_nickname?.text = nickname
                        txt_for_present_coin?.text=String.format(getString(R.string.string_for_present_coin_amount),coin_amount)

                        if(image_url.isNullOrEmpty()) {
                            profile_image.setImageResource(R.drawable.profile_img)
                        }else{
                            Glide.with(requireActivity()).load(image_url).into(profile_image)
                        }

                    }else{

                        Logger.v("기본 정보 가져오는데  문제 생김")
                        Logger.v("errorbody->  ${response.errorBody()?.string()}")
                        Logger.v("server code-> ${response.code()}")
                    }

                }
                override fun onFailure(call: Call<GetBasicUserInFo>, t: Throwable) {
                    Logger.v("유저의 기본정보 가져오기 실패 하였습니다. ${t.message}")
                }


            })


    }

    //유저 프로필 정보를 받아올때
    //null 값으로 들어오는 부분에대한 처리이다.
    //null값으로 올수 있는 값들 nickname , imageurl
    fun profile_result_null_check(info_value:String?,value_status:Int):String?{

        var return_value:String?=""

        if(info_value.isNullOrEmpty()) {

            when (value_status) {
                0 -> {//닉네임일때
                   return_value= R.string.string_for_nickname_null.toString()
                }
                1 -> {//이미지 url
                    return_value= null
                }
            }

        }else{
            return_value=info_value
        }

        return return_value
    }//profile_result_null_check 끝




}//클래스 끝