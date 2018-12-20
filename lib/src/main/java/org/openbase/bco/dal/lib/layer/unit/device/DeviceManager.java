package org.openbase.bco.dal.lib.layer.unit.device;

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

import org.openbase.bco.dal.lib.layer.unit.HostUnitManager;
import org.openbase.bco.dal.lib.layer.unit.UnitController;
import org.openbase.bco.dal.lib.layer.unit.UnitControllerRegistry;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public interface DeviceManager extends HostUnitManager, DeviceFactoryProvider {

    /**
     * Enables access of the controller registry of this manager.
     * <p>
     * Note: Mainly used for accessing the controller via test routines.
     *
     * @return the controller registry.
     */
    UnitControllerRegistry<DeviceController> getDeviceControllerRegistry();

    /**
     * Enables access of the controller registry of this manager.
     * <p>
     * Note: Mainly used for accessing the controller via test routines.
     *
     * @return the controller registry.
     */
    UnitControllerRegistry<UnitController<?, ?>> getUnitControllerRegistry();

    /**
     * All devices will be supported by default. Feel free to overwrite method
     * to changing this behavior.
     *
     * @param config
     *
     * @return true if supported
     */
    @Override
    default boolean isSupported(final UnitConfig config) {
        return config.getUnitType() == UnitType.DEVICE;
    }

}
