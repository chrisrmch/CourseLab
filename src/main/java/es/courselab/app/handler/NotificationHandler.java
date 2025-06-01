package es.courselab.app.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class NotificationHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationHandler.class);

    /**
     * Se crea un Sink multicast que retiene los mensajes en un buffer y los distribuye
     * a todos los clientes suscritos. Cada cliente conectará a este handler y consumirá
     * desde sink.asFlux().
     */
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("Nueva conexión WebSocket (reactiva): sessionId={}", session.getId());

        // 1) Flujo de mensajes de salida: convertimos cada String en WebSocketMessage de texto
        Flux<WebSocketMessage> outbound = sink.asFlux()
                .map(session::textMessage)
                // Opcional: cuando el cliente se desconecte, cancelamos suscripción automáticamente
                .doOnTerminate(() -> log.info("Cliente desconectado: sessionId={}", session.getId()));

        // 2) Si quieres procesar mensajes entrantes (p. ej. comandos desde el cliente),
        //    puedes encadenar session.receive() aquí. En este ejemplo solo haremos eco:
        Mono<Void> inbound = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(text -> log.info("Mensaje recibido de {}: {}", session.getId(), text))
                // Por ejemplo, podrías reenviar lo que llegue de un cliente al mismo sink:
                // .doOnNext(text -> sink.tryEmitNext("Echo from " + session.getId() + ": " + text))
                .then();

        // 3) Combina inbound (procesar mensajes entrantes) y outbound (enviar notificaciones)
        //    de modo que el handler no termine hasta que ambos flujos finalicen.
        return Mono.zip(inbound, session.send(outbound)).then();
    }

    /**
     * Este método puede invocarse desde cualquier servicio o controlador de tu aplicación
     * para enviar una notificación a todos los clientes conectados.
     */
    public void publish(String payload) {
        log.info("Broadcasting mensaje a {} clientes: {}",
                sink.currentSubscriberCount(), payload);
        // Intentamos emitir, en caso de error (por ejemplo, sin suscriptores) no bloqueamos
        sink.tryEmitNext(payload);
    }
}
