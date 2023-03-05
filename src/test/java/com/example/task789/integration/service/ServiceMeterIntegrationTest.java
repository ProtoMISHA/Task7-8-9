package com.example.task789.integration.service;


import com.example.task789.model.GroupMeter;
import com.example.task789.model.Meter;
import com.example.task789.model.MeterResult;
import com.example.task789.model.dto.MeterData;
import com.example.task789.service.ServiceMeter;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@ActiveProfiles("test")
public class ServiceMeterIntegrationTest {

    private static final String deleteAllMeterResult = "DELETE FROM meter_result";

    private static final String insertMeterResult = "INSERT INTO meter_result(meter_id, current_reading, reading_date) VALUES(?,?,?)";

    private static final String selectMeterResultFromDB = "SELECT id, meter_id, current_reading, reading_date " +
            " FROM meter_result WHERE meter_id = ? AND current_reading = ? AND reading_date = ?";

    @Autowired
    private ServiceMeter serviceMeter;

    @Autowired
    private HikariDataSource hikariDataSource;

    @BeforeEach
    public void prepareDB() {
        deleteAllResultMeters();
        addTwoResultMeters();

    }


    @Test
    public void getReportData_shouldOK() {
        List<MeterResult> meterResults = getMeterResults();

        MeterResult firstResult = meterResults.get(0);
        MeterResult secondResult = meterResults.get(1);


        HashMap<String, HashMap<Long, List<BigDecimal>>> groupByMeterGroup = serviceMeter.getReportData(firstResult.getReadingDate().toLocalDate());

        HashMap<Long, List<BigDecimal>> resultGroupByMeter = groupByMeterGroup.get(firstResult.getMeter().getGroupMeter().getName());

        List<BigDecimal> bigDecimals = resultGroupByMeter.get(firstResult.getMeter().getId());

        assertThat(groupByMeterGroup.size(), is(1));

        assertThat(resultGroupByMeter.size(), is(1));

        assertThat(bigDecimals.get(0), is(firstResult.getCurrentReading()));

        assertThat(bigDecimals.get(1), is(secondResult.getCurrentReading()));


    }


    @Test
    public void saveMeterResult_shouldOK() {
        MeterResult meterResult = getMeterResults().get(0);

        MeterData meterData = new MeterData();

        meterData.setMeterId(meterResult.getMeter().getId());
        meterData.setType(meterResult.getMeter().getMeterType());
        meterData.setCurrentReading(meterResult.getCurrentReading());
        meterData.setTimestamp(meterResult.getReadingDate());
        meterData.setMeterGroup(meterResult.getMeter().getGroupMeter().getName());

        serviceMeter.saveResult(meterData);

        assertThat(true, is(isExistInDBCheckWithoutID(meterResult)));

    }


    @SneakyThrows
    private void deleteAllResultMeters() {
        Connection connection = hikariDataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteAllMeterResult)) {
            preparedStatement.execute();
        }
    }

    @SneakyThrows
    private void addTwoResultMeters() {
        Connection connection = hikariDataSource.getConnection();
        List<MeterResult> meterResults = getMeterResults();


        try (PreparedStatement preparedStatement = connection.prepareStatement(insertMeterResult)) {
            preparedStatement.setLong(1, meterResults.get(0).getMeter().getId());
            preparedStatement.setBigDecimal(2, meterResults.get(0).getCurrentReading());
            preparedStatement.setObject(3, meterResults.get(0).getReadingDate());

            preparedStatement.addBatch();

            preparedStatement.setLong(1, meterResults.get(1).getMeter().getId());
            preparedStatement.setBigDecimal(2, meterResults.get(1).getCurrentReading());
            preparedStatement.setObject(3, meterResults.get(1).getReadingDate());

            preparedStatement.addBatch();

            preparedStatement.executeBatch();

        }
    }

    @SneakyThrows
    private boolean isExistInDBCheckWithoutID(MeterResult meterResult) {

        Connection connection = hikariDataSource.getConnection();


        try (PreparedStatement preparedStatement = connection.prepareStatement(selectMeterResultFromDB)) {
            preparedStatement.setLong(1, meterResult.getMeter().getId());
            preparedStatement.setBigDecimal(2, meterResult.getCurrentReading());
            preparedStatement.setObject(3, meterResult.getReadingDate());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    private List<MeterResult> getMeterResults() {
        GroupMeter groupMeter = new GroupMeter();
        groupMeter.setName("room1");
        Meter meter = new Meter();
        meter.setGroupMeter(groupMeter);
        meter.setId(1L);

        MeterResult meterResult = new MeterResult();
        meterResult.setMeter(meter);
        meterResult.setCurrentReading(BigDecimal.valueOf(200));
        meterResult.setReadingDate(LocalDateTime.of(2023, 03, 10, 10, 10, 10));

        MeterResult meterResult2 = new MeterResult();
        meterResult2.setMeter(meter);
        meterResult2.setCurrentReading(BigDecimal.valueOf(430));
        meterResult2.setReadingDate(LocalDateTime.of(2023, 03, 30, 10, 10, 10));


        return List.of(meterResult, meterResult2);
    }

}
