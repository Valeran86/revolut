package com.revolut.payments.service;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    private Gson gson = new Gson();

    @Override
    public String render( Object model ) {
        return gson.toJson( model );
    }

    public <T> T unmarshal( String json, Class<T> classOfT ) {
        return gson.fromJson( json, classOfT );
    }
}
