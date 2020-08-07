package com.example.leedonghun.sellinguseditemapp.Singleton

/**
 * SellingUsedItemApp
 * Class: SnsEmailValue.
 * Created by leedonghun.
 * Created On 2020-08-08.
 * Description:
 *
 * sns 로그인의 경우  이메일을  받아와서
 * 나중에 가입시  서버로 넘겨줘야 한다. 그러므로,
 * 그래서  회원가입이 끝날때까지
 * 공유할수 있는  싱글톤 객체를 만들어서  공유 시키기로함.
 */
object SnsEmailValue {

    //sns 로그인으로 가져온 이메일
    var sns_login_email:String=""

    //sns 로그인 이메일 받아서  넘겨줌.
    fun get_sns_email(sns_email:String){
        this.sns_login_email=sns_email
    }

    //singltone에 있던 값 없애줌.
    //필요한 부분이 끝나면,  더이상
    //해당 값을 가지고 있으면  유출 될수 있으므로,
    //없애준다,
    fun delete_sns_email(){

        this.sns_login_email=""

    }
}