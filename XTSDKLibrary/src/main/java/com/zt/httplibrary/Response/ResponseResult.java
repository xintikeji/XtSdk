package com.zt.httplibrary.Response;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2019/3/15
 * @Description
 */

public class ResponseResult
{
    /*{
        "code": "200",
        "data": [],
        "msg": "\u67e5\u8be2\u6210\u529f",
        "total": 0
    }*/

    private String code;
    private String msg;
    private String data;
    private String total;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public String getTotal()
    {
        return total;
    }

    public void setTotal(String total)
    {
        this.total = total;
    }

    @Override
    public String toString()
    {
        return "ResponseResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", total='" + total + '\'' +
                '}';
    }
}
