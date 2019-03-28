package com.bottle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author work
 * 爬取抖音音乐demo
 */
public class CrawlerDemo {
    /**
     * 记录爬取数量条数
     */
    private static int counts = 0;

    public static void main(String[] args) throws IOException {
        //getDouYin();
        getXiaoHongShu();
    }
    /**
     * 爬取小红书数据
     * 注意: 小红书有反采集常用的签名保护机制,改变参数导致 参数错误
     */
    private static void getXiaoHongShu() throws IOException {
        String str = FileUtils.readFileToString(new File("src/main/java/com/bottle/xiaohongshu.json"));
        JSONArray jsonArray = JSONArray.parseArray(str);
        for (int i = 0; i < jsonArray.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("shield",jsonArray.getJSONObject(i).getString("shield"));
            String urlStr = MyUtil.sendGet(jsonArray.getJSONObject(i).getString("url"),map);
            JSONObject urlJO = JSONObject.parseObject(urlStr);
            JSONArray dataJA = urlJO.getJSONArray("data");
            System.out.println("==========================================="+jsonArray.getJSONObject(i).getString("name")+"===========================================");
            for (int j = 0; j < dataJA.size(); j++) {
                JSONObject dataJO = dataJA.getJSONObject(j);
                String name = dataJO.getString("name");
                String mp3Url = dataJO.getString("url");
                String singer = dataJO.getString("singer");
                System.out.println(singer+"-"+name+"-"+mp3Url);
                counts=counts+1;
            }
        }
        System.out.println("共获取" + counts + "条数据");
    }

    /**
     * 爬取抖音数据
     */
    private static void getDouYin() {
        //抖音歌曲分类URL
        String typeUrl = "https://api.amemv.com/aweme/v1/music/collection/?iid=30600516332&ac=WIFI&os_api=18&app_name=aweme&channel=App%20Store&idfa=D065159C-A8B6-4D89-8B13-F4C785A53669&device_platform=iphone&build_number=17805&vid=CA468973-B544-44F5-9163-A832328E4E9C&openudid=2d20f63eeb3ef810a3c6c0ac22827db69f28a7b3&device_type=iPhone6,2&app_version=1.7.8&device_id=7426456155&version_code=1.7.8&os_version=9.0.1&screen_width=640&aid=1128&mas=0079e957459e03ae25a25dbc69092701145a75afd2fda122ed66ae&as=a1951ff9d9880c48618015&ts=1553070217";
        //抖音歌曲分类明细URL  mc_id 分类id  cursor 当前歌曲条数
        String url = "https://aweme.snssdk.com/aweme/v1/music/list/?cursor=0&iid=30600516332&ac=WIFI&os_api=18&app_name=aweme&channel=App%20Store&idfa=D065159C-A8B6-4D89-8B13-F4C785A53669&device_platform=iphone&build_number=17805&vid=CA468973-B544-44F5-9163-A832328E4E9C&openudid=2d20f63eeb3ef810a3c6c0ac22827db69f28a7b3&device_type=iPhone6,2&app_version=1.7.8&device_id=7426456155&version_code=1.7.8&os_version=9.0.1&screen_width=640&aid=1128&mc_id=0&mas=005a90acb44e3ce942f732b090b4cab060947eeb6cc6a588e2e6a7&as=a1756dd92bea1ce1612614&ts=1553060267";
        getType(typeUrl, url);
        System.out.println("共获取" + counts + "条数据");
    }

    /**
     * 爬取抖音音乐分类
     * @param typeUrl   歌曲分类URL
     * @param url       歌曲分类明细URL
     */
    private static void getType(String typeUrl, String url) {
        String str = MyUtil.sendPost(typeUrl, "");
        JSONObject jsonObject = JSONObject.parseObject(str);
        JSONArray mc_list = jsonObject.getJSONArray("mc_list");
        for (int i = 0; i < mc_list.size(); i++) {
            String mc_name = mc_list.getJSONObject(i).getString("mc_name");
            String mc_id = mc_list.getJSONObject(i).getString("mc_id");
            System.out.println("====================================" + mc_name + "====================================");
            //记录 总条数
            getMusics(mc_id, url, 0);
        }
    }

    /**
     * 爬取抖音音乐
     *
     * @param mc_id  分类ID
     * @param url    url
     * @param cursor 页码
     * @return 返回记录数
     */
    private static void getMusics(String mc_id, String url, Integer cursor) {
        url = url.replace("mc_id=0", "mc_id=" + mc_id);
        String str = MyUtil.sendPost(url, "");
        JSONObject jsonObject = JSONObject.parseObject(str);
        JSONArray music_list = jsonObject.getJSONArray("music_list");
        for (int i = 0; i < music_list.size(); i++) {
            JSONObject jo = music_list.getJSONObject(i);
            String title = jo.getString("title");
            String author = jo.getString("author");
            JSONObject play_url = jo.getJSONObject("play_url");

            String uri = play_url.getString("uri");
            try {
                //获取文件名并保存
                String fileName = uri.substring(uri.lastIndexOf("/")+1);
                if (!fileName.contains(".mp3")){
                    fileName=fileName+".mp3";
                }
                //MyUtil.downloadFile(uri, "D:\\music\\"+fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(title + "-" + author + "-" + uri);
            counts = counts + 1;
            break;
        }
//        if (music_list.size() >= 30) {
//            String newURL = url.replace("cursor=" + cursor, "cursor=" + (cursor + 30));
//            cursor = cursor + 30;
//            getMusics(mc_id, newURL, cursor);
//        }

    }








}