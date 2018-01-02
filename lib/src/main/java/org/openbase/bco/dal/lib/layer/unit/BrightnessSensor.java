package org.openbase.bco.dal.lib.layer.unit;

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
import org.openbase.bco.dal.lib.layer.service.provider.BrightnessStateProviderService;
import org.openbase.bco.dal.lib.layer.service.provider.IlluminanceStateProviderService;
import org.openbase.jul.exception.NotAvailableException;
import rst.domotic.state.BrightnessStateType.BrightnessState;

/**
 * Is not supported anymore. Use LightSensor instead.
 *
 * * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
@Deprecated
public interface BrightnessSensor extends IlluminanceStateProviderService, BrightnessStateProviderService {

    @Deprecated
    @Override
    public BrightnessState getBrightnessState() throws NotAvailableException;
}
