package org.openbase.bco.dal.lib.layer.unit;

/*
 * #%L
 * DAL Library
 * %%
 * Copyright (C) 2014 - 2016 openbase.org
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
import java.util.concurrent.Future;
import org.openbase.bco.dal.lib.layer.service.operation.PowerStateOperationService;
import org.openbase.bco.dal.lib.layer.service.operation.StandbyStateOperationService;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.protobuf.ClosableDataBuilder;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.homeautomation.state.PowerStateType.PowerState;
import rst.homeautomation.state.StandbyStateType.StandbyState;
import rst.homeautomation.unit.MonitorDataType.MonitorData;
import rst.homeautomation.unit.UnitConfigType;

/**
 *
 * @author mpohling
 */
public class MonitorController extends AbstractUnitController<MonitorData, MonitorData.Builder> implements Monitor {
    
    static {
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(MonitorData.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(PowerState.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(StandbyState.getDefaultInstance()));
    }
    
    private PowerStateOperationService powerStateService;
    private StandbyStateOperationService standbyStateService;
    
    public MonitorController(final UnitHost unitHost, final MonitorData.Builder builder) throws InstantiationException, CouldNotPerformException {
        super(MonitorController.class, unitHost, builder);
    }
    
    @Override
    public void init(UnitConfigType.UnitConfig config) throws InitializationException, InterruptedException {
        super.init(config);
        try {
            this.powerStateService = getServiceFactory().newPowerService(this);
            this.standbyStateService = getServiceFactory().newStandbyService(this);
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }
    
    public void updatePowerStateProvider(final PowerState powerState) throws CouldNotPerformException {
        logger.debug("Apply powerState Update[" + powerState + "] for " + this + ".");
        
        try (ClosableDataBuilder<MonitorData.Builder> dataBuilder = getDataBuilder(this)) {
            dataBuilder.getInternalBuilder().setPowerState(powerState);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not apply powerState Update[" + powerState + "] for " + this + "!", ex);
        }
    }
    
    @Override
    public Future<Void> setPowerState(final PowerState powerState) throws CouldNotPerformException {
        logger.debug("Setting [" + getLabel() + "] to PowerState [" + powerState + "]");
        return powerStateService.setPowerState(powerState);
    }
    
    @Override
    public PowerState getPowerState() throws NotAvailableException {
        try {
            return getData().getPowerState();
        } catch (CouldNotPerformException ex) {
            throw new NotAvailableException("powerState", ex);
        }
    }
    
    @Override
    public Future<Void> setStandbyState(StandbyState state) throws CouldNotPerformException {
        logger.debug("Setting [" + getLabel() + "] to StandbyState [" + state + "]");
        return standbyStateService.setStandbyState(state);
    }
    
    @Override
    public StandbyState getStandbyState() throws NotAvailableException {
        try {
            return getData().getStandbyState();
        } catch (CouldNotPerformException ex) {
            throw new NotAvailableException("standbyState", ex);
        }
    }
    
    public void updateStandbyStateProvider(final StandbyState standbyState) throws CouldNotPerformException {
        logger.debug("Apply standbyState Update[" + standbyState + "] for " + this + ".");
        
        try (ClosableDataBuilder<MonitorData.Builder> dataBuilder = getDataBuilder(this)) {
            dataBuilder.getInternalBuilder().setStandbyState(standbyState);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not apply standbyState Update[" + standbyState + "] for " + this + "!", ex);
        }
    }
}