package com.example.leedonghun.sellinguseditemapp.Singleton

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import com.example.leedonghun.sellinguseditemapp.Util.Logger
import com.google.firebase.FirebaseApp

import com.google.firebase.iid.FirebaseInstanceId


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

        //사용자 식별 id
        var uniqueID=""

    }

    //앱 시작시  oncreate 실행됨
    override fun onCreate() {
        super.onCreate()




     /** 기기의  firebase message 라이브러리에서 제공하는
        기기의  uuid를  firebasinstanceid로  받을수 있어서 사용함.
        앱이  instance id를  삭제하거나 -> feat  deleteInstanceid

        1.앱이  다른  기기에서  복구되거나
        2.앱이 다시 설치되거나
        3.유저가  앱데이터를  clear했을떄
        4.현재 id는 사라진다.   */
        uniqueID=FirebaseInstanceId.getInstance().id


        //debug 가능 여부 넣어줌.
        DEBUG_AVAILABLE=isDebuggable(this)
    }




    //debug 가능 여부를  체크해준다. (logger 안보이게 할려고)
    //release 버전에서는  false 로 체크된다.
    fun isDebuggable(context: Context): Boolean {
        return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }


}