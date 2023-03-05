package com.example.task789.service;

import com.example.task789.model.dto.MeterData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface ServiceMeter {

    HashMap<String, HashMap<Long, List<BigDecimal>>> getReportData(LocalDate localDate);

    void saveResult(MeterData meterData);

}
