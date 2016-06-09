package org.dc.bco.dal.lib.layer.service.collection;

/*
 * #%L
 * DAL Library
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
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
import org.dc.bco.dal.lib.layer.service.operation.ShutterOperationService;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.NotAvailableException;
import org.dc.jul.schedule.GlobalExecutionService;
import rst.homeautomation.state.ShutterStateType.ShutterState;
import static rst.homeautomation.state.ShutterStateType.ShutterState.State.DOWN;
import static rst.homeautomation.state.ShutterStateType.ShutterState.State.STOP;
import static rst.homeautomation.state.ShutterStateType.ShutterState.State.UP;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public interface ShutterStateOperationServiceCollection extends ShutterOperationService {

    @Override
    default public Future<Void> setShutter(ShutterState state) throws CouldNotPerformException {
        return GlobalExecutionService.allOf((ShutterOperationService input) -> input.setShutter(state), getShutterStateOperationServices());
    }

    /**
     * Returns up if all shutter services are up and else the from up differing
     * state of the first shutter.
     *
     * @return
     * @throws NotAvailableException
     */
    @Override
    default public ShutterState getShutter() throws NotAvailableException {
        try {
            for (ShutterOperationService service : getShutterStateOperationServices()) {
                switch (service.getShutter().getValue()) {
                    case DOWN:
                        return ShutterState.newBuilder().setValue(ShutterState.State.DOWN).build();
                    case STOP:
                        return ShutterState.newBuilder().setValue(ShutterState.State.STOP).build();
                    case UP:
                    default:
                }
            }
            return ShutterState.newBuilder().setValue(ShutterState.State.UP).build();
        } catch (CouldNotPerformException ex) {
            throw new NotAvailableException("ShutterState", ex);
        }
    }

    public Collection<ShutterOperationService> getShutterStateOperationServices() throws CouldNotPerformException;
}