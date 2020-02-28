package com.xt.library.beans

import java.io.Serializable

/**
 * @author admin
 * @Date 2019/4/26
 * @Version 1.0
 */
class MqMessageBean : Serializable {
     var topic: String=""
     var body: String=""
     override fun toString(): String {
          return "MqMessageBean(topic='$topic', body='$body')"
     }

}