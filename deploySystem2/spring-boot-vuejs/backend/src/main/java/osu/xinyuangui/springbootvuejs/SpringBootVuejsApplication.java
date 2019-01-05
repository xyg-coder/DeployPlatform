package osu.xinyuangui.springbootvuejs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import osu.xinyuangui.springbootvuejs.FileReadingWebsocket.FileReadingWebsocketHandler;

@SpringBootApplication
@EnableWebSocket
public class SpringBootVuejsApplication implements WebSocketConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootVuejsApplication.class, args);
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

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
	    webSocketHandlerRegistry.addHandler(fileReadingWebsocketHandler(), "/file-read").setAllowedOrigins("*");
	}

	@Bean
	public FileReadingWebsocketHandler fileReadingWebsocketHandler() {
		return new FileReadingWebsocketHandler();
	}
}

