package com.example.leedonghun.sellinguseditemapp.Interface

import org.json.JSONObject

/**
 * SellingUsedItemApp
 * Class: NewMemberInfo.
 * Created by leedonghun.
 * Created On 2020-07-12.
 * Description:
 *
 * 회원가입 부분에서 3번째 프래그먼트에서
 * 회원가입 정보를 다 적고,  네번째 프래그 먼트 (선호도 조사) 부분 들어가기위해
 * 다음 버튼 누를때,  입력된 회원 정보를   서버에  업로드 하여,  회원가입을  진행하려고 한다.
 * 회원가입 정보  취합해서  다음 버튼이  Parent 엑티비티에 있으니  인터페이스로  넘겨주자.
 *
 * 왜  네번째 프래그먼트는 남겨둠??
 * 네번째 프래그먼트 부터는 선호도조사여서  일단   세번째 회원 정보 입력까지 하면
 * 회원가입이 되는 상태로 구현 할 예정
 * 그래서 네번째 프래그먼트에서 그냥  넘어가술 있다는 다이얼로그 넣어주고,
 * 혹시나 선호도 조사를 이행하지 않았다면, 로그인때마다 선호도 조사 엑티비티를  띄어 주자.
 */
interface NewMemberInfo {

    //JOSN object로  회원 정보 넘겨주기.
    fun new_member_info_with_json(memberInfo:JSONObject)


}//NewMemberInfo 끝