package com.example.leedonghun.sellinguseditemapp.Data.Login

import com.google.gson.annotations.SerializedName

/**
 * SellingUsedItemApp
 * Class: AutoLoginCallback.
 * Created by leedonghun.
 * Created On 2020-08-20.
 * Description:
 */


data class AutoLoginCallback(

    @SerializedName("response")
    var auto_login_response:Boolean,

    @SerializedName("status")
    var auto_login_callback_status:Int

)