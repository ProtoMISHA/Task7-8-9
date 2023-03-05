package com.example.task789.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "meter_result")
public class MeterResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "current_reading")
    private BigDecimal currentReading;

    @Column(name = "reading_date")
    private LocalDateTime readingDate;

    @ManyToOne
    @JoinColumn(name = "meter_id", referencedColumnName = "id")
    private Meter meter;
}
