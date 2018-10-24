package com.revolut.moneytransfer.util;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    @Override
    public String render(Object model) {
        Gson gson = new Gson();

        return gson.toJson(model);
    }
}
