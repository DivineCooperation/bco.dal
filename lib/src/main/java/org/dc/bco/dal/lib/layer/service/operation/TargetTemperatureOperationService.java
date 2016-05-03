/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.dal.lib.layer.service.operation;

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

import java.util.concurrent.Future;
import org.dc.bco.dal.lib.layer.service.provider.TargetTemperatureProviderService;
import org.dc.jul.exception.CouldNotPerformException;

/**
 *
 * @author mpohling
 */
public interface TargetTemperatureOperationService extends OperationService, TargetTemperatureProviderService {

    public Future<Void> setTargetTemperature(final Double value) throws CouldNotPerformException;

//    public class SetTargetTemperatureCallback extends EventCallback {
//
//        private static final Logger logger = LoggerFactory.getLogger(SetTargetTemperatureCallback.class);
//
//        private final TargetTemperatureService service;
//
//        public SetTargetTemperatureCallback(final TargetTemperatureService service) {
//            this.service = service;
//        }
//
//        @Override
//        public Event invoke(final Event request) throws UserCodeException {
//            try {
//                service.setTargetTemperature(((Double) request.getData()));
//                return new Event(Void.class);
//            } catch (Exception ex) {
//                throw ExceptionPrinter.printHistoryAndReturnThrowable(new UserCodeException(new InvocationFailedException(this, service, ex)), logger);
//            }
//        }
//    }
}
