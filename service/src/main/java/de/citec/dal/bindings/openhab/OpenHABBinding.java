/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.bindings.openhab;

import de.citec.dal.bindings.AbstractDALBinding;
import de.citec.dal.DALService;
import de.citec.dal.bindings.openhab.transform.OpenhabCommandTransformer;
import de.citec.jul.extension.rsb.com.RSBCommunicationService;
import de.citec.jul.extension.rsb.com.RSBRemoteService;
import de.citec.jps.core.JPService;
import de.citec.jps.properties.JPHardwareSimulationMode;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.InvalidStateException;
import de.citec.jul.exception.InvocationFailedException;
import de.citec.jul.exception.NotAvailableException;
import de.citec.jul.exception.printer.LogLevel;
import de.citec.jul.extension.protobuf.ClosableDataBuilder;
import de.citec.jul.extension.rsb.iface.RSBLocalServerInterface;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsb.Event;
import rsb.Scope;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rsb.patterns.EventCallback;
import rst.homeautomation.openhab.DALBindingType;
import rst.homeautomation.openhab.DALBindingType.DALBinding;
import rst.homeautomation.openhab.OpenhabCommandType;
import rst.homeautomation.openhab.OpenhabCommandType.OpenhabCommand;
import rst.homeautomation.openhab.RSBBindingType;
import rst.homeautomation.openhab.RSBBindingType.RSBBinding;
import rst.homeautomation.state.ActiveDeactiveType;

/**
 * @author thuxohl
 * @author mpohling
 */
public class OpenHABBinding extends AbstractDALBinding implements OpenHABBindingInterface {

    public static final String RPC_METHODE_INTERNAL_RECEIVE_UPDATE = "internalReceiveUpdate";
    public static final String RPC_METHODE_EXECUTE_COMMAND = "executeCommand";

    public static final Scope SCOPE_DAL = new Scope("/dal");
    public static final Scope SCOPE_OPENHAB = new Scope("/openhab");

    private static final Logger logger = LoggerFactory.getLogger(OpenHABBinding.class);

    private OpenHABCommandExecutor commandExecutor;

    private final RSBRemoteService<RSBBinding> openhabRemoteService;
    private final RSBCommunicationService<DALBinding, DALBinding.Builder> dalCommunicationService;

    static {
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(OpenhabCommandType.OpenhabCommand.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(RSBBindingType.RSBBinding.getDefaultInstance()));
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(DALBindingType.DALBinding.getDefaultInstance()));
    }

    public OpenHABBinding() throws InstantiationException, InterruptedException {
        try {
            this.commandExecutor = new OpenHABCommandExecutor(DALService.getRegistryProvider().getUnitRegistry());

            openhabRemoteService = new RSBRemoteService<RSBBinding>() {

                @Override
                public void notifyUpdated(final RSBBinding data) {
                    OpenHABBinding.this.notifyUpdated(data);
                }
            };
            openhabRemoteService.init(SCOPE_OPENHAB);

            dalCommunicationService = new RSBCommunicationService<DALBinding, DALBinding.Builder>(DALBinding.newBuilder()) {

                @Override
                public void registerMethods(RSBLocalServerInterface server) throws CouldNotPerformException {
                    OpenHABBinding.this.registerMethods(server);
                }
            };

            dalCommunicationService.init(SCOPE_DAL);

            if (!JPService.getProperty(JPHardwareSimulationMode.class).getValue()) {
                // Init Openhab connection
                openhabRemoteService.activate();
                dalCommunicationService.activate();

                try (ClosableDataBuilder<DALBinding.Builder> dataBuilder = dalCommunicationService.getDataBuilder(this)) {
                    dataBuilder.getInternalBuilder().setState(ActiveDeactiveType.ActiveDeactive.newBuilder().setState(ActiveDeactiveType.ActiveDeactive.ActiveDeactiveState.ACTIVE));
                } catch (Exception ex) {
                    throw new CouldNotPerformException("Could not setup dalCommunicationService as active.", ex);
                }
            }
        } catch (CouldNotPerformException ex) {
            throw new de.citec.jul.exception.InstantiationException(this, ex);
        }
    }

    public final void notifyUpdated(final RSBBinding data) {
        switch (data.getState().getState()) {
            case ACTIVE:
                logger.info("Active dal binding state!");
                break;
            case DEACTIVE:
                logger.info("Deactive dal binding state!");
                break;
            case UNKNOWN:
                logger.info("Unkown dal binding state!");
                break;
        }
    }

    public final void registerMethods(final RSBLocalServerInterface server) {
        try {
            server.addMethod(RPC_METHODE_INTERNAL_RECEIVE_UPDATE, new InternalReceiveUpdateCallback());
        } catch (CouldNotPerformException ex) {
            logger.warn("Could not add methods to local server in [" + getClass().getSimpleName() + "]", ex);
        }
    }

    @Override
    public void internalReceiveUpdate(final OpenhabCommand command) throws CouldNotPerformException {
        try {
            commandExecutor.receiveUpdate(command);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Skip item update [" + command.getItem() + " = " + OpenhabCommandTransformer.getCommandData(command) + "]!", ex);
        }
    }

    public class InternalReceiveUpdateCallback extends EventCallback {

        @Override
        public Event invoke(final Event request) throws Throwable {
            try {
                OpenHABBinding.this.internalReceiveUpdate((OpenhabCommand) request.getData());
            } catch (Throwable cause) {
                throw ExceptionPrinter.printHistoryAndReturnThrowable(new InvocationFailedException(this, OpenHABBinding.this, cause), logger , LogLevel.ERROR);
            }
            return new Event(Void.class);
        }
    }

    @Override
    public Future executeCommand(final OpenhabCommandType.OpenhabCommand command) throws CouldNotPerformException {
        try {
            
            if(!command.hasItem() || command.getItem().isEmpty()) {
                throw new NotAvailableException("command item");
            }
            
            if(!command.hasType()) {
                throw new NotAvailableException("command type");
            }
            
            if (JPService.getProperty(JPHardwareSimulationMode.class).getValue()) {
                internalReceiveUpdate(command);
                return null;
            }
            if (!openhabRemoteService.isConnected()) {
                throw new InvalidStateException("Dal openhab binding could not reach openhab server! Please check if openhab is still running!");
            }

            openhabRemoteService.callMethod(RPC_METHODE_EXECUTE_COMMAND, command);
            return null; // TODO: mpohling implement future handling.
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not execute " + command + "!", ex);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[version=" + getClass().getPackage().getImplementationVersion() + "]";
    }
    /**
     * TODO mpohling: add the following code to the dal openhabbinding pom.
     *
     * <plugin>
     * <groupId>org.apache.maven.plugins</groupId>
     * <artifactId>maven-jar-plugin</artifactId>
     * <configuration>
     * <archive>
     * <manifest>
     * <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
     * <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
     * </manifest>
     * </archive>
     * </configuration>
     * </plugin>
     */

}
