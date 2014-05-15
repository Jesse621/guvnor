package org.guvnor.common.services.shared.events;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PublishBatchMessagesEvent extends PublishBaseEvent {

    /**
     * If true, existing messages that full fills publication parameters will deleted prior to publication.
     */
    private boolean cleanExisting = false;

    /**
     * Makes sense only when clean is cleanExisting = true.
     */
    private String messageType;

    /**
     * List of messages to selective unpublish. This messages will allways be unpublished independent of cleanExisting value.
     */
    private List<SystemMessage> messagesToUnpublish = new ArrayList<SystemMessage>( );

    public PublishBatchMessagesEvent() {
        //needed for marshalling.
    }

    public boolean isCleanExisting() {
        return cleanExisting;
    }

    public void setCleanExisting( boolean cleanExisting ) {
        this.cleanExisting = cleanExisting;
    }

    public String getMessageType() {
        return messageType;
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
