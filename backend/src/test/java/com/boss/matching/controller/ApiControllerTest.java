package com.boss.matching.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.data.provider=memory",
        "spring.sql.init.mode=never"
})
@AutoConfigureMockMvc
class ApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void merchantHappyPathApisWork() throws Exception {
        String merchantToken = register("13900001000", "MERCHANT");

        mockMvc.perform(post("/api/merchant/profile")
                        .header("Authorization", bearer(merchantToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":100,\"name\":\"测试商家\",\"industry\":\"美妆\",\"description\":\"测试简介\",\"contact\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", equalTo("测试商家")));

        mockMvc.perform(post("/api/products")
                        .header("Authorization", bearer(merchantToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"merchantId\":1,\"name\":\"接口测试产品\",\"type\":\"美妆\",\"description\":\"测试产品\",\"goal\":\"曝光\",\"budgetMin\":1000,\"budgetMax\":3000,\"platform\":\"小红书\",\"fansMin\":10000,\"fansMax\":100000,\"cooperationType\":\"种草\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", equalTo("接口测试产品")));

        mockMvc.perform(get("/api/influencers").param("category", "美妆"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(post("/api/unlock/influencer")
                        .header("Authorization", bearer(merchantToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"merchantId\":1,\"influencerId\":1,\"productId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.status", equalTo("PAID")));
    }

    @Test
    void protectedMerchantApisRejectWrongRole() throws Exception {
        String influencerToken = register("13900001001", "INFLUENCER");

        mockMvc.perform(post("/api/products")
                        .header("Authorization", bearer(influencerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"merchantId\":1,\"name\":\"越权产品\",\"type\":\"美妆\",\"description\":\"测试产品\",\"goal\":\"曝光\",\"budgetMin\":1000,\"budgetMax\":3000,\"platform\":\"小红书\",\"fansMin\":10000,\"fansMax\":100000,\"cooperationType\":\"种草\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedApisRequireToken() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"merchantId\":1,\"name\":\"未登录产品\",\"type\":\"美妆\",\"description\":\"测试产品\",\"goal\":\"曝光\",\"budgetMin\":1000,\"budgetMax\":3000,\"platform\":\"小红书\",\"fansMin\":10000,\"fansMax\":100000,\"cooperationType\":\"种草\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void influencerFavoriteApisUseAuthenticatedAccount() throws Exception {
        String influencerToken = register("13900001002", "INFLUENCER");

        mockMvc.perform(post("/api/influencer/favorites/1")
                        .header("Authorization", bearer(influencerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited", equalTo(true)));

        mockMvc.perform(get("/api/influencer/favorites")
                        .header("Authorization", bearer(influencerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", equalTo(1)))
                .andExpect(jsonPath("$.items[0].id", equalTo(1)));

        mockMvc.perform(delete("/api/influencer/favorites/1")
                        .header("Authorization", bearer(influencerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited", equalTo(false)));
    }

    private String register(String phone, String role) throws Exception {
        String body = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + phone + "\",\"code\":\"123456\",\"role\":\"" + role + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.role", equalTo(role)))
                .andExpect(jsonPath("$.token").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode payload = objectMapper.readTree(body);
        return payload.get("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
