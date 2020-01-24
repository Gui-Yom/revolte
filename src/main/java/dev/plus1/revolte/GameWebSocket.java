package dev.plus1.revolte;

import dev.plus1.revolte.data.NewGame;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class GameWebSocket {

    private static final Logger log = LoggerFactory.getLogger(GameWebSocket.class);
    private final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void connected(Session session) {
        log.info("Websocket client connected : {}", session.getRemoteAddress());
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        log.info("Websocket client closed : {}", session.getRemoteAddress());
        sessions.remove(session);
    }

    /**
     * Handle '|' separated values
     *
     * @param session
     * @param message
     * @throws IOException
     */
    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        String[] params = message.split("\\|");
        switch (params[0]) {
            case "game_exists?":
                session.getRemote().sendString(String.valueOf(App.getGames().containsKey(params[1])));
                break;
            case "game_has_player?":
                Revolte game = App.getGames().get(params[1]);
                if (game != null)
                    session.getRemote().sendString(String.valueOf(game.getPlayers().containsKey(params[2])));
                else
                    session.getRemote().sendString("error:no_game_with_this_id");
                break;
            case "game_join!":
                game = App.getGames().get(params[1]);
                if (game != null) {
                    game.addPlayer(new Player(params[2]));
                    session.getRemote().sendString("ok");
                } else
                    session.getRemote().sendString("error:no_game_with_this_id");
                break;
            case "game_create!":
                NewGame data = App.gson.fromJson(params[1], NewGame.class);
                if (App.getGames().containsKey(data.getThreadId())) {
                    session.getRemote().sendString("error:game_already_exists");
                } else {
                    App.getGames().put(params[1], new Revolte(data.getThreadId(), data.getPhasesDuration()));
                    session.getRemote().sendString("ok");
                }
                break;
        }
    }
}
