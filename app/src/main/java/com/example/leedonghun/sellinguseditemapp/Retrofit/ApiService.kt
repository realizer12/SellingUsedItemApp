package com.example.leedonghun.sellinguseditemapp.Retrofit

import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * SellingUsedItemApp
 * Class: ApiService.
 * Created by leedonghun.
 * Created On 2020-06-30.
 * Description:
 *
 * retrofit http 통신에서 사용할 http api들을 제공하는 인터페이스
 *
 */
interface ApiService {


    //retrofit 서버 통신 되는지 확인용
    @POST("retrofitt_server_connection_check.php")
    fun server_connetion_test():Call<String>



}