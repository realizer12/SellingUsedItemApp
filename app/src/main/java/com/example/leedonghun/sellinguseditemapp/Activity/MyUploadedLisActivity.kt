package com.example.leedonghun.sellinguseditemapp.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import kotlinx.android.synthetic.main.my_uploaded_list_activity.*

/**
 * SellingUsedItemApp
 * Class: MyUploadedLisActivity.
 * Created by leedonghun.
 * Created On 2020-09-03.
 * Description:
 *
 * 내가 올린 물건들의  리스트를 볼수 있고
 * 새로운  물건을 업로드 할수 있다.
 */
class MyUploadedLisActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_uploaded_list_activity)
        Logger.v("실행")


        arrow_back_btn_img.setOnClickListener(clickListener)//뒤로가기 이미지 버튼 클릭됨 1-1
        upload_new_product_fab.setOnClickListener(clickListener)//새로운 물건 추가 플로팅 버튼 클릭됨 1-2


    }//oncreate() 끝


    //클릭 리스너 모음
    val clickListener= View.OnClickListener {

        when(it){

            arrow_back_btn_img->{//1-1
                Logger.v("뒤로가기 이미지 버튼 클릭")

                finish()
            }//1-1 끝


            upload_new_product_fab->{//1-2
                Logger.v("새 업로드  floating button 클릭됨")


            }//1-2 끝


        }

    }//clickListener 끝


}//클래스 끝