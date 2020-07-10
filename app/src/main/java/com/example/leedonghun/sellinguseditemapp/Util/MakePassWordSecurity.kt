package com.example.leedonghun.sellinguseditemapp.Util

import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and
import kotlin.random.Random

/**
 * SellingUsedItemApp
 * Class: MakePassWordSecurity.
 * Created by leedonghun.
 * Created On 2020-07-08.
 *
 * Description: 이번엔  회원 가입할때,  sns 회원 이외
 * 일반 회원의 경우는  비밀번호를  등록해야 하는데,
 * 이때, 회원가입시 해당 비밀번호를  sha 256 단방향 암호화를  해주자.
 * sort 값으로  2자리  숫자를  랜덤 생성해서  암호화를 진행한다.
 *
 * 서버에는  해당  sort값과 암호화된  해쉬값이  저장된다.
 */
class MakePassWordSecurity {

  //sha 256 암호화  할때  비밀번호와  함께  넣어질  sort 값이다.
  private var sort_value:Int=0

  //해쉬 생성시  아래 문자들안에서만 진행
  private  val HEX_CHARS = "0123456789ABCDEF"



   //sort 값  랜덤 생성해줌.
   private fun make_random_sort_value(){

       //10 에서 99 사이의  랜덤  숫자 (2자리수로 sort 값을 받기 위해서)
       sort_value=(10..99).random()

   }

   //client가 쓴 비밀 번호를 넣어서 sort값  같이 넣어서 256 단방향 암호화  진행 함수
   fun make_sha_256_hash_value(password:String):JSONObject{

       //랜덤으로 생성한  sort 값 생성 -  먼저  생성 해줘야됨.
       make_random_sort_value()

       //랜덤으로 생성한  sort값을  비밀번호 앞에 넣어줘서  중복이  최대한  줄도록 한다
       val final_psw_value="${sort_value}"+password

       //회원가입시 반환할 해쉬값이랑 sort값  서버로 보내주기 위하여,
       //jsonobject 로  두개 값 모두 보내줌.
       val josn_value_for_password_security:JSONObject= JSONObject()
       josn_value_for_password_security.put("sort_value",sort_value)//sort 값  넣어줌.


       //*****************************sha 256 해쉬값 생성 하기*******************************//
       val bytes = MessageDigest
           .getInstance("SHA-256")
           .digest(final_psw_value.toByteArray())

       val sha = StringBuilder(bytes.size * 2)

       bytes.forEach {
           val i = it.toInt()
           sha.append(HEX_CHARS[i shr 4 and 0x0f])
           sha.append(HEX_CHARS[i and 0x0f])
       }
       //*****************************sha 256 해쉬값 생성 끝*********************************//



       josn_value_for_password_security.put("sha_value",sha)//sha 256 해쉬값  생성 넣어줌

       return josn_value_for_password_security
    }


}



