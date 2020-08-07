package com.example.leedonghun.sellinguseditemapp.Retrofit

import com.example.leedonghun.sellinguseditemapp.PrivateInfo.ServerIp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * SellingUsedItemApp
 * Class: RetrofitClient.
 * Created by leedonghun.
 * Created On 2020-06-30.
 *
 * Description:
 *
 * retrofitclient 라는 클래스를 하나  만들어서 ,
 * 미리 retrofit 사용에 필요한 정보들을  객체 생성시
 * 반환 시킬수 있도록 만들어 놓았다,
 *
 */
class RetrofitClient(url:String) {

    private var retrofit:Retrofit=Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


     var apiService:ApiService=retrofit.create(ApiService::class.java)

}