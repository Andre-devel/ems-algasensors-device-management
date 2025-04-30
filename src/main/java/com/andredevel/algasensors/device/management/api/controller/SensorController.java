package com.andredevel.algasensors.device.management.api.controller;

import com.andredevel.algasensors.device.management.api.model.Sensor;
import com.andredevel.algasensors.device.management.api.model.SensorInput;
import com.andredevel.algasensors.device.management.common.IdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sensor create(@RequestBody SensorInput input) {

        return Sensor.builder()
                .id(IdGenerator.generateTSID())
                .name(input.getName())
                .ip(input.getIp())
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .enabled(false)
                .build();
    }
}
