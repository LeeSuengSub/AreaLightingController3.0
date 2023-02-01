package net.woorisys.lighting.control3.admin.sjp.server;

import net.woorisys.lighting.control3.admin.sjp.server.returnvalue.returnlogin;

import lombok.Getter;
import lombok.Setter;

public class LoginResult {

    public static final int RETURN_CODE_SUCCESS = 0;
    public static final int RETURN_CODE_FAIL = -1;

    @Getter
    @Setter
    private int returnCode;
    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private returnlogin result=null;

    public LoginResult(){this.returnCode=RETURN_CODE_SUCCESS;}
    public LoginResult(int returnCode){this.returnCode=returnCode;}
    public LoginResult(returnlogin object){this.result=object;}

    public LoginResult(int returnCode, returnlogin object)
    {
        this(returnCode);
        this.result=object;
    }

}

