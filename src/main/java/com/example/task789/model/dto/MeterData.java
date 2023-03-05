package com.example.task789.model.dto;

import com.example.task789.model.MeterType;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MeterData {

    @NotNull
    private Long meterId;

    @NotNull
    private MeterType type;
    @NotBlank
    private String meterGroup;
    @NotNull
    private LocalDateTime timestamp;
    @DecimalMin("0.0")
    @NotNull
    private BigDecimal currentReading;

}
