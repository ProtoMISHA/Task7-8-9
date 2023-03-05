package com.example.task789.service;

import com.example.task789.dao.MeterResultDAO;
import com.example.task789.model.GroupMeter;
import com.example.task789.model.Meter;
import com.example.task789.model.MeterResult;
import com.example.task789.model.dto.MeterData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ServiceMeterJDBCImpl implements ServiceMeter {


    private final MeterResultDAO meterResultDAO;

    @Override
    public HashMap<String, HashMap<Long, List<BigDecimal>>> getReportData(LocalDate localDate) {
        return meterResultDAO.getMinAndMaxResultsByDateGroupByGroupMeterAndMeter(localDate);
    }

    @Override
    public void saveResult(MeterData meterData) {
        MeterResult meterResult = new MeterResult();

        meterResult.setId(meterData.getMeterId());
        meterResult.setReadingDate(meterData.getTimestamp());
        meterResult.setCurrentReading(meterData.getCurrentReading());

        Meter meter = new Meter();
        meter.setId(meterData.getMeterId());
        meter.setMeterType(meterData.getType());
        meterResult.setMeter(meter);

        GroupMeter meterGroup = new GroupMeter();
        meterGroup.setName(meterData.getMeterGroup());

        meter.setGroupMeter(meterGroup);

        meterResultDAO.saveResult(meterResult);

    }

}
