package com.example.task789.integration;

import com.example.task789.controller.MeterResultController;
import com.example.task789.model.MeterType;
import com.example.task789.model.dto.MeterData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class MeterResultControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;


    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private boolean isBeforeFirstTest = true;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        if (isBeforeFirstTest) {
            isBeforeFirstTest = false;
            objectMapper.registerModule(new JavaTimeModule());
        }
    }


    @Test
    public void postResult_shouldOK() throws Exception {

        mockMvc.perform(post(MeterResultController.PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getMeterData())))
                .andExpect(status().isOk());

    }

    @Test
    public void postResult_shouldBadRequest() throws Exception {
        MeterData meterData = getMeterData();
        meterData.setCurrentReading(BigDecimal.valueOf(-111));

        mockMvc.perform(post(MeterResultController.PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meterData)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getReport_shouldOk() throws Exception {

        mockMvc.perform(get(MeterResultController.PATH)
                        .param("date", LocalDate.now().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(status().isOk());

    }

    @Test
    public void getReport_shouldBadRequest() throws Exception {


        mockMvc.perform(get(MeterResultController.PATH)
                        .param("date", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(status().isBadRequest());
    }


    private MeterData getMeterData() {
        MeterData meterData = new MeterData();
        meterData.setMeterId(1L);
        meterData.setType(MeterType.ELM111);
        meterData.setMeterGroup("room1");
        meterData.setTimestamp(LocalDateTime.now());
        meterData.setCurrentReading(BigDecimal.valueOf(300));
        return meterData;
    }


}
