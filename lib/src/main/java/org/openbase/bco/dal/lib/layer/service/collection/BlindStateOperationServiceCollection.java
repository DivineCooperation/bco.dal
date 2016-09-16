package org.openbase.bco.dal.lib.layer.service.collection;

/*
 * #%L
 * DAL Library
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
import java.util.concurrent.Future;
import org.openbase.bco.dal.lib.layer.service.operation.BlindStateOperationService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.schedule.GlobalExecutionService;
import rst.homeautomation.state.BlindStateType.BlindState;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public interface BlindStateOperationServiceCollection extends BlindStateOperationService {

    @Override
    default public Future<Void> setBlindState(BlindState state) throws CouldNotPerformException {
        return GlobalExecutionService.allOf((BlindStateOperationService input) -> input.setBlindState(state), getBlindStateOperationServices());
    }

    /**
     * Returns up if all shutter services are up and else the from up differing
     * state of the first shutter.
     *
     * @return
     * @throws NotAvailableException
     */
    @Override
    default public BlindState getBlindState() throws NotAvailableException {
        try {
            for (BlindStateOperationService service : getBlindStateOperationServices()) {
                switch (service.getBlindState().getMovementState()) {
                    case DOWN:
                        return BlindState.newBuilder().setMovementState(BlindState.MovementState.DOWN).build();
                    case STOP:
                        return BlindState.newBuilder().setMovementState(BlindState.MovementState.STOP).build();
                    case UP:
                    default:
                }
            }
            return BlindState.newBuilder().setMovementState(BlindState.MovementState.UP).build();
        } catch (CouldNotPerformException ex) {
            throw new NotAvailableException("BlindState", ex);
        }
    }

    public Collection<BlindStateOperationService> getBlindStateOperationServices() throws CouldNotPerformException;
}