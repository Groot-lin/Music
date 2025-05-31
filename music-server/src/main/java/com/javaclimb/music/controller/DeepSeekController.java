package com.javaclimb.music.controller;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/deepSeek")
public class DeepSeekController {
    private static final String API_KEY = "sk-1b27465cc6aa43f0b57c534d732fea8f";
    private static final String BASE_URL = "https://api.deepseek.com";
    private static final OkHttpClient client =  new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();


    @PostMapping("/chat")
    public String createEvent(@RequestBody String req) {
        System.out.println("稍等,正在为你推荐......");
        String response = "";
        try {
            response = createChatCompletion(false,req);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }
    private static String createChatCompletion(boolean streamFlag, String question) throws Exception {

        String url = BASE_URL + "/v1/chat/completions";

        JSONArray messages = new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", ",你是一名资深音乐推荐官," +
                        "你最擅长的事情就是给用户推荐歌曲.当别人说明自己状态和心情或者询问你推荐听什么歌曲时你就回给别人推荐中文歌曲,推荐数量是三首,并且说明为什么," +
                        "并且每段不能回复超过200字！此外,当别人问你其他不相关问题时,你只能回复:抱歉,我只能向你推荐一些歌曲哦!如果别人问你是谁或者和音乐相关的问题,你就正常回答不用推荐歌曲!"))
//                .put(new JSONObject().put("role", "user").put("content", "Hello! How can I assist you today? \uD83D\uDE0A"));
                .put(new JSONObject().put("role", "system").put("content", "回答格式为:" +
                        "我推荐(只能在第一段前出现一次),然后另起一行为第一段,标明序号,歌曲名用书名号,后面接短横线再接作者,说明为什么,为第一段,以此类推,每段空一行"))
                .put(new JSONObject().put("role", "user").put("content", question));

        JSONObject jsonBody = new JSONObject()
                .put("model", "deepseek-chat")
                .put("messages", messages)
                .put("stream", streamFlag);

        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if(!streamFlag) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                JSONObject jsonResponse = new JSONObject(responseBody.string());
                String ans = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
                System.out.println(ans);


                return ans;
            }
        }else{
            //流式回答
        }
        return null;
    }

}