package com.example.leedonghun.sellinguseditemapp.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Util.Logger

/**
 * SellingUsedItemApp
 * Class: SellingUsedMainActivity.
 * Created by leedonghun.
 * Created On 2020-08-09.
 * Description:
 *
 * 로그인후에  보여지는 현재앱의  메인  엑티비티이다.
 *
 */
class SellingUsedMainActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selling_used_main_activity)
        Logger.v("실행")





    }//oncreate() 끝



}