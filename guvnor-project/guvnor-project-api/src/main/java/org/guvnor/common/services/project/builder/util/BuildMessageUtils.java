package org.guvnor.common.services.project.builder.util;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.shared.events.SystemMessage;

public class BuildMessageUtils {

    public static final String BUILD_SYSTEM_MESSAGE = "BuildSystem";

    public static SystemMessage convert(BuildMessage buildMessage) {

        SystemMessage systemMessage = new SystemMessage();

        systemMessage.setMessageType( BUILD_SYSTEM_MESSAGE );
        systemMessage.setId( buildMessage.getId() );
        systemMessage.setLevel( convert( buildMessage.getLevel() ) );
        systemMessage.setColumn( buildMessage.getColumn() );
        systemMessage.setLine( buildMessage.getLine() );
        systemMessage.setText( buildMessage.getText() );
        systemMessage.setPath( buildMessage.getPath() );
        return systemMessage;
    }

    public static SystemMessage.Level convert( BuildMessage.Level level ) {
        if ( level == null ) {
            return null;
        }
        if ( level == BuildMessage.Level.ERROR ) {
            return SystemMessage.Level.ERROR;
        }
        if ( level == BuildMessage.Level.WARNING ) {
            return SystemMessage.Level.WARNING;
        }
        if ( level == BuildMessage.Level.INFO ) {
            return SystemMessage.Level.INFO;
        }
        return null;
    }

}
