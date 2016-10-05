package es.vallesoft.util;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.List;

public class HTTPRequest {

    public HTTPRequest(String Url, List<NameValuePair> params, final String op, final Handler success){
        // Create a new HttpClient and Post Header

        if(!Url.contains("http://")) Url = "http://"+Url;
        final HttpClient httpclient = new DefaultHttpClient();
        final HttpPost httppost = new HttpPost(Url);
         try {

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            // Execute HTTP Post Request
            new Thread(){
                public void run(){
                    try {
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity entity = response.getEntity();

                        if (success!=null) {
                           sendRespuesta(success, EntityUtils.toString(entity), op);
                        }

                    } catch (Exception e) {

                        // TODO Auto-generated catch block
                       if(success!=null) {
                           sendRespuesta(success, "error", op);
                        }
                        e.printStackTrace();
                    }
                }
            }.start();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void sendRespuesta(Handler success, String res, String op){
        Message msg = success.obtainMessage();
        Bundle bundle = msg.getData();
        if (bundle == null) bundle = new Bundle();
        bundle.putString("RESPONSE", res);
        bundle.putString("op", op);
        msg.setData(bundle);
        success.sendMessage(msg);
    }

}