package com.fernandez;

import static com.fernandez.HelloWorldController.MESSAGE_KEY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(HelloWorldController.class)
public class HelloWorldIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, String> result;
    
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void responseShouldContainHelloWorldKey() throws Exception {
        String response = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        result = objectMapper.readValue(response, Map.class);

        assertThat(result.containsKey(MESSAGE_KEY)).isTrue();
        assertThat(result.get(MESSAGE_KEY)).isEqualTo("Hello World!");
    }
}
