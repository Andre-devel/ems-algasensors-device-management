package com.andredevel.algasensors.device.management.api.controller;

import com.andredevel.algasensors.device.management.api.client.SensorMonitoringClient;
import com.andredevel.algasensors.device.management.api.model.SensorInput;
import com.andredevel.algasensors.device.management.api.model.SensorOutput;
import com.andredevel.algasensors.device.management.common.IdGenerator;
import com.andredevel.algasensors.device.management.domain.model.Sensor;
import com.andredevel.algasensors.device.management.domain.model.SensorId;
import com.andredevel.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {
    
    private final SensorRepository sensorRepository;
    private final SensorMonitoringClient sensorMonitoringClient;
    
    @GetMapping
    public Page<SensorOutput> search(@PageableDefault Pageable pageable) {
        Page<Sensor> sensors = sensorRepository.findAll(pageable);
        return sensors.map(this::convertToModel);
    }
    
    @PutMapping("{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable TSID sensorId) {
        Sensor currentSensor = findSensorByID(sensorId);
        currentSensor.setEnabled(true);
        
        sensorRepository.save(currentSensor);

        sensorMonitoringClient.enableMonitoring(sensorId);
    }

    @DeleteMapping("{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable TSID sensorId) {
        Sensor currentSensor = findSensorByID(sensorId);
        currentSensor.setEnabled(false);

        sensorRepository.save(currentSensor);

        sensorMonitoringClient.disableMonitoring(sensorId);
    }

    @PutMapping("{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody SensorInput input, @PathVariable TSID sensorId) {
        Sensor currentSensor = findSensorByID(sensorId);

        Sensor sensor = Sensor.builder()
                .id(currentSensor.getId())
                .name(input.getName())
                .ip(input.getIp())
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .enabled(input.getEnabled())
                .build();
        
        sensorRepository.save(sensor);
    }
    
    @DeleteMapping("{sensorId}")
    public void delete(@PathVariable TSID sensorId) {
        Sensor currentSensor = findSensorByID(sensorId);
        
        sensorRepository.delete(currentSensor);
        
        sensorMonitoringClient.disableMonitoring(sensorId);
    }
    
    @GetMapping("{sensorId}")
    public SensorOutput getOne(@PathVariable TSID sensorId) {
        Sensor sensor = findSensorByID(sensorId);

        return convertToModel(sensor);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput input) {

        Sensor sensor = Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID()))
                .name(input.getName())
                .ip(input.getIp())
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .enabled(false)
                .build();
        
        sensor = sensorRepository.saveAndFlush(sensor);
        
        return convertToModel(sensor);
    }

    private SensorOutput convertToModel(Sensor sensor) {
        return SensorOutput.builder()
                .id(sensor.getId().getValue())
                .name(sensor.getName())
                .ip(sensor.getIp())
                .location(sensor.getLocation())
                .protocol(sensor.getProtocol())
                .model(sensor.getModel())
                .enabled(sensor.getEnabled())
                .build();
    }

    private Sensor findSensorByID(TSID sensorId) {
        return sensorRepository.findById(new SensorId(sensorId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
