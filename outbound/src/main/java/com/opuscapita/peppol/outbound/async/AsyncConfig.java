package com.opuscapita.peppol.outbound.async;

import org.jetbrains.annotations.NotNull;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author Sergejs.Roze
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    private final AsyncExceptionHandler asyncExceptionHandler;

    @Value("${peppol.outbound.consumers.default:2}")
    private int defaultConsumers;
    @Value("${peppol.outbound.consumers.max:4}")
    private int maxConsumers;

    @Autowired
    public AsyncConfig(@NotNull AsyncExceptionHandler asyncExceptionHandler) {
        this.asyncExceptionHandler = asyncExceptionHandler;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(defaultConsumers > 0 ? defaultConsumers : 2);
        executor.setMaxPoolSize(maxConsumers >= defaultConsumers ? maxConsumers : 4);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("outbound-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return asyncExceptionHandler::handle;
    }
}
