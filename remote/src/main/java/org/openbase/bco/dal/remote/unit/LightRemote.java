package org.openbase.bco.dal.remote.unit;

/*
 * #%L
 * BCO DAL Remote
 * %%
 * Copyright (C) 2014 - 2017 openbase.org
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
import org.openbase.bco.dal.lib.layer.unit.Light;
import org.openbase.bco.dal.remote.VoidFuture;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.rst.processing.ActionDescriptionProcessor;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.communicationpatterns.ResourceAllocationType.ResourceAllocation;
import rst.domotic.action.ActionAuthorityType.ActionAuthority;
import rst.domotic.action.ActionDescriptionType.ActionDescription;
import rst.domotic.state.PowerStateType.PowerState;
import rst.domotic.unit.dal.LightDataType.LightData;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LightRemote extends AbstractUnitRemote<LightData> implements Light {

    static {
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(LightData.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(PowerState.getDefaultInstance()));
    }

    public LightRemote() {
        super(LightData.class);
    }

    @Override
    protected void notifyDataUpdate(LightData data) throws CouldNotPerformException {
    }

    @Override
    public Future<Void> setPowerState(PowerState powerState) throws CouldNotPerformException {
        ActionDescription.Builder actionDescription = ActionDescriptionProcessor.getActionDescription(ActionAuthority.getDefaultInstance(), ResourceAllocation.Initiator.SYSTEM);
        try {
            return new VoidFuture(this.applyAction(updateActionDescription(actionDescription, powerState).build()));
        } catch (InterruptedException ex) {
            throw new CouldNotPerformException("Interrupted while setting powerState.", ex);
        }
    }

    @Override
    public PowerState getPowerState() throws NotAvailableException {
        try {
            return this.getData().getPowerState();
        } catch (CouldNotPerformException ex) {
            throw new NotAvailableException("PowerState", ex);
        }
    }
}
