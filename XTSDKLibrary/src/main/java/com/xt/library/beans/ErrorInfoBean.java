package com.xt.library.beans;

/**
 * @author Administrator
 * @Date 2018/4/25
 * @Version 1.0
 */
public class ErrorInfoBean
{
    private String interfaceName;
    private String errorCode;
    private String errorBody;

    public ErrorInfoBean(String interfaceName, String errorCode, String errorBody)
    {
        this.interfaceName = interfaceName;
        this.errorCode = errorCode;
        this.errorBody = errorBody;
    }

    public String getInterfaceName()
    {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName)
    {
        this.interfaceName = interfaceName;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorBody()
    {
        return errorBody;
    }

    public void setErrorBody(String errorBody)
    {
        this.errorBody = errorBody;
    }

    @Override
    public String toString()
    {
        return "ErroInfoBean{" +
                "interfaceName='" + interfaceName + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorBody='" + errorBody + '\'' +
                '}';
    }
}
