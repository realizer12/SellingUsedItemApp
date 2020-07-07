package com.example.leedonghun.sellinguseditemapp.Singleton

/**
 * SellingUsedItemApp
 * Class: auth_phon_num.
 * Created by leedonghun.
 * Created On 2020-07-07.
 * Description:
 */
object auth_phon_num {

    var phonnumber:String=""

    fun get_phone_number(phonnumber:String){

        this.phonnumber=phonnumber

    }

}