package com.example.leedonghun.sellinguseditemapp.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import kotlinx.android.synthetic.main.selling_used_main_activity.*


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
        setUpNavigation()

        // TODO: 2020-08-27 여기서 부터   이제  진행 ㄱㄱ 하자 




    }//oncreate() 끝


    //bottom navigation을  jetpack navigation controller로 연결
    fun setUpNavigation() {

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

        val navController=navHostFragment!!.navController

        NavigationUI.setupWithNavController(
            bottom_navigation_view,
            navController
        )

        //프래그먼트  중복 실행을 방지하기 위해서 추가함
        bottom_navigation_view.setOnNavigationItemReselectedListener {}


    }


}//SellingUsedMainActivity 클래스 끝