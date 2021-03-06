package org.guvnor.structure.backend.organizationalunit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.structure.backend.config.OrgUnit;
import org.guvnor.structure.backend.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationaUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.organizationalunit.OrganizationalUnitFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.rpc.SessionInfo;

@Service
@ApplicationScoped
public class OrganizationalUnitServiceImpl implements OrganizationalUnitService {

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private OrganizationalUnitFactory organizationalUnitFactory;

    @Inject
    private Event<NewOrganizationalUnitEvent> newOrganizationalUnitEvent;

    @Inject
    private Event<RemoveOrganizationalUnitEvent> removeOrganizationalUnitEvent;

    @Inject
    private Event<RepoAddedToOrganizationaUnitEvent> repoAddedToOrgUnitEvent;

    @Inject
    private Event<RepoRemovedFromOrganizationalUnitEvent> repoRemovedFromOrgUnitEvent;

    @Inject
    private Event<UpdatedOrganizationalUnitEvent> updatedOrganizationalUnitEvent;

    private Map<String, OrganizationalUnit> registeredOrganizationalUnits = new HashMap<String, OrganizationalUnit>();

    private
    @Inject SessionInfo sessionInfo;

    @PostConstruct
    public void loadOrganizationalUnits() {
        Collection<ConfigGroup> groups = configurationService.getConfiguration( ConfigType.ORGANIZATIONAL_UNIT );
        if ( groups != null ) {
            for ( ConfigGroup groupConfig : groups ) {
                OrganizationalUnit ou = organizationalUnitFactory.newOrganizationalUnit( groupConfig );
                registeredOrganizationalUnits.put( ou.getName(),
                                                   ou );
            }
        }
    }

    @Override
    public OrganizationalUnit getOrganizationalUnit( final String name ) {
        return registeredOrganizationalUnits.get( name );
    }

    @Override
    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        return new ArrayList<OrganizationalUnit>( registeredOrganizationalUnits.values() );
    }

    @Override
    public OrganizationalUnit createOrganizationalUnit( final String name,
                                                        final String owner ) {
        final ConfigGroup groupConfig = configurationFactory.newConfigGroup( ConfigType.ORGANIZATIONAL_UNIT,
                                                                             name,
                                                                             "" );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "owner",
                                                                       owner ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "repositories",
                                                                       new ArrayList<String>() ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "security:roles",
                                                                       new ArrayList<String>() ) );
        configurationService.addConfiguration( groupConfig );

        final OrganizationalUnit newOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( groupConfig );
        registeredOrganizationalUnits.put( newOrganizationalUnit.getName(),
                                           newOrganizationalUnit );

        newOrganizationalUnitEvent.fire( new NewOrganizationalUnitEvent( newOrganizationalUnit, sessionInfo ) );

        return newOrganizationalUnit;
    }

    @Override
    public OrganizationalUnit createOrganizationalUnit( final String name,
                                                        final String owner,
                                                        final Collection<Repository> repositories ) {
        final ConfigGroup groupConfig = configurationFactory.newConfigGroup( ConfigType.ORGANIZATIONAL_UNIT,
                                                                             name,
                                                                             "" );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "owner",
                                                                       owner ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "repositories",
                                                                       getRepositoryAliases( repositories ) ) );
        groupConfig.addConfigItem( configurationFactory.newConfigItem( "security:roles",
                                                                       new ArrayList<String>() ) );
        configurationService.addConfiguration( groupConfig );

        final OrganizationalUnit newOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( groupConfig );
        registeredOrganizationalUnits.put( newOrganizationalUnit.getName(),
                                           newOrganizationalUnit );

        newOrganizationalUnitEvent.fire( new NewOrganizationalUnitEvent( newOrganizationalUnit, sessionInfo ) );

        return newOrganizationalUnit;
    }

    private List<String> getRepositoryAliases( final Collection<Repository> repositories ) {
        final List<String> repositoryList = new ArrayList<String>();
        for ( Repository repo : repositories ) {
            repositoryList.add( repo.getAlias() );
        }
        return repositoryList;
    }

    @Override
    public void updateOrganizationalUnitOwner( final String name,
                                               final String owner ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( name );

        if ( thisGroupConfig != null ) {
            thisGroupConfig.setConfigItem( configurationFactory.newConfigItem( "owner",
                                                                               owner ) );
            configurationService.updateConfiguration( thisGroupConfig );

            final OrganizationalUnit updatedOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( thisGroupConfig );
            registeredOrganizationalUnits.put( updatedOrganizationalUnit.getName(),
                                               updatedOrganizationalUnit );

            updatedOrganizationalUnitEvent.fire( new UpdatedOrganizationalUnitEvent( updatedOrganizationalUnit, sessionInfo ) );

        } else {
            throw new IllegalArgumentException( "OrganizationalUnit " + name + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addRepository( final OrganizationalUnit organizationalUnit,
                               final Repository repository ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( organizationalUnit.getName() );

        if ( thisGroupConfig != null ) {
            ConfigItem<List> repositories = thisGroupConfig.getConfigItem( "repositories" );
            repositories.getValue().add( repository.getAlias() );

            configurationService.updateConfiguration( thisGroupConfig );

            final OrganizationalUnit updatedOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( thisGroupConfig );
            registeredOrganizationalUnits.put( updatedOrganizationalUnit.getName(),
                                               updatedOrganizationalUnit );

            repoAddedToOrgUnitEvent.fire(new RepoAddedToOrganizationaUnitEvent(organizationalUnit, repository, sessionInfo ));
        } else {
            throw new IllegalArgumentException( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void removeRepository( final OrganizationalUnit organizationalUnit,
                                  final Repository repository ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( organizationalUnit.getName() );

        if ( thisGroupConfig != null ) {
            final ConfigItem<List> repositories = thisGroupConfig.getConfigItem( "repositories" );
            repositories.getValue().remove( repository.getAlias() );

            configurationService.updateConfiguration( thisGroupConfig );

            final OrganizationalUnit updatedOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( thisGroupConfig );
            registeredOrganizationalUnits.put( updatedOrganizationalUnit.getName(),
                                               updatedOrganizationalUnit );

            repoRemovedFromOrgUnitEvent.fire(new RepoRemovedFromOrganizationalUnitEvent(organizationalUnit, repository, sessionInfo ));
        } else {
            throw new IllegalArgumentException( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addRole( final OrganizationalUnit organizationalUnit,
                         final String role ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( organizationalUnit.getName() );

        if ( thisGroupConfig != null ) {
            final ConfigItem<List> roles = thisGroupConfig.getConfigItem( "security:roles" );
            roles.getValue().add( role );

            configurationService.updateConfiguration( thisGroupConfig );

            final OrganizationalUnit updatedOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( thisGroupConfig );
            registeredOrganizationalUnits.put( updatedOrganizationalUnit.getName(),
                                               updatedOrganizationalUnit );

            updatedOrganizationalUnitEvent.fire( new UpdatedOrganizationalUnitEvent( updatedOrganizationalUnit, sessionInfo ) );

        } else {
            throw new IllegalArgumentException( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void removeRole( final OrganizationalUnit organizationalUnit,
                            final String role ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( organizationalUnit.getName() );

        if ( thisGroupConfig != null ) {
            final ConfigItem<List> roles = thisGroupConfig.getConfigItem( "security:roles" );
            roles.getValue().remove( role );

            configurationService.updateConfiguration( thisGroupConfig );

            final OrganizationalUnit updatedOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit( thisGroupConfig );
            registeredOrganizationalUnits.put( updatedOrganizationalUnit.getName(),
                                               updatedOrganizationalUnit );

            updatedOrganizationalUnitEvent.fire( new UpdatedOrganizationalUnitEvent( updatedOrganizationalUnit, sessionInfo ) );

        } else {
            throw new IllegalArgumentException( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
        }
    }

    protected ConfigGroup findGroupConfig( final String name ) {
        final Collection<ConfigGroup> groups = configurationService.getConfiguration( ConfigType.ORGANIZATIONAL_UNIT );
        if ( groups != null ) {
            for ( ConfigGroup groupConfig : groups ) {
                if ( groupConfig.getName().equals( name ) ) {
                    return groupConfig;
                }
            }
        }
        return null;
    }

    @Override
    public void removeOrganizationalUnit( String groupName ) {
        final ConfigGroup thisGroupConfig = findGroupConfig( groupName );

        if ( thisGroupConfig != null ) {
            configurationService.removeConfiguration( thisGroupConfig );
            final OrganizationalUnit ou = registeredOrganizationalUnits.remove( groupName );
            removeOrganizationalUnitEvent.fire( new RemoveOrganizationalUnitEvent( ou, sessionInfo ) );
        }

    }

    @Override
    public OrganizationalUnit getParentOrganizationalUnit( final Repository repository ) {
        for ( OrganizationalUnit organizationalUnit : registeredOrganizationalUnits.values() ) {
            if ( organizationalUnit.getRepositories() != null &&
                    organizationalUnit.getRepositories().contains( repository ) ) {
                return organizationalUnit;
            }
        }
        return null;
    }

    public void updateRegisteredOU( @Observes @OrgUnit SystemRepositoryChangedEvent changedEvent ) {
        registeredOrganizationalUnits.clear();
        loadOrganizationalUnits();
    }

    // refresh org unit in case repository changed otherwise it will have outdated information
    public void updateRegisteredOUonRepoChange( @Observes RepositoryUpdatedEvent changedEvent ) {
        registeredOrganizationalUnits.clear();
        loadOrganizationalUnits();
    }
}
