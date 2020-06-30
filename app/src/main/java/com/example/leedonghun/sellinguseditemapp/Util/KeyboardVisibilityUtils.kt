package com.example.leedonghun.sellinguseditemapp.Util

import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.Window


/**
 * SellingUsedItemApp
 * Class: SoftkeyboardDetection.
 * Created by leedonghun.
 *
 * Created On 2020-06-12
 *.
 * Description: 키보드의 visible 형태를  판단해서 알려준다.
 * 이부분  다시 한 번  공부하기
 */
class KeyboardVisibilityUtils(

    private val window: Window,
    private val onShowKeyboard: ((keyboardHeight: Int, visibleDisplayFrameHeight: Int) -> Unit)? = null,
    private val onHideKeyboard: (() -> Unit)? = null) {


    private val MIN_KEYBOARD_HEIGHT_PX = 150
    private val windowVisibleDisplayFrame = Rect()
    private var lastVisibleDecorViewHeight: Int = 0



    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        window.decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)

        val visibleDecorViewHeight = windowVisibleDisplayFrame.height()

        // Decide whether keyboard is visible from changing decor view height.
        if (lastVisibleDecorViewHeight != 0){

            if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {

                //Calculate current keyboard height (this includes also navigation bar height when in fullscreen mode).
                val currentKeyboardHeight = window.decorView.height - windowVisibleDisplayFrame.bottom

                //Notify listener about keyboard being shown.
                onShowKeyboard?.invoke(currentKeyboardHeight, windowVisibleDisplayFrame.height())

            }else if(lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight){

                // Notify listener about keyboard being hidden.
                onHideKeyboard?.invoke()
            }
        }

        // Save current decor view height for the next call.
        lastVisibleDecorViewHeight = visibleDecorViewHeight
    }

    init{
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }


    fun detachKeyboardListeners() {
        window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
    }

}