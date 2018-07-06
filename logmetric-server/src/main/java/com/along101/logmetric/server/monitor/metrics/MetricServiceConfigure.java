package com.along101.logmetric.server.monitor.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.ExportMetricReader;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.actuate.metrics.buffer.CounterBuffers;
import org.springframework.boot.actuate.metrics.buffer.GaugeBuffers;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yinzuolong on 2017/4/6.
 */
@Configuration
@AutoConfigureAfter(MetricRepositoryAutoConfiguration.class)
public class MetricServiceConfigure {

    @Bean
    @ExportMetricReader
    @Qualifier("InMemoryMetricRepository")
    @ConditionalOnMissingBean(name = "InMemoryMetricRepository")
    public InMemoryMetricRepository metricRepository(){
        return  new InMemoryMetricRepository();
    }

    @Bean
    public MetricService metricService(@Qualifier("InMemoryMetricRepository") InMemoryMetricRepository inMemoryMetricRepository) {
        return new LegacyMetricService(inMemoryMetricRepository);
    }

//    @Bean
//    @ConditionalOnMissingBean({MetricService.class,InMemoryMetricRepository.class})
//    public MetricService fastMetricService(CounterBuffers counters,
//                                        GaugeBuffers gauges) {
//        return new FastMetricService(counters, gauges);
//    }
//
//
//    @Bean
//    @ConditionalOnMissingBean({MetricService.class,CounterBuffers.class})
//    public MetricService legacyMetricService(InMemoryMetricRepository inMemoryMetricRepository) {
//        return new LegacyMetricService(inMemoryMetricRepository);
//    }

}
