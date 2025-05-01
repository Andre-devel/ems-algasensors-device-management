package com.andredevel.algasensors.device.management.api.client;

import com.andredevel.algasensors.device.management.api.client.impl.RestClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClassConfig {
    
    @Bean
    public SensorMonitoringClient sensorMonitoringClient(RestClientFactory restClientFactory) {
        RestClient restClient = restClientFactory.temperatureMonitoringRestClient();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        
        return proxyFactory.createClient(SensorMonitoringClient.class);
    }
}
