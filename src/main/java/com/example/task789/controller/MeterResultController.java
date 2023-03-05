package com.example.task789.controller;

import com.example.task789.model.dto.MeterData;
import com.example.task789.service.ServiceMeter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;


@Controller
@RequestMapping(MeterResultController.PATH)
@RequiredArgsConstructor
@Slf4j
public class MeterResultController {


    public static final String PATH = "/api/v1.0/example";

    private final ServiceMeter serviceMeter;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void saveResult(@RequestBody @Valid MeterData meterData) {
        serviceMeter.saveResult(meterData);
    }


    @GetMapping()
    public String getReportByDateTime(@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                      LocalDate date, Model model) {
        HashMap<String, HashMap<Long, List<BigDecimal>>> groupHashMap = serviceMeter.getReportData(date);

        List<Map.Entry<String, HashMap<Long, List<BigDecimal>>>> entryList = groupHashMap
                .entrySet()
                .stream()
                .sorted((firstEnt, secondEnt) -> firstEnt.getKey().compareTo(secondEnt.getKey()))
                .collect(Collectors.toList());

        Function<List<BigDecimal>, BigDecimal> listBigDecimalMap = (listBigDec) -> listBigDec.get(1).subtract(listBigDec.get(0));


        BinaryOperator<BigDecimal> bigDecimalReduce = BigDecimal::add;

        model.addAttribute("entryList", entryList);
        model.addAttribute("listToBigDecimal", listBigDecimalMap);
        model.addAttribute("bigDecimalReduce", bigDecimalReduce);

        return "report";
    }


}
