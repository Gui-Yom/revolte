package dev.plus1.revolte;

import dev.plus1.messenger.Messenger;
import dev.plus1.messenger.sendapi.GenericTemplateMessage;
import dev.plus1.revolte.data.NewGame;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class GameWebSocket {

    public static final char SEP = '|';
    private static final Logger log = LoggerFactory.getLogger(GameWebSocket.class);
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, Session> getSessions() {
        return sessions;
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        if (!session.getUpgradeRequest().getParameterMap().containsKey("viewerId")) {
            log.error("Bad websocket upgrade request !");
            session.close(StatusCode.PROTOCOL, "Must specify query parameter : 'viewerId");
            return;
        }
        log.info("Websocket client connected : {} + {}",
                session.getRemoteAddress(),
                getSenderId(session));
        sessions.put(session.getUpgradeRequest().getParameterMap().get("viewerId").get(0), session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        log.info("Websocket client closed : {}", session.getRemoteAddress());
        sessions.remove(session.getUpgradeRequest().getParameterMap().get("viewerId").get(0));
    }

    /**
     * Frame are '|' separated values
     * type : e for event
     * r for request
     *
     * @param session
     * @param message
     * @throws IOException
     */
    @OnWebSocketMessage
    public void messageText(Session session, String message) throws IOException {
        String[] params = message.split("\\" + SEP);
        log.info("Received message : {}", Arrays.toString(params));
        switch (params[1]) {
            case "gameExists?":
                sendResponse(session, params[0], App.getGames().containsKey(params[2]) ? "true" : "false");
                break;
            case "joinGame!":
                Revolte game = App.getGames().get(params[2]);
                if (game != null) {
                    game.addPlayer(new Player(session.getUpgradeRequest().getParameterMap().get("viewerId").get(0)));
                    for (String k : sessions.keySet()) {
                        if (game.getPlayers().containsKey(k))
                            sendEvent(k, "playerJoin!", k);
                    }
                    sendResponse(session, params[0], "ok");
                } else
                    sendResponse(session, params[0], "error", "no_game_with_this_id");
                break;
            case "gameCreate!":
                NewGame data = App.gson.fromJson(params[2], NewGame.class);
                if (App.getGames().containsKey(data.getThreadId())) {
                    sendResponse(session, params[0], "error", "game_already_exists");
                } else {
                    if (data.getDeveloperKey().equals("69420")) {
                        App.getGames().put(data.getThreadId(), new Revolte(data.getThreadId(), data.getPhasesDuration()));
                        Messenger.sendGenericTemplateMessage(
                                new GenericTemplateMessage(
                                        data.getThreadId(),
                                        getSenderId(session) + " a créé une partie",
                                        "https://revolte.netlify.com/favicon.ico",
                                        "vous pouvez rejoindre la partie",
                                        "https://revolte.netlify.com",
                                        "Rejoindre",
                                        "tall"
                                )
                        );
                        sendResponse(session, params[0], "ok");
                    } else {
                        sendResponse(session, params[0], "error", "Mauvaise clé développeur");
                    }
                }
                break;
            case "gameInfo?":
                game = App.getGames().get(params[2]);
                if (game != null)
                    sendResponse(session, params[0], App.gson.toJson(game));
                else
                    sendResponse(session, params[0], "error", "no_game_with_this_id");
                break;
            case "dayVote!":
                game = App.getGames().get(params[2]);
                if (game != null) {
                    game.getPlayers().get(getSenderId(session)).setVote(params[3]);
                    if (game.getPlayers().containsKey(params[3]))
                        sendResponse(session, params[0], "ok");
                    else
                        sendResponse(session, params[0], "error", "no_player_with_this_id");
                } else
                    sendResponse(session, params[0], "error", "no_game_with_this_id");
                break;
        }
    }

    public void sendEvent(String viewerId, String... params) throws IOException {
        sendFrame(sessions.get(viewerId), "e", params);
    }

    public void broadcastEvent(String... params) throws IOException {
        for (Session s : sessions.values())
            sendFrame(s, "e", params);
    }

    private void sendResponse(Session session, String... params) throws IOException {
        sendFrame(session, "r", params);
    }

    private void sendFrame(Session session, String type, String... params) throws IOException {
        String msg = type + SEP + String.join(String.valueOf(SEP), params);
        log.info("Sending message : {}", msg);
        session.getRemote().sendString(msg);
    }

    private String getSenderId(Session session) {
        return session.getUpgradeRequest().getParameterMap().get("viewerId").get(0);
    }
}
