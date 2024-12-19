package com.integracao.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integracao.dto.Category;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;

public class ApiCategoryService {

    private final OkHttpClient client = new OkHttpClient();

    public List<Category> getCategories(String endpoint) throws IOException {

        Request request = new Request.Builder().url("http://localhost:8080/api/"+endpoint).build();

        try (Response response = client.newCall(request).execute()){

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(response.body().string(), new TypeReference<List<Category>>() {
            });
        } catch (InterruptedIOException e) {
            throw new RuntimeException(e);
        }
    }

}
