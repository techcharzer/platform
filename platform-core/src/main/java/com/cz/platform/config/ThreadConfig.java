//package com.cz.platform.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.task.TaskExecutor;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//
//import com.cz.platform.PlatformConstants;
//
//@Configuration
//public class ThreadConfig {
//
//	@Autowired
//	private ThreadPoolConfigurationProperties props;
//
//	@Bean(PlatformConstants.WORKER_POOL)
//	@Primary
//	public TaskExecutor threadPoolTaskExecutor() {
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setCorePoolSize(props.getWorkerThreadCorePoolSize());
//		executor.setMaxPoolSize(props.getWorkerThreadMaxPoolSize());
//		executor.setQueueCapacity(props.getWorkerThreadQueueCapacity());
//		executor.setThreadNamePrefix("worker_thread_");
//		executor.setWaitForTasksToCompleteOnShutdown(true);
//		executor.initialize();
//		return executor;
//	}
//
//	@Bean(PlatformConstants.SCHEDULER_POOL)
//	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
//		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
//		threadPoolTaskScheduler.setPoolSize(props.getSchedulerThreadPoolSize());
//		threadPoolTaskScheduler.setThreadNamePrefix("scheduler_thread_");
//		return threadPoolTaskScheduler;
//	}
//
//}
