package com.nithin.razorpay.payment.simulator;

import com.nithin.razorpay.common.enums.ChaosMode;
import com.nithin.razorpay.common.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("payment.simulator")
@Getter
@Setter
public class SimulatorConfig {

    private Integer pollIntervalMs = 2000;
    private ChaosMode chaosMode = ChaosMode.NORMAL;
    private Map<PaymentMethod,MethodSimulatorConfig> methods = new HashMap<>();

    public MethodSimulatorConfig configFor(PaymentMethod method) {
        return methods.getOrDefault(method,new MethodSimulatorConfig());
    }

    @Getter
    @Setter
    public static class MethodSimulatorConfig{
        private Integer minDelaySeconds = 1;
        private Integer maxDelaySeconds = 5;
        private Integer successRate = 80;
    }
}
