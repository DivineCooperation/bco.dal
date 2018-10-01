package org.openbase.bco.dal.lib.layer.service.collection;

/*
 * #%L
 * BCO DAL Library
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
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
import java.util.concurrent.Future;

import org.openbase.bco.dal.lib.layer.service.operation.BlindStateOperationService;
import org.openbase.bco.dal.lib.layer.service.operation.PowerStateOperationService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import rst.domotic.action.ActionDescriptionType.ActionDescription;
import rst.domotic.state.PowerStateType.PowerState;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

/**
 *
 * * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public interface PowerStateOperationServiceCollection extends PowerStateOperationService {

    public Future<ActionDescription> setPowerState(final PowerState powerState, final UnitType unitType) throws CouldNotPerformException;

    default public Future<ActionDescription> setPowerState(final PowerState.State powerState, final UnitType unitType) throws CouldNotPerformException {
        return setPowerState(PowerState.newBuilder().setValue(powerState).build(), unitType);
    }

    /**
     * Returns on if at least one of the power services is on and else off.
     *
     * @return
     * @throws NotAvailableException
     */
    @Override
    default PowerState getPowerState() throws NotAvailableException {
        return PowerStateOperationService.super.getPowerState();
    }

    /**
     * Returns on if at least one of the powerServices with given unitType is on
     * and else off.
     *
     * @param unitType
     * @return
     * @throws NotAvailableException
     */
    PowerState getPowerState(final UnitType unitType) throws NotAvailableException;
}
