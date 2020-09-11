package com.example.leedonghun.sellinguseditemapp.Dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.leedonghun.sellinguseditemapp.R
import kotlinx.android.synthetic.main.custom_permission_option_check_dialog.*


/**
 * SellingUsedItemApp
 * Class: UpdatePermissionOptionDialog.
 * Created by leedonghun.
 * Created On 2020-09-11.
 * Description:
 *
 *  사용자가 권한 설정을 전부
 * '다시 묻지 않음'으로 설정하고
 *  거부를 진행하였을때, 사용자에게
 *  세팅창에서 권한을 모두 허용하는 방법을
 *  gif로 보여주며 권한 허용을 유도하는  클래스이다.
 *
 */
class UpdatePermissionOptionDialog(context: Context,packagename:String) {


    private var dialog: Dialog= Dialog(context)
    private var context:Context = context
    private var packagename:String = packagename

    //외부 context 받아서  로딩용 dialog 세팅 진행
    init {

        dialog.setContentView(R.layout.custom_permission_option_check_dialog)
        dialog.setCancelable(false)//취소 불가

        //glide 라이브러리를 통해 중고마켓 gif 로고 파일을 넣어줌.
        //glide 캐쉬 저장은 skip시킴  왜냐면
        //다이얼로그 취소했다가 진행해도 남아있던 캐쉬메모리때문에
        //gif 계속 돌아가기 때문.
        Glide.with(context)
            .load(R.drawable.permission_check_info)
            .diskCacheStrategy(DiskCacheStrategy.NONE )
            .skipMemoryCache(true)
            .into(this.dialog.img_for_permission_check_info_gif)



    }



    //다이얼로그 보이기
    fun show_dialog(){

        dialog.show()

        //다이얼로그 권한 설정 가기  버튼 클릭됨
        dialog.btn_for_complete.setOnClickListener{

            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packagename, null)
            intent.data = uri
            context.startActivity(intent)

            Glide.with(context).clear(this.dialog.img_for_permission_check_info_gif)
            dialog.dismiss()

        }


        //다이얼로그 권한 취소 설정가기
        dialog.btn_for_cancel.setOnClickListener {
            Glide.with(context).clear(this.dialog.img_for_permission_check_info_gif)
            dialog.dismiss()
        }

    }

    //다이얼로그 숨기기
    fun dismiss_dialog(){

        dialog.dismiss()
    }

}//클래스 끝