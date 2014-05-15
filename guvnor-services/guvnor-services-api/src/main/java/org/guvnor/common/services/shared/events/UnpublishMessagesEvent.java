package org.guvnor.common.services.shared.events;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class UnpublishMessagesEvent {

    /**
     * Filter parameter to establish that messagesToUnpublish for this session should be unpublished. (if null
     * all messagesToUnpublish will be deleted depending on userId and messageType).
     */
    private String sessionId;

    /**
     * Filter parameter to establish that messagesToUnpublish for this user should be unpublished.
     */
    private String userId;

    /**
     * Filter parameter to establish that messagesToUnpublish of this type should be unpublished.
     */
    private String messageType;

    private List<SystemMessage> messagesToUnpublish = new ArrayList<SystemMessage>();

    /**
     * If set to true, interested parties will always try to show the system errors console. If set to false
     * it's expected that the console is already opened.
     */
    private boolean showSystemConsole = true;

    public UnpublishMessagesEvent() {
        //needed for marshalling.
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

    public String getMessageType() {
        return messageType;
    }

    public boolean isShowSystemConsole() {
        return showSystemConsole;
    }

    public void setShowSystemConsole( boolean showSystemConsole ) {
        this.showSystemConsole = showSystemConsole;
    }

    public void setMessageType( String messageType ) {
        this.messageType = messageType;
    }

    public List<SystemMessage> getMessagesToUnpublish() {
        return messagesToUnpublish;
    }

    public void setMessagesToUnpublish( List<SystemMessage> messagesToUnpublish ) {
        this.messagesToUnpublish = messagesToUnpublish;
    }
}
