/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.dal.remote.unit;

import org.dc.bco.dal.lib.layer.unit.ButtonInterface;
import org.dc.jul.exception.CouldNotPerformException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.homeautomation.state.ButtonStateType.ButtonState;
import rst.homeautomation.unit.ButtonType.Button;

/**
 *
 * @author thuxohl
 */
public class ButtonRemote extends AbstractUnitRemote<Button> implements ButtonInterface {

    static {
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(Button.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(ButtonState.getDefaultInstance()));
    }

    public ButtonRemote() {
    }

    @Override
    public void notifyUpdated(Button data) {
    }

    @Override
    public ButtonState getButton() throws CouldNotPerformException {
        return getData().getButtonState();
    }
}
