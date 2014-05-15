package org.guvnor.common.services.shared.events;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PublishMessagesEvent extends PublishBaseEvent {

    public PublishMessagesEvent() {
        //needed for marshalling.
    }

}
