package com.example.leedonghun.sellinguseditemapp.Singleton

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo

/**
 * SellingUsedItemApp
 * Class: GlobalClass.
 * Created by leedonghun.
 * Created On 2020-08-11.
 * Description:
 *
 * 현재  프로젝트의  applicaiton 클래스이다.
 * 우선  logger를  구현하기 위해
 * debuggable 여부를   application 단위로  체크해준다.
 */
class GlobalClass :Application() {

    companion object{

        //debug 가능 여부 -> default 값은  false
        var DEBUG_AVAILABLE:Boolean=false
    }

    //앱 시작시  oncreate 실행됨
    override fun onCreate() {
        super.onCreate()

        //debug 가능 여부 넣어줌.
        DEBUG_AVAILABLE=isDebuggable(this)

    }

    //debug 가능 여부를  체크해준다.
    //release 버전에서는  false 로 체크된다.
    fun isDebuggable(context: Context): Boolean {
        return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
}