package net.woorisys.lighting.control3.admin.sjp.server;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationController extends Application {

    private NetworkService _NetworkService;
    public NetworkService get_NetworkService() {
        return _NetworkService;
    }

    private static ApplicationController instance;

    public static ApplicationController getInstance()
    {
        if(instance==null)
            instance=new ApplicationController();

        return instance;
    }

    public NetworkService startApplication(String IP, String PORT)
    {
        String baseUrl= String.format("http://%s:%s/",IP,PORT);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        _NetworkService = retrofit.create(NetworkService.class);

        return _NetworkService;
    }
}
