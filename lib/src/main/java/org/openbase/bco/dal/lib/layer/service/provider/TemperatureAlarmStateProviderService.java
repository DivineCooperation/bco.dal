package org.openbase.bco.dal.lib.layer.service.provider;

/*
 * #%L
 * BCO DAL Library
 * %%
 * Copyright (C) 2014 - 2021 openbase.org
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
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.annotation.RPCMethod;
import org.openbase.type.domotic.state.AlarmStateType.AlarmState;

import static org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType.TEMPERATURE_ALARM_STATE_SERVICE;

/**
 *
 * * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public interface TemperatureAlarmStateProviderService extends AlarmStateProviderService {

    @RPCMethod(legacy = true)
    default AlarmState getTemperatureAlarmState() throws NotAvailableException {
        return (AlarmState) getServiceProvider().getServiceState(TEMPERATURE_ALARM_STATE_SERVICE);
    }
}
