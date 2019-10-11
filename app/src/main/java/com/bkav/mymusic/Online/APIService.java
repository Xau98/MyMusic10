package com.bkav.mymusic.Online;

public class APIService {
    private  static String base_url="https://appmusicttn.000webhostapp.com";

    public  static Dataservice getService(){
        return APIRetrofitclient.getClient(base_url).create(Dataservice.class);
    }


}
