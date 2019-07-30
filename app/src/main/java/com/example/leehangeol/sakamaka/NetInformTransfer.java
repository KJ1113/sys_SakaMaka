package com.example.leehangeol.sakamaka;

import java.util.ArrayList;

public class NetInformTransfer {

    private static String ServerUrl = "----------:3000";
    private String ServerResponse = null;
    private ArrayList<String> ReqBodyinform = new ArrayList<String>();

    private String userName="";

    public void setUserName(String userName){this.userName=userName;}
    public String getServerResponse() {
        return ServerResponse;
    }
    public static String getServerUrl() {
        return ServerUrl;
    }
    public static void setServerUrl(String serverUrl) {
        ServerUrl = serverUrl;
    }
    public void setServerResponse(String serverResponse) {
        ServerResponse = serverResponse;
    }
    public void setBodyinform(String input){
        ReqBodyinform.add(input);
    }
    public String getindexTovalue(int index){
        return ReqBodyinform.get(index);
    }
    public void removeAllvalue(){
        ReqBodyinform.clear();
    }
}
