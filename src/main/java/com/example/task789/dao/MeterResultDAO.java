package com.example.task789.dao;

import com.example.task789.model.MeterResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface MeterResultDAO {
    HashMap<String, HashMap<Long, List<BigDecimal>>> getMinAndMaxResultsByDateGroupByGroupMeterAndMeter(LocalDate localDate);

    void saveResult(MeterResult meterResult);

}
