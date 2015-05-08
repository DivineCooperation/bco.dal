/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.hal.unit;

import de.citec.dal.hal.device.Device;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.NotAvailableException;
import de.citec.jul.extension.protobuf.ClosableDataBuilder;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.homeautomation.state.OpenClosedTiltedType.OpenClosedTilted.OpenClosedTiltedState;
import rst.homeautomation.unit.HandleSensorType;
import rst.homeautomation.unit.HandleSensorType.HandleSensor;
import rst.homeautomation.unit.UnitConfigType;

/**
 *
 * @author thuxohl
 */
public class HandleSensorController extends AbstractUnitController<HandleSensor, HandleSensor.Builder> implements HandleSensorInterface {

    static {
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(HandleSensorType.HandleSensor.getDefaultInstance()));
    }

    public HandleSensorController(final UnitConfigType.UnitConfig config, Device device, HandleSensor.Builder builder) throws InstantiationException, CouldNotPerformException {
        super(config, HandleSensorController.class, device, builder);
    }

    //TODO tamino: rename OpenClosedTiltedState to HandleState

    public void updateHandle(final OpenClosedTiltedState value) throws CouldNotPerformException {
        logger.debug("Apply handle state Update[" + value + "] for " + this + ".");

        try (ClosableDataBuilder<HandleSensor.Builder> dataBuilder = getDataBuilder(this)) {
            dataBuilder.getInternalBuilder().getHandleStateBuilder().setState(value);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not apply handle state Update[" + value + "] for " + this + "!", ex);
        }
    }

    //TODO tamino: rename to getHandleState
    @Override
    public OpenClosedTiltedState getHandle() throws NotAvailableException {
        try {
            return getData().getHandleState().getState();
        } catch (CouldNotPerformException ex) {
            throw new NotAvailableException("handle state", ex);
        }
    }
}
