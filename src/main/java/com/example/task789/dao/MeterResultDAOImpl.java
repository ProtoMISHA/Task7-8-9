package com.example.task789.dao;

import com.example.task789.model.MeterResult;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MeterResultDAOImpl implements MeterResultDAO {


    private final HikariDataSource hikariDataSource;

    private static final String getMinMaxResultString = "SELECT met.id, gr.name, met.meter_type, " +
            " MAX(mr.current_reading) AS mx, MIN(mr.current_reading) AS mn" +
            " FROM meter_result mr INNER JOIN meter met ON(met.id = mr.meter_id)" +
            " INNER JOIN group_meter gr ON(gr.id = met.group_id) " +
            " WHERE YEAR(mr.reading_date) = YEAR(?) AND MONTH(mr.reading_date) = MONTH(?)"
            + " GROUP BY gr.name, met.id, met.meter_type";

    private static final String getMeterById = "SELECT meter_type, group_id FROM meter WHERE id = ?";

    private static final String getMeterGroupById = "SELECT name FROM group_meter WHERE id = ?";

    private static final String isExistMeter = "SELECT met.id FROM meter met INNER JOIN group_meter grm ON(met.group_id = grm.id) " +
            "WHERE met.id = ? AND grm.name = ?";


    private static final String saveResultMeter = "INSERT INTO meter_result(meter_id, current_reading, reading_date) VALUES(?,?,?)";


    @Override
    public HashMap<String, HashMap<Long, List<BigDecimal>>> getMinAndMaxResultsByDateGroupByGroupMeterAndMeter(LocalDate localDate) {
        Connection connection = null;
        PreparedStatement getAllResultByDatePS = null;
        try {
            connection = hikariDataSource.getConnection();
            connection.setAutoCommit(false);

            getAllResultByDatePS = connection.prepareStatement(getMinMaxResultString);
            getAllResultByDatePS.setObject(1, localDate);
            getAllResultByDatePS.setObject(2, localDate);

            ResultSet resultSet = getAllResultByDatePS.executeQuery();

            HashMap<String, HashMap<Long, List<BigDecimal>>> mainHashMap = new HashMap<>();

            HashMap<Long, List<BigDecimal>> metrHashMap = new HashMap<>();

            List<BigDecimal> minMaxList;
            while (resultSet.next()) {
                String groupName = resultSet.getString("gr.name");
                Long meterId = resultSet.getLong("met.id");

                if (!mainHashMap.containsKey(groupName)) {
                    mainHashMap.put(groupName, new HashMap<Long, List<BigDecimal>>());
                }

                metrHashMap = mainHashMap.get(groupName);
                if (!metrHashMap.containsKey(meterId)) {
                    metrHashMap.put(meterId, new ArrayList<>());
                }

                minMaxList = metrHashMap.get(meterId);

                //min result has a 0 index in list
                minMaxList.add(resultSet.getBigDecimal("mn"));


                //max result has a 1 index in list
                minMaxList.add(resultSet.getBigDecimal("mx"));

            }

            connection.commit();

            return mainHashMap;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (getAllResultByDatePS != null) {
                    getAllResultByDatePS.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


    }


    @Override
    public void saveResult(MeterResult meterResult) {
        Connection connection = null;
        PreparedStatement preparedStatementSaveMeterResult = null;
        PreparedStatement preparedStatementIsExistSaveMeterResult = null;
        try {
            connection = hikariDataSource.getConnection();

            connection.setAutoCommit(false);

            preparedStatementSaveMeterResult = connection.prepareStatement(saveResultMeter);

            preparedStatementIsExistSaveMeterResult = connection.prepareStatement(isExistMeter);
            preparedStatementIsExistSaveMeterResult.setLong(1, meterResult.getMeter().getId());
            preparedStatementIsExistSaveMeterResult.setString(2, meterResult.getMeter().getGroupMeter().getName());

            ResultSet resultSet = preparedStatementIsExistSaveMeterResult.executeQuery();

            if (resultSet.next()) {
                preparedStatementSaveMeterResult.setLong(1, meterResult.getMeter().getId());
                preparedStatementSaveMeterResult.setBigDecimal(2, meterResult.getCurrentReading());
                preparedStatementSaveMeterResult.setObject(3, meterResult.getReadingDate());
                preparedStatementSaveMeterResult.executeUpdate();

                connection.commit();
            } else {
                throw new RuntimeException("Meter with this id and group name doesn't exist");
            }

        } catch (SQLException sqlException) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                throw new RuntimeException(sqlException);
            }

        } finally {
            if (preparedStatementIsExistSaveMeterResult != null) {
                try {
                    preparedStatementIsExistSaveMeterResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (preparedStatementSaveMeterResult != null) {
                try {
                    preparedStatementSaveMeterResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }


}
