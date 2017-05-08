package com.baofeng.mj.unity.launcher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 使用gson获取数据
 */
public class GsonUtil {
	private GsonUtil() {

	}

	public static <T> T getgson(String jsonString, Class<T> cls) {
		Gson gson = new Gson();
		return gson.fromJson(jsonString, cls);

	}

	public static <T> T getgsonList(String jsonString, TypeToken<T> ty) {
		Gson gson = new Gson();
		return gson.fromJson(jsonString, ty.getType());

	}

	public static List<String> getlist(String jsonString) {
		List<String> list = new ArrayList<String>();
		Gson gson = new Gson();
		list = gson.fromJson(jsonString, new TypeToken<List<String>>() {
		}.getType());
		return list;

	}

	public static List<Map<String, Object>> getgsonMap(String jsonString) {
		List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
		Gson gson = new Gson();
		map = gson.fromJson(jsonString,
				new TypeToken<List<Map<String, Object>>>() {
				}.getType());
		return map;
	}

	public static String toJsonData(Object cls) {
		String jsonData = null;
		Gson gson = new Gson();
		jsonData = gson.toJson(cls);
		return jsonData;
	}


}
