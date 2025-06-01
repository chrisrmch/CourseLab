package es.courselab.app.config;

import es.courselab.app.handler.NotificationHandler;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Configura un endpoint WebSocket reactivo en /notify.
 * No usa las anotaciones de Servlet (EnableWebSocket), sino la aproximación WebFlux
 * con SimpleUrlHandlerMapping + WebSocketHandlerAdapter.
 */
@Configuration
public class WebConfig {
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);
    @Autowired
    private NotificationHandler notificationHandler;

    @PostConstruct
    public void init() {
        log.info("🔧 WebConfig (WebFlux WebSocket) cargado correctamente");
    }

    /**
     * 1) HandlerMapping: mapea la ruta “/notify” a nuestro NotificationHandler.
     * Aquí indicamos que cualquier conexión WebSocket que llegue a “/notify”
     * sea atendida por el bean notificationHandler.
     */
    @Bean
    public SimpleUrlHandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> urlMap = new HashMap<>();
        // La URL “/notify” será manejada por el NotificationHandler
        urlMap.put("/notify", notificationHandler);

        // El orden debe ser menor que 1 para que tenga mayor precedencia que los controladores anotados
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(urlMap);
        mapping.setOrder(-1);
        return mapping;
    }

    /**
     * 2) WebSocketHandlerAdapter: necesario para habilitar el soporte de WebSocket en WebFlux.
     * Este bean se encarga de “adaptar” las conexiones HTTP arriba de WebFlux para que
     * entren en el flujo reactive de WebSocketHandler.
     */
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}