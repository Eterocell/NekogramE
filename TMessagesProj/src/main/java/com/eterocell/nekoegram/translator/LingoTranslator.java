package com.eterocell.nekoegram.translator;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.eterocell.nekoegram.Extra;

public class LingoTranslator extends BaseTranslator {

    private final List<String> targetLanguages = Arrays.asList("zh", "en", "ja", "ko", "es", "fr", "ru");

    private static final class InstanceHolder {
        private static final LingoTranslator instance = new LingoTranslator();
    }

    static LingoTranslator getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public List<String> getTargetLanguages() {
        return targetLanguages;
    }

    @Override
    protected Result translate(String query, String fl, String tl) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray source = new JSONArray();
        for (var s : query.split("\n")) {
            source.put(s);
        }
        jsonObject.put("source", source);
        jsonObject.put("trans_type", "auto2" + tl);
        jsonObject.put("request_id", String.valueOf(System.currentTimeMillis()));
        jsonObject.put("detect", true);
        String response = Http.url("https://api.interpreter.caiyunai.com/v1/translator")
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("X-Authorization", "token " + Extra.LINGO_TOKEN)
                .header("User-Agent", "okhttp/3.12.3")
                .data(jsonObject.toString())
                .request();
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        jsonObject = new JSONObject(response);
        if (!jsonObject.has("target") && jsonObject.has("error")) {
            throw new IOException(jsonObject.getString("error"));
        }
        JSONArray target = jsonObject.getJSONArray("target");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < target.length(); i++) {
            result.append(target.getString(i));
            if (i != target.length() - 1) {
                result.append("\n");
            }
        }
        return new Result(result.toString(), null);
    }
}
