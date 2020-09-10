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
class PermissionCheck(
) {
    //객체 선언 없이  진행 static 형태로
    companion object {

       lateinit var activity:Activity
       lateinit var packgename:String

        fun get_packagename(packagename: String): Companion {
            this.packgename= packagename
            return this
        }

        fun get_context(activity: Activity):Companion{
            this.activity=activity
            return  this
        }


        //퍼미션 list를 받아서
        //각 퍼미션이 사용자에게
        fun check_permissions(
            requst_permission_array: Array<out String>
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


            if (re_permission_array.size > 0) {//퍼미션 다시 할수 있는게 있으면  request 실행
                requestPermissions(activity, requst_permission_array, PERMISSION_REQUEST_CODE)

                return false
            } else {

                if (first_checked_array.size > 0) {
                    requestPermissions(activity, requst_permission_array, PERMISSION_REQUEST_CODE)

                    return false
                } else {

                    if (option_checked_permission_array.size > 0) {

                        AlertDialog.Builder(activity).setMessage("해당 기능을 사용하기위해선 \n제공되는 권한 체크를 \n모두 허용하셔야 합니다")
                            .setPositiveButton("네") { dialog, i ->
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", packgename, null)
                                intent.data = uri
                                activity.startActivity(intent)
                                dialog.dismiss()
                            }
                            .setNegativeButton("아니오") { dialog, i ->

                                dialog.dismiss()
                            }.show()

                        return false
                    }else{

                        Logger.v("다 올 허용")

                        return true
                    }
                }

            }



        }//check_permissions 끝


    }

}//클래스 끝