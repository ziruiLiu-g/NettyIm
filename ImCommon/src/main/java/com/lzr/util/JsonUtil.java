package com.lzr.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/**
 * JsonUtil
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
public class JsonUtil
{

    // GsonBuilder
    static GsonBuilder gb = new GsonBuilder();

    private static final Gson gson;

    static
    {
        // dont need html escape
        gb.disableHtmlEscaping();
        gson = gb.create();
    }

    // Object -> Json, Json -> ByteArray
    public static byte[] object2JsonBytes(Object obj)
    {

        // Object -> Json

        String json = pojoToJson(obj);
        try
        {
            return json.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    // Gson  Object(pojo) -> Json
    public static String pojoToJson(Object obj)
    {
        String json = gson.toJson(obj);
        return json;
    }


    public static <T> T jsonBytes2Object(byte[] bytes, Class<T> tClass)
    {

        // pojo -> json
        try
        {
            String json = new String(bytes, "UTF-8");
            T t = jsonToPojo(json, tClass);
            return t;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    // Gson  json -> pojo
    public static <T> T jsonToPojo(String json, Class<T> tClass)
    {
        T t = gson.fromJson(json, tClass);
        return t;
    }

    // Fastjson json -> pojo
    public static <T> T jsonToPojo(String json, TypeReference<T> type)
    {
        T t = JSON.parseObject(json, type);
        return t;
    }


    // Gson json -> pojo
    public static <T> T jsonToPojo(String json, Type type)
    {
        T t = gson.fromJson(json, type);
        return t;
    }


}
