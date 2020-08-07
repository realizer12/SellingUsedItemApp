package com.example.leedonghun.sellinguseditemapp.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.leedonghun.sellinguseditemapp.R

/**
 * SellingUsedItemApp
 * Class: LoadingDialog.
 * Created by leedonghun.
 * Created On 2020-07-03.
 * Description:
 *
 *로딩되는 중을 표현하기 위한 다이얼로그이다.
 *
 */
class LoadingDialog(context: Context) {

    //로딩용 다이얼로그
    private var dialog: Dialog= Dialog(context)
    val imageView: ImageView
    val animation: Animation


    //외부 context 받아서  로딩용 dialog 세팅 진행
    init {

       this.dialog.setContentView(R.layout.custom_loading_dialog)
       this.dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

       this.imageView=this.dialog.findViewById(R.id.loading_img)
       this.animation=AnimationUtils.loadAnimation(context, R.anim.loading)
   }


    //다이얼로그 보이기
    fun show_dialog(){

        imageView.startAnimation(animation)
        this.dialog.show()

    }

    //다이얼로그 숨기기
    fun dismiss_dialog(){
        this.dialog.dismiss()
    }


}