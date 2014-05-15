package org.guvnor.common.services.shared.events;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

public abstract class PublishBaseEvent {

    /**
     * If set the messagesToPublish should be published only for the given session, if null the messagesToPublish will be published in all
     * active sessions. (sessionId and userId can be used together).
     */
    private String sessionId;

    /**
     * If set the messagesToPublish should be published only for the given user, if userId == null and sessionId == null the message will be
     * published in all active sessions.
     */
    private String userId;

    private Place place = Place.END;

    private List<SystemMessage> messagesToPublish = new ArrayList<SystemMessage>();

    /**
     * If set to true, interested parties will always try to show the system errors console. If set to false
     * it's expected that the console is already opened.
     */
    private boolean showSystemConsole = true;

    protected PublishBaseEvent() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId( String userId ) {
        this.userId = userId;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace( Place place ) {
        this.place = place;
    }

    public boolean isShowSystemConsole() {
        return showSystemConsole;
    }

    public void setShowSystemConsole( boolean showSystemConsole ) {
        this.showSystemConsole = showSystemConsole;
    }

    public List<SystemMessage> getMessagesToPublish() {
        return messagesToPublish;
    }

    public void setMessagesToPublish( List<SystemMessage> messagesToPublish ) {
        this.messagesToPublish = messagesToPublish;
    }

    @Portable
    public static enum Place {
        TOP, END;
    }

}
