package org.forrest.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.*;

public class ValidatorService {
    private final String UA = "Keycloak Remote Validator SPI";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String url;
    private final OkHttpClient httpClient;
    private final String authorization;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ValidatorService(String url, String authorization) {
        this.httpClient = new OkHttpClient();
        this.url = url;
        this.authorization = authorization;
    }

    public ValidatorResponse call(String attr, String value) {
        Map<String, String> params = new HashMap<>() {{
            put("attr", attr);
            put("value", value);
        }};
        try (Response response = doQuery(params)) {
            if (response.body() == null) {
                return new ValidatorResponse(false, "The remote validator service is unreachable.");
            }
            return objectMapper.readValue(response.body().string(), ValidatorResponse.class);
        } catch (Exception e) {
            return new ValidatorResponse(false, e.getMessage());
        }
    }

    private Response doQuery(Map<String, String> params) throws IOException {
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            formBody.add(param.getKey(), param.getValue());
        }
        Request.Builder request = new Request.Builder().url(url).post(formBody.build());
        request.addHeader("User-Agent", UA);
        if (!"".equals(authorization) && authorization != null) {
            request.header("Authorization", authorization);
        }
        return httpClient.newCall(request.build()).execute();
    }
}
