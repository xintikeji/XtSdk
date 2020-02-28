package com.zt.httplibrary.Response;

/**
 * @author yqh
 * @version 1.0
 * @createDate 2019/3/15
 * @Description
 */

public class OldResponseResult
{
    /**
     * {"msg":"登陆成功",
     * "result":{"user_id":"201802051052099541","user_name":"测试用户","phone_number":"18311193299","token":"45f02836-5378-4cdc-b111-343549f00a2d"},
     * "success":true}
     * {"msg":"该用户不存在！","result":null,"success":false}
     * */

    private String success;
    private String msg;
    private String result;

    public String getSuccess()
    {
        return success;
    }

    public void setSuccess(String success)
    {
        this.success = success;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    @Override
    public String toString()
    {
        return "ResponseResult{" +
                "success='" + success + '\'' +
                ", msg='" + msg + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
