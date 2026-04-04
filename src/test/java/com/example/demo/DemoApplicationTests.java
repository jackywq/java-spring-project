package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldReturnPersonList() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("张三"))
                .andExpect(jsonPath("$[1].department").value("产品部"));
    }

    @Test
    void shouldFilterAndSortPersonList() throws Exception {
        mockMvc.perform(get("/persons")
                        .param("department", "研发部")
                        .param("minAge", "25")
                        .param("sortBy", "age")
                        .param("order", "desc")
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("张三"));
    }

    @Test
    void shouldReturnPersonById() throws Exception {
        mockMvc.perform(get("/persons/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("李四"))
                .andExpect(jsonPath("$.department").value("产品部"));
    }

    @Test
    void shouldReturnDepartments() throws Exception {
        mockMvc.perform(get("/persons/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("产品部"))
                .andExpect(jsonPath("$[1]").value("研发部"))
                .andExpect(jsonPath("$[2]").value("运营部"));
    }

    @Test
    void shouldReturnStats() throws Exception {
        mockMvc.perform(get("/persons/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.averageAge").value(28.7))
                .andExpect(jsonPath("$.youngestAge").value(26))
                .andExpect(jsonPath("$.oldestAge").value(32))
                .andExpect(jsonPath("$.departmentCounts.研发部").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenPersonDoesNotExist() throws Exception {
        mockMvc.perform(get("/persons/99"))
                .andExpect(status().isNotFound());
    }

}
