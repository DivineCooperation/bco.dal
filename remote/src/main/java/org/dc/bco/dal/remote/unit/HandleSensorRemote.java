/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.dal.remote.unit;

import org.dc.bco.dal.lib.layer.unit.HandleSensorInterface;
import org.dc.jul.exception.CouldNotPerformException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.homeautomation.state.HandleStateType.HandleState;
import rst.homeautomation.unit.HandleSensorType.HandleSensor;

/**
 *
 * @author thuxohl
 */
public class HandleSensorRemote extends AbstractUnitRemote<HandleSensor> implements HandleSensorInterface {

    static {
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(HandleSensor.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(HandleState.getDefaultInstance()));
    }

    public HandleSensorRemote() {
    }

    @Override
    public void notifyUpdated(HandleSensor data) {
    }

    @Override
    public HandleState getHandle() throws CouldNotPerformException {
        return getData().getHandleState();
    }

}
