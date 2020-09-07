package com.example.leedonghun.sellinguseditemapp.Util

import android.content.Context
import android.graphics.Point
import android.view.Display
import android.view.WindowManager

/**
 * SellingUsedItemApp
 * Class: GetWindowSize.
 * Created by leedonghun.
 * Created On 2020-09-07.
 * Description:
 *
 * 디바이스의  화면 크기를  구해서
 * 가로 세로  길이를  retun 해준다.
 */
class GetWindowSize(context: Context) {

    private var windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var display=windowManager.defaultDisplay
    private var size=Point()

    init {
        display.getSize(size)
    }


    //디바이스의   넓이
    fun getX(): Int {
        return size.x
    }

    //디바이싀의 높이
    fun getY(): Int {
        return size.y
    }



}