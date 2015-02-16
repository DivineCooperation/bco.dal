/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.bindings.openhab.transform;

import de.citec.dal.bindings.openhab.OpenhabBinding;
import de.citec.dal.hal.service.ServiceType;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.CouldNotTransformException;
import de.citec.jul.exception.NotAvailableException;
import de.citec.jul.exception.NotSupportedException;
import rst.homeautomation.openhab.OpenhabCommandType;

/**
 *
 * @author mpohling
 */
public final class OpenhabCommandTransformer {

    public static Object getServiceData(OpenhabCommandType.OpenhabCommand command, String serviceName) throws CouldNotPerformException {

        // Detect service type
        ServiceType serviceType;
        try {
            serviceType = ServiceType.valueOfByServiceName(serviceName);
        } catch (CouldNotPerformException ex) {
            throw new NotAvailableException("ServiceData", ex);
        }

        //TODO tamino: 
        
        // Transform service data.
        switch (command.getType()) {
            case DECIMAL:
                switch (serviceType) {
                    case MOTION:
                        return MotionStateTransformer.transform(command.getDecimal());
                    case TAMPER:
                        return TamperStateTransformer.transform(command.getDecimal());
                    default:
                        // native double type
                        return command.getDecimal();
                }
            case HSB:
                switch (serviceType) {
                    case COLOR:
                        return HSVColorTransformer.transform(command.getHsb());
                    default:
                        throw new NotSupportedException(serviceType, OpenhabBinding.class);
                }
            case INCREASEDECREASE:
//				return IncreaseDecreaseTransformer(command.getIncreaseDecrease());
                throw new NotSupportedException(command.getType(), OpenhabCommandTransformer.class);
            case ONOFF:
                switch (serviceType) {
                    case BUTTON:
                        return ButtonStateTransformer.transform(command.getOnOff().getState());
                    case POWER:
                        return PowerStateTransformer.transform(command.getOnOff().getState());
                    default:
                        throw new NotSupportedException(serviceType, OpenhabBinding.class);
                }
            case OPENCLOSED:
                return OpenClosedStateTransformer.transform(command.getOpenClosed().getState());
            case PERCENT:
                // native int type
                return new Double(command.getPercent().getValue());
            case STOPMOVE:
                return StopMoveStateTransformer.transform(command.getStopMove().getState());
            case STRING:
                // native string type
                return command.getText();
            case UPDOWN:
                return UpDownStateTransformer.transform(command.getUpDown().getState());
            default:
                throw new CouldNotTransformException("No corresponding data found for " + command + ".");
        }
    }

    public static Object getCommandData(final OpenhabCommandType.OpenhabCommand command) throws CouldNotPerformException {

        switch (command.getType()) {
            case DECIMAL:
                return command.getDecimal();
            case HSB:
                return command.getHsb();
            case INCREASEDECREASE:
                return command.getIncreaseDecrease();
            case ONOFF:
                return command.getOnOff();
            case OPENCLOSED:
                return command.getOpenClosed();
            case PERCENT:
                return command.getPercent();
            case STOPMOVE:
                return command.getStopMove();
            case STRING:
                return command.getText();
            case UPDOWN:
                return command.getUpDown();
            default:
                throw new CouldNotTransformException("No corresponding data found for " + command + ".");
        }
    }
}
