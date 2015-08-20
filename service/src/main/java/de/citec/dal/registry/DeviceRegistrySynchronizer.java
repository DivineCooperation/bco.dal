/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.registry;

import de.citec.dal.hal.device.DeviceFactory;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.MultiException;
import de.citec.jul.extension.protobuf.ProtobufListDiff;
import de.citec.jul.pattern.Observable;
import de.citec.jul.pattern.Observer;
import de.citec.jul.schedule.RecurrenceEventFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.binding.BindingTypeHolderType;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.device.DeviceRegistryType;
import rst.homeautomation.state.InventoryStateType;

/**
 *
 * @author mpohling
 */
public class DeviceRegistrySynchronizer {

    private static final Logger logger = LoggerFactory.getLogger(DeviceRegistrySynchronizer.class);
    private final DeviceFactory factory;
    private final DeviceRegistry registry;
    private final DeviceRegistryRemote remoteRegistry;
    private final Observer<DeviceRegistryType.DeviceRegistry> remoteChangeObserver;
    private final RecurrenceEventFilter recurrenceSyncFilter;
    private final ProtobufListDiff<String, DeviceConfig, DeviceConfig.Builder> deviceConfigDiff;

    public DeviceRegistrySynchronizer(final DeviceRegistry registry, final DeviceRegistryRemote remoteRegistry) throws InstantiationException {
        try {
            this.factory = new DeviceFactory(remoteRegistry);
            this.registry = registry;
            this.remoteRegistry = remoteRegistry;
            this.deviceConfigDiff = new ProtobufListDiff<>();
            this.recurrenceSyncFilter = new RecurrenceEventFilter(15000) {

                @Override
                public void relay() throws Exception {
                    internalSync();
                }
            };

            this.remoteChangeObserver = new Observer<DeviceRegistryType.DeviceRegistry>() {

                @Override
                public void update(Observable<DeviceRegistryType.DeviceRegistry> source, DeviceRegistryType.DeviceRegistry data) throws Exception {
                    sync();
                }
            };
        } catch (Exception ex) {
            throw new InstantiationException(this, ex);
        }
    }

    public void init() {
        this.remoteRegistry.addObserver(remoteChangeObserver);
        try {
            this.internalSync();
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(logger, new CouldNotPerformException("Initial sync failed!", ex));
        }
    }

    public void shutdown() {
        this.remoteRegistry.removeObserver(remoteChangeObserver);
        this.recurrenceSyncFilter.cancel();
    }

    private void sync() {
        recurrenceSyncFilter.trigger();
    }

    private synchronized void internalSync() throws CouldNotPerformException {
        logger.info("Trigger registry sync...");

        MultiException.ExceptionStack exceptionStack = null;

        try {
            deviceConfigDiff.diff(remoteRegistry.getDeviceConfigs());

            for (DeviceConfig config : deviceConfigDiff.getRemovedMessageMap().getMessages()) {
                try {
                    registry.remove(config.getId());
                } catch (Exception ex) {
                    exceptionStack = MultiException.push(this, ex, exceptionStack);
                }
            }

            for (DeviceConfig config : deviceConfigDiff.getUpdatedMessageMap().getMessages()) {
                try {
                    registry.update(factory.newDevice(config));
                } catch (Exception ex) {
                    exceptionStack = MultiException.push(this, ex, exceptionStack);
                }
            }

            for (DeviceConfig config : deviceConfigDiff.getNewMessageMap().getMessages()) {
                try {
                    if (verifyDeviceConfig(config)) {
                        registry.register(factory.newDevice(config));
                    }
                } catch (Exception ex) {
                    exceptionStack = MultiException.push(this, ex, exceptionStack);
                }
            }

            logger.info(registry.size() + " devices successfully loaded. " + MultiException.size(exceptionStack) + " skipped.");
            MultiException.checkAndThrow("Could not sync all devices!", exceptionStack);

        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Device registry sync failed!", ex);
        }
    }

    private boolean verifyDeviceConfig(final DeviceConfig config) throws CouldNotPerformException {
        try {

            // load device class
            DeviceClass deviceClass;
            try {
                deviceClass = remoteRegistry.getDeviceClassById(config.getDeviceClassId());
            } catch (CouldNotPerformException ex) {
                throw new CouldNotPerformException("Could not load device class of Device[" + config.getId() + "] !", ex);
            }

            if (!deviceClass.getBindingConfig().getType().equals(BindingTypeHolderType.BindingTypeHolder.BindingType.OPENHAB)) {
                return false;
            }

            if (config.getInventoryState().getValue() != InventoryStateType.InventoryState.State.INSTALLED) {
                logger.info("Skip Device[" + config.getLabel() + "] because it is currently not installed!");
                return false;
            }
            return true;
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not verify device config!", ex);
        }
    }
}