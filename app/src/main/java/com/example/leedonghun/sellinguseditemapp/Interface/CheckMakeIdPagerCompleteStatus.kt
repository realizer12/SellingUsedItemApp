package com.example.leedonghun.sellinguseditemapp.Interface

/**
 * SellingUsedItemApp
 * Class: check_all.
 * Created by leedonghun.
 * Created On 2020-06-17.
 *
 * Description: 회원가입을 하는데 있어서,
 * 4개의 정보 입력  화면은  프래그먼트로 구성되어있다.
 * 그리고 다음 정보 입력 칸으로 넘어가는 버튼은 부모  엑티비티에 존재한다.
 * 그러므로, 프래그먼트에서 정보 입력이 맞췄을떄 다음 버튼이 활성화가 되게 하려면,
 *
 * 엑티비티와 프래그먼트 사이에 소통이 가능해야한다.
 * 이를 진행하기 위한  인터페이스이다.
 */
interface CheckMakeIdPagerCompleteStatus {

    //회원가입  각 페이져 정보 입력 완성여부 받음.
    fun CheckMakeIdPagerComplete_all_or_not(status:Boolean,check_page_number:Int)

}