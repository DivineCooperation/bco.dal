package org.openbase.bco.dal.remote.service;

/*
 * #%L
 * DAL Remote
 * %%
 * Copyright (C) 2014 - 2016 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
import java.util.Collection;
import org.openbase.bco.dal.lib.layer.service.collection.SmokeStateProviderServiceCollection;
import org.openbase.bco.dal.lib.layer.service.provider.SmokeStateProviderService;
import org.openbase.bco.dal.remote.unit.UnitRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import rst.homeautomation.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import rst.homeautomation.state.SmokeStateType.SmokeState;
import rst.timing.TimestampType.Timestamp;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class SmokeStateServiceRemote extends AbstractServiceRemote<SmokeStateProviderService, SmokeState> implements SmokeStateProviderServiceCollection {

    public SmokeStateServiceRemote() {
        super(ServiceType.SMOKE_STATE_SERVICE);
    }

    @Override
    public Collection<SmokeStateProviderService> getSmokeStateProviderServices() {
        return getServices();
    }

    /**
     * {@inheritDoc}
     * Computes the average smoke level and the state as smoke if at least one underlying services detects smoke.
     * If no service detects smoke and at least one detects some smoke then that is set and else no smoke.
     *
     * @throws CouldNotPerformException {@inheritDoc}
     */
    @Override
    protected void computeServiceState() throws CouldNotPerformException {
        boolean someSmoke = false;
        SmokeState.State smokeValue = SmokeState.State.NO_SMOKE;
        Collection<SmokeStateProviderService> smokeStateProviderServices = getSmokeStateProviderServices();
        int amount = smokeStateProviderServices.size();
        double averageSmokeLevel = 0;
        for (SmokeStateProviderService provider : smokeStateProviderServices) {
            if (((UnitRemote) provider).isDataAvailable()) {
                amount--;
                continue;
            }

            SmokeState smokeState = provider.getSmokeState();
            if (smokeState.getValue() == SmokeState.State.SMOKE) {
                smokeValue = SmokeState.State.SMOKE;
                break;
            } else if (smokeState.getValue() == SmokeState.State.SOME_SMOKE) {
                someSmoke = true;
            }

            averageSmokeLevel += smokeState.getSmokeLevel();
        }

        if (someSmoke) {
            smokeValue = SmokeState.State.SOME_SMOKE;
        }
        averageSmokeLevel /= amount;

        serviceState = SmokeState.newBuilder().setValue(smokeValue).setSmokeLevel(averageSmokeLevel).setTimestamp(Timestamp.newBuilder().setTime(System.currentTimeMillis())).build();
    }

    @Override
    public SmokeState getSmokeState() throws NotAvailableException {
        return getServiceState();
    }
}
