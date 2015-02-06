/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.bindings.openhab.service;

import de.citec.dal.hal.service.BrightnessService;
import de.citec.dal.hal.device.DeviceInterface;
import de.citec.dal.hal.unit.UnitInterface;
import de.citec.jul.exception.CouldNotPerformException;

/**
 *
 * @author mpohling
 * @param <ST> Related service type.
 */
public class BrightnessServiceImpl<ST extends BrightnessService & UnitInterface> extends OpenHABServiceImpl<ST> implements BrightnessService {

    public BrightnessServiceImpl(DeviceInterface device, ST unit) {
        super(device, unit);
    }

    @Override
    public double getBrightness() throws CouldNotPerformException {
        return unit.getBrightness();
    }

    @Override
    public void setBrightness(double brightness) throws CouldNotPerformException {
        throw new UnsupportedOperationException("Not supported yet.");
//            setColor(cloneBuilder().getColorBuilder().setValue(brightness).build());
    }
}
