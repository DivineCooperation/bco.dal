/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.remote.unit;

import de.citec.dal.transform.HSVColorToRGBColorTransformer;
import de.citec.dal.hal.unit.AmbientLightInterface;
import de.citec.jul.exception.CouldNotPerformException;
import org.slf4j.LoggerFactory;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.homeautomation.unit.AmbientLightType;
import rst.homeautomation.state.PowerStateType.PowerState;
import rst.vision.HSVColorType;
import rst.vision.HSVColorType.HSVColor;

/**
 *
 * @author mpohling
 */
public class AmbientLightRemote extends DALRemoteService<AmbientLightType.AmbientLight> implements AmbientLightInterface {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AmbientLightRemote.class);

    static {
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(AmbientLightType.AmbientLight.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(HSVColorType.HSVColor.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(PowerState.getDefaultInstance()));
    }

    public AmbientLightRemote() {
    }

    public void setColor(final java.awt.Color color) throws CouldNotPerformException {
        try {
            setColor(HSVColorToRGBColorTransformer.transform(color));
        } catch (CouldNotPerformException ex) {
            logger.warn("Could not set color!", ex);
        }
    }

    @Override
    public void setColor(final HSVColor color) throws CouldNotPerformException {
        callMethodAsync("setColor", color);
    }

    @Override
    public void notifyUpdated(AmbientLightType.AmbientLight data) {
    }

    @Override
    public void setBrightness(Double brightness) throws CouldNotPerformException {
        callMethodAsync("setBrightness", brightness);
    }

    @Override
    public PowerState getPower() throws CouldNotPerformException {
        return this.getData().getPowerState();
    }

    @Override
    public void setPower(PowerState.State state) throws CouldNotPerformException {
        callMethodAsync("setPower", PowerState.newBuilder().setValue(state).build());
    }

    @Override
    public HSVColor getColor() throws CouldNotPerformException {
        return this.getData().getColor();
    }

    @Override
    public Double getBrightness() throws CouldNotPerformException {
        return this.getData().getColor().getValue();
    }
}
