package com.example.leedonghun.sellinguseditemapp.Util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.example.leedonghun.sellinguseditemapp.Dialog.UpdatePermissionOptionDialog
import com.example.leedonghun.sellinguseditemapp.Dialog.UpdateUserInFoDialog
import com.example.leedonghun.sellinguseditemapp.R

/**
 * SellingUsedItemApp
 * Class: PermissionCheck.
 * Created by leedonghun.
 * Created On 2020-09-09.
 * Description:
 *
 * 유저의  permission 체크가 필요한 부분에서
 * permission 다이얼로그를 띄어주는 기능의 클래스이다.
 */
class PermissionCheck() {
    companion object {

//        private lateinit var dialog:UpdatePermissionOptionDialog
        //퍼미션 list를 받아서
        //각 퍼미션이 사용자에게 승인 받았는지를 확인한다.
        //list의 퍼미션이 전부 확인 승인 받으면 true
        //그외는 전부 false를 반환한다.
        fun isPermissionChecked(
            requst_permission_array: Array<out String>,
            packagename: String,
            activity: Activity
        ):Boolean {
            Logger.v("실행")


            //이전에 거절되어서 다시 사용자에게 권한 request해야하는 퍼미션 array
            val re_permission_array: MutableList<String> = ArrayList()

            //다시 뭍지 않기 옵션이 체크된  퍼미션 array
            val option_checked_permission_array: MutableList<String> = ArrayList()

            //첫번째로 권한 뭍는 퍼미션 array
            val first_checked_array: MutableList<String> = ArrayList()


            //받아온  permission array for문으로
            //각가  체크 진행
            for (permission in requst_permission_array) {

                //해당 permission 으로  key값을 주고  처음 권한 체크인지 여부를 shared에 저장한다.
                //이유는 shouldShowRequestPermissionRationale에서 옵션 체크와 처음 체크 모두 false로 반환하기에
                //이 sharedpreference값으로 구분해준다.
                //default 값은 true(처음체크) 이다.
                val preference = activity.getPreferences(Context.MODE_PRIVATE)
                val isFirstCheck = preference.getBoolean(permission, true)


                //해당 퍼미션이 승인이 안된  퍼미션일 경우
                if ((ContextCompat.checkSelfPermission(
                        activity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED)
                ) {
                    Logger.v("$permission 은  승인 안된 permission임")

                    //사용자가 이전에 한번 권한을 거절했던 경우  true반환이다.
                    if (shouldShowRequestPermissionRationale(activity, permission)) {

                        //이때는 권한을  다시 요청 할수 있다.
                        //re_permission_array 담아서 모든 체크가  끝나고  permission  권유 진행
                        re_permission_array.add(permission)

                    } else {//처음으로 사용자에게 권한을 허락 받아야 하는 경우 or 다신 보지 않기 옵션이 추가된 권한의 경우 false 반환

                        //처음 체크인 경우이다.
                        if (isFirstCheck) {

                            //이제  체크를 진행할거니까  더이상 처음 체크가 아니므로 false
                            preference.edit().putBoolean(permission, false).apply()
                            first_checked_array.add(permission)

                        } else {//옵션이 체크된 경우이다.

                            option_checked_permission_array.add(permission)

                        }
                    }
                }
            }//for문 끝


            //퍼미션 다시 request할수 있는게 있으면  request 실행
            if (re_permission_array.size > 0) {

                requestPermissions(activity, requst_permission_array, PERMISSION_REQUEST_CODE)

                return false

            } else {//다시 request할수 있는게 아예 없는 경우는  첫번째 체크나, option체크를 한 경우로만 퍼미션이 채워진경우이다.

                //첫번째 체크인 경우가 있는 경우 -> 이땐  request dialog 띄울수 있으므로
                //request 를 진행한다.
                if (first_checked_array.size > 0) {
                    requestPermissions(activity, requst_permission_array, PERMISSION_REQUEST_CODE)

                    return false

                } else {

                    //첫번째 퍼미션 리스트도 없는 경우 및
                    //옵션체크된 퍼미션 리스트가 있는 경우이다.
                    //이때는 다이얼로그를 띄어 setting창에서 권한을 설정해야 함을 알려준다.
                    if (option_checked_permission_array.size > 0) {

                        val dialog=UpdatePermissionOptionDialog(activity,packagename)
                        dialog.show_dialog()

                        return false
                    }else{

                        Logger.v("퍼미션 전부 허용")

                        return true
                    }
                }

            }



        }//check_permissions 끝

    }

}//클래스 끝