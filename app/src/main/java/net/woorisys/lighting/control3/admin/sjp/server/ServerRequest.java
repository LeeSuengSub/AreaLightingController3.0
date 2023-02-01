package net.woorisys.lighting.control3.admin.sjp.server;


import android.content.Context;

import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerRequest {

    private final static String IP="182.162.104.154";
    private final static String PORT="8080";

    Context context;
    ApplicationController applicationController=null;
    NetworkService networkService=null;

    @Setter
    private ServerRequestResult listener;

    public ServerRequest(Context context)
    {
        this.context=context;
        applicationController=ApplicationController.getInstance();
        networkService=applicationController.startApplication(IP,PORT);
    }

    public void Login(String userID,String password)
    {
        Call<LoginResult> getCall=networkService.Login(userID,password);
        getCall.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if(response.body()==null)
                {
                    listener.Result(ResultType.Login,false,"Response Body Null");
                    return;
                }

                int ack=response.body().getReturnCode();
                String message=response.body().getMessage();

                switch (ack)
                {
                    case LoginResult.RETURN_CODE_SUCCESS:
                        listener.Result(ResultType.Login,true,message);
                        break;

                    case LoginResult.RETURN_CODE_FAIL:
                        listener.Result(ResultType.Login,false,message);
                        break;
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                listener.Result(ResultType.Login,false,t.getMessage());
            }
        });
    }

}
