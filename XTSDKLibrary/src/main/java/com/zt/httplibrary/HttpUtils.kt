package com.zt.httplibrary

/**
 * @author admin
 * @Date 2019/4/25
 * @Version 1.0
 */
class HttpUtils {

    private constructor()

    var baseUrl = ""
    var restfulBaseUrl = ""
    var wpRestfulBaseUrl = ""
    var swRestfulBaseUrl = ""
    companion object {

        private val httpUtils = HttpUtils()

        public fun getInstance(): HttpUtils {
            return httpUtils
        }
    }
    fun initAllUrl(url: String) {
        baseUrl = url
        restfulBaseUrl = url
        wpRestfulBaseUrl = url
        swRestfulBaseUrl = url
    }


    fun initBaseUrl(url: String) {
        baseUrl = url
    }

    fun initRestfulUrl(url: String) {
        restfulBaseUrl = url
    }

    fun initWPRestfulUrl(url: String) {
        wpRestfulBaseUrl = url
    }
    fun initSWRestfulUrl(url: String) {
        swRestfulBaseUrl = url
    }
}