package com.example.leedonghun.sellinguseditemapp.Dialog


import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.example.leedonghun.sellinguseditemapp.R
import com.example.leedonghun.sellinguseditemapp.Util.*
import kotlinx.android.synthetic.main.update_user_info_dialog.*


/**
 * SellingUsedItemApp
 * Class: UpdateUserInFoDialog.
 * Created by leedonghun.
 * Created On 2020-09-06.
 * Description:
 *
 * 유저의 정보를  업데이트 할때 사용하는
 * 다이얼로그를 생성한다.
 */
class UpdateUserInFoDialog:DialogFragment() {

    //기기 화면 사이즈를 가져오는 클래스  1-1
    lateinit var getWindowSize: GetWindowSize

    //키보드 관련 처리 1-2
    lateinit var keyboardutil:KeyboardVisibilityUtils


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.update_user_info_dialog,container,false)
        Logger.v("실행")

        getWindowSize= GetWindowSize(requireActivity())//1-1
        keyboardutil=KeyboardVisibilityUtils()//1-2


        //다이얼로그 기본 설정
        //배경 투명 및  기본 타이틀 없앰
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false//취소 불가




        return view

    }//oncreateview 끝



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //프로필 같은경우에는 텍스트와 이미지뷰 같이  group으로 묶어 놈
        //한번에 클릭 가능하게.
        change_profile_image_group.setAllOnClickListener(View.OnClickListener{
           Logger.v("프로필 사진 편집 눌림")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //퍼미션 체크 진행

                PermissionCheck.apply {
                    get_context(requireActivity())
                    get_packagename(requireActivity().packageName)

                    if (check_permissions(arrayOf(READ_EXTERNAL_STORAGE))) {
                        Toast.makeText(requireActivity(), "다 허용됨", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireActivity(), "아직", Toast.LENGTH_SHORT).show()
                    }

                }
            }else{
                Toast.makeText(requireActivity(), "다 허용됨", Toast.LENGTH_SHORT).show()
            }

        })//이미지 편집 2-1

        btn_for_cancel.setOnClickListener(clickListener)//편집취소 버튼 클릭됨 2-2
        btn_for_complete.setOnClickListener(clickListener)//편집 완료 버튼 클릭됨 2-3
        container_of_profile_edit.setOnClickListener(clickListener)//전체 배경 클릭됨 -> 키보드 내려감 2-4

    }



     val clickListener=View.OnClickListener {

         when(it){

             btn_for_cancel->{//2-2
                 Logger.v("취소 버튼 클릭됨")
                 dismiss()
             }

             btn_for_complete->{//2-3
                 Logger.v("편집 하기 버튼 클릭됨")
                 dismiss()
             }

             container_of_profile_edit->{//2-4

                 Logger.v("배경 클릭됨-> 키보드 내려감")
                 keyboardutil.hideKeyboard(requireActivity(),it)
             }

         }


     }


    //constaring 그룹으로 묶인 뷰들  클릭 할때 사용
    private fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }

    override fun onResume() {
        super.onResume()
        Logger.v("실행")


        //다이얼로그 크기 설정
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        params?.width = (getWindowSize.getX() * 0.93).toInt()//전체 넓이에 93% 크리고 설정
        params?.height = (getWindowSize.getY() * 0.8).toInt()//전체 높이에 80% 크기로 설정
        dialog?.window?.attributes = params as WindowManager.LayoutParams


    }// onResume() 끝




}