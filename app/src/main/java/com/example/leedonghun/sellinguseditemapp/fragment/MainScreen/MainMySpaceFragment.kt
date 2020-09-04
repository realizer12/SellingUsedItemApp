package com.example.leedonghun.sellinguseditemapp.fragment.MainScreen

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.leedonghun.sellinguseditemapp.Activity.MainLoginActivity
import com.example.leedonghun.sellinguseditemapp.Activity.MyUploadedLisActivity
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Singleton.GlobalClass
import com.example.leedonghun.sellinguseditemapp.Util.DeleteSnsData
import kotlinx.android.synthetic.main.main_my_space_fragment.view.*

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view=inflater.inflate(R.layout.main_my_space_fragment,container,false)


       view.txt_for_logout.setOnClickListener(clickListener)//로그아웃 버튼 1-1
       view.txt_for_my_upload_product.setOnClickListener(clickListener)//내가 올린 물건들 보러가기 1-2


        return view
    }



    val clickListener=View.OnClickListener{

        when(it) {

            view?.txt_for_logout->{//1-1

                //로그아웃진행
                AlertDialog.Builder(requireActivity())
                    .setMessage(R.string.string_for_logout_alert)
                    .setCancelable(false)
                    .setPositiveButton(R.string.string_for_yes){dialog_for_logout, which ->

                        //uid지워주고 다시 로그인 화면으로 ㄱㄱ
                       DeleteSnsData(requireActivity()).logot_erase_uid()
                        val intent_to_go_main_login= Intent(requireActivity(),MainLoginActivity::class.java)
                        startActivity(intent_to_go_main_login)
                        requireActivity().finish()
                    }
                    .setNegativeButton(R.string.string_for_no){dialog_for_logout, which ->
                        dialog_for_logout.dismiss()
                    }.show()

            }//1-1 끝

            view?.txt_for_my_upload_product->{//1-2

                //내 업로드 엑티비티로 가기
                val intent_to_go_my_upload_activity=Intent(requireActivity(),MyUploadedLisActivity::class.java)
                startActivity(intent_to_go_my_upload_activity)

            }//1-2끝


        }

    }//clicklistner끝



}