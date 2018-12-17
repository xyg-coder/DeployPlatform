package osu.xinyuan.deploySystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.*;
import osu.xinyuan.deploySystem.logWebsocket.LogWebsocketHandler;

import javax.jms.ConnectionFactory;

@SpringBootApplication
@EnableJms
@EnableWebSocketMessageBroker
@EnableAsync
@EnableWebSocket
public class DeploySystemApplication implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

	/**
	 * configure the jms
	 * @param connectionFactory
	 * @param configurer
	 * @return
	 */
	@Bean
	public JmsListenerContainerFactory<?> listenerContainerFactory(ConnectionFactory connectionFactory,
																   DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		return factory;
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.initialize();
		return executor;
	}

	/**
	 * configure the Websocket
	 * @param config
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/project-status-websocket").withSockJS();
	}

	public static void main(String[] args) {
		SpringApplication.run(DeploySystemApplication.class, args);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
		webSocketHandlerRegistry.addHandler(logHandler(), "/log");
	}

	@Bean
	public LogWebsocketHandler logHandler() {
		return new LogWebsocketHandler();
	}
}
