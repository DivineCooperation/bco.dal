package org.openbase.bco.dal.control.layer.unit;

/*
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
import java.util.concurrent.Future;
import org.openbase.bco.dal.lib.layer.service.operation.TargetTemperatureStateOperationService;
import org.openbase.bco.dal.lib.layer.unit.TemperatureController;
import org.openbase.bco.dal.lib.layer.unit.UnitHost;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.domotic.action.ActionDescriptionType.ActionDescription;
import rst.domotic.state.TemperatureStateType.TemperatureState;
import rst.domotic.unit.dal.TemperatureControllerDataType.TemperatureControllerData;

import static rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType.TARGET_TEMPERATURE_STATE_SERVICE;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class TemperatureControllerController extends AbstractDALUnitController<TemperatureControllerData, TemperatureControllerData.Builder> implements TemperatureController {
    
    static {
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(TemperatureControllerData.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(TemperatureState.getDefaultInstance()));
    }
    
    private TargetTemperatureStateOperationService targetTemperatureStateService;
    
    public TemperatureControllerController(final UnitHost unitHost, final TemperatureControllerData.Builder builder) throws InstantiationException {
        super(TemperatureControllerController.class, unitHost, builder);
    }

    @Override
    public Future<ActionDescription> setTargetTemperatureState(final TemperatureState state) throws CouldNotPerformException {
        return applyUnauthorizedAction(state, TARGET_TEMPERATURE_STATE_SERVICE);
    }
}
