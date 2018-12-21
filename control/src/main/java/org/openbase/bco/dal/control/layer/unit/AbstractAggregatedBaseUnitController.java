package org.openbase.bco.dal.control.layer.unit;

/*-
 * #%L
 * BCO DAL Control
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import org.openbase.bco.authentication.lib.AuthPair;
import org.openbase.bco.authentication.lib.AuthenticationBaseData;
import org.openbase.bco.dal.lib.action.ActionDescriptionProcessor;
import org.openbase.bco.dal.lib.layer.service.ServiceRemote;
import org.openbase.bco.dal.lib.layer.unit.MultiUnit;
import org.openbase.bco.dal.lib.layer.unit.Unit;
import org.openbase.bco.dal.remote.layer.service.ServiceRemoteManager;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.protobuf.ClosableDataBuilder;
import org.openbase.jul.schedule.RecurrenceEventFilter;
import org.openbase.type.domotic.action.ActionDescriptionType.ActionDescription;
import org.openbase.type.domotic.action.SnapshotType.Snapshot;
import org.openbase.type.domotic.authentication.AuthenticatedValueType.AuthenticatedValue;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractAggregatedBaseUnitController<D extends AbstractMessage & Serializable, DB extends D.Builder<DB>> extends AbstractBaseUnitController<D, DB> implements MultiUnit<D> {

    private static final long MINIMAL_UPDATE_FREQUENCY = 10;

    private final RecurrenceEventFilter unitEventFilter;
    private final ServiceRemoteManager<D> serviceRemoteManager;

    public AbstractAggregatedBaseUnitController(final Class unitClass, final DB builder) throws InstantiationException {
        this(unitClass, builder, 0);
    }

    public AbstractAggregatedBaseUnitController(final Class unitClass, final DB builder, final long maxUpdateFrequency) throws InstantiationException {
        super(unitClass, builder);

        // filter updates through internal units by the provided frequency
        unitEventFilter = new RecurrenceEventFilter(Math.max(maxUpdateFrequency, MINIMAL_UPDATE_FREQUENCY)) {
            @Override
            public void relay() throws Exception {
                updateUnitData();
            }
        };
        // Create a service remote manager that triggers data updates
        this.serviceRemoteManager = new ServiceRemoteManager<D>(this) {
            @Override
            protected Set<ServiceType> getManagedServiceTypes() throws NotAvailableException {
                return AbstractAggregatedBaseUnitController.this.getSupportedServiceTypes();
            }

            @Override
            protected void notifyServiceUpdate(Unit source, Message data) throws NotAvailableException {
                try {
                    unitEventFilter.trigger();
                } catch (final CouldNotPerformException ex) {
                    logger.error("Could not trigger recurrence event filter for unit[" + getLabel() + "]");
                }
            }
        };
    }

    @Override
    public synchronized UnitConfig applyConfigUpdate(final UnitConfig config) throws CouldNotPerformException, InterruptedException {
        UnitConfig unitConfig = super.applyConfigUpdate(config);
        serviceRemoteManager.applyConfigUpdate(unitConfig.getLocationConfig().getUnitIdList());
        // if already active than update the current state.
        if (isActive()) {
            updateUnitData();
        }
        return unitConfig;
    }

    @Override
    public void activate() throws InterruptedException, CouldNotPerformException {
        if (isActive()) {
            logger.debug("Skip unit activation because it is already active...");
            return;
        }
        logger.debug("Activate unit [" + getLabel() + "]!");
        super.activate();
        serviceRemoteManager.activate();
        updateUnitData();
    }

    @Override
    public void deactivate() throws InterruptedException, CouldNotPerformException {
        logger.debug("Deactivate unit [" + getLabel() + "]!");
        super.deactivate();
        serviceRemoteManager.deactivate();
    }

    private void updateUnitData() throws InterruptedException {
        try (final ClosableDataBuilder<DB> dataBuilder = getDataBuilder(this)) {
            serviceRemoteManager.updateBuilderWithAvailableServiceStates(dataBuilder.getInternalBuilder(), getDataClass(), getSupportedServiceTypes());
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not update current status!", ex), logger, LogLevel.WARN);
        }
    }

    @Override
    public boolean isServiceAvailable(ServiceType serviceType) {
        return serviceRemoteManager.isServiceAvailable(serviceType);
    }

    @Override
    public Future<Snapshot> recordSnapshot() throws CouldNotPerformException, InterruptedException {
        return serviceRemoteManager.recordSnapshot();
    }

    @Override
    public Future<Snapshot> recordSnapshot(final UnitType unitType) throws CouldNotPerformException, InterruptedException {
        return serviceRemoteManager.recordSnapshot(unitType);
    }

    @Override
    public Future<Void> restoreSnapshot(final Snapshot snapshot) throws CouldNotPerformException, InterruptedException {
        return serviceRemoteManager.restoreSnapshot(snapshot);
    }

    @Override
    protected Future<Void> internalRestoreSnapshot(Snapshot snapshot, AuthenticationBaseData authenticationBaseData) throws CouldNotPerformException, InterruptedException {
        return serviceRemoteManager.restoreSnapshotAuthenticated(snapshot, authenticationBaseData);
    }

    @Override
    public Future<ActionDescription> applyAction(final ActionDescription actionDescription) throws CouldNotPerformException {
        if (!isServiceAggregated(actionDescription.getServiceStateDescription().getServiceType())) {
            return super.applyAction(actionDescription);
        }

        final ActionDescription.Builder actionDescriptionBuilder = actionDescription.toBuilder();
        ActionDescriptionProcessor.verifyActionDescription(actionDescriptionBuilder, this, true);
        return serviceRemoteManager.applyAction(actionDescriptionBuilder.build());
    }

    protected ActionDescription internalApplyActionAuthenticated(final AuthenticatedValue authenticatedValue, final ActionDescription.Builder actionDescriptionBuilder, final AuthenticationBaseData authenticationBaseData, final AuthPair authPair) throws InterruptedException, CouldNotPerformException, ExecutionException {
        if (isServiceAggregated(actionDescriptionBuilder.getServiceStateDescription().getServiceType())) {
            return super.internalApplyActionAuthenticated(authenticatedValue, actionDescriptionBuilder, authenticationBaseData, authPair);
        }

        final byte[] sessionKey = (authenticationBaseData != null) ? authenticationBaseData.getSessionKey() : null;
        serviceRemoteManager.applyActionAuthenticated(authenticatedValue, actionDescriptionBuilder, sessionKey).get();
        return actionDescriptionBuilder.build();
    }

    @Override
    public ServiceRemote getServiceRemote(final ServiceType serviceType) throws NotAvailableException {
        return serviceRemoteManager.getServiceRemote(serviceType);
    }
}