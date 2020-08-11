package com.example.leedonghun.sellinguseditemapp.Util

import android.util.Log
import com.example.leedonghun.sellinguseditemapp.Singleton.GlobalClass

/**
 * SellingUsedItemApp
 * Class: Logger.
 * Created by leedonghun.
 * Created On 2020-08-11.
 * Description:
 *
 * 기존 log 사용의 경우는  release 할때  지우기 위해서 따로 처리를 해야되고,
 * 지워지지 않을때도 있다.
 * 하지만, 이렇게 사용하게 되면  패키지이름  메소드 이름 등도 다오고 해당 thread 를  찾을수도 있으며
 * release시 자동적으로 로그를  안나오게 만들어 줄수 있음.
 */
class Logger private constructor(){

    companion object{

        private val DEBUG = GlobalClass.DEBUG_AVAILABLE
        private const val TAG = "Check_Logger"

        fun v(msg: String) = logger(Log.VERBOSE, msg)
        fun d(msg: String) = logger(Log.DEBUG, msg)
        fun i(msg: String) = logger(Log.INFO, msg)
        fun w(msg: String) = logger(Log.WARN, msg)
        fun e(msg: String) = logger(Log.ERROR, msg)


        private fun logger(priority: Int, msg: String) {

            //debug 가능한 상태일때는  log를 출력한다.
            //해당 클래스 이름  메소드 이름 파일이름 및  라인 넘버  출력하게 함.
            if (DEBUG) {
                val message =
                        "[${Thread.currentThread().stackTrace[4].fileName} => "+
                        "${Thread.currentThread().stackTrace[4].methodName}()] :: $msg" +
                        "(${Thread.currentThread().stackTrace[4].fileName}:${Thread.currentThread().stackTrace[4].lineNumber})"
                Log.println(priority, TAG, message)
            }
        }



    }




}