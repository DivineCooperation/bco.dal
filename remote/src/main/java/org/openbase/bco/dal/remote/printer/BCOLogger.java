package org.openbase.bco.dal.remote.printer;

/*-
 * #%L
 * BCO DAL Remote
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

import com.google.protobuf.ProtocolMessageEnum;
import org.openbase.bco.authentication.lib.jp.JPCredentialsDirectory;
import org.openbase.bco.dal.lib.layer.service.Services;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPDebugMode;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.MultiException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.rsb.com.jp.JPRSBHost;
import org.openbase.jul.extension.rsb.com.jp.JPRSBPort;
import org.openbase.jul.extension.rsb.com.jp.JPRSBTransport;
import org.openbase.jul.processing.StringProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.service.ServiceDescriptionType.ServiceDescription;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServicePattern;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class BCOLogger extends UnitStatePrinter {

    public static final String APP_NAME = BCOLogger.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(BCOLogger.class);

    public BCOLogger() throws InstantiationException {
        super(getTransitionPrintStream());
        this.printStaticRelations(getModelPrintStream());
    }

    public static void main(String[] args) throws InstantiationException, InterruptedException, InitializationException {

        /* Setup JPService */
        JPService.setApplicationName(APP_NAME);
        JPService.registerProperty(JPDebugMode.class);
        JPService.registerProperty(JPCredentialsDirectory.class);
        JPService.registerProperty(JPRSBHost.class);
        JPService.registerProperty(JPRSBPort.class);
        JPService.registerProperty(JPRSBTransport.class);

        try {
            JPService.parseAndExitOnError(args);
        } catch (IllegalStateException ex) {
            ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
            LOGGER.info(APP_NAME + " finished unexpected.");
        }

        LOGGER.info("Start " + APP_NAME + "...");

        try {
            new BCOLogger().init();
        } catch (InitializationException ex) {
            throw ExceptionPrinter.printHistoryAndReturnThrowable(ex, LOGGER, LogLevel.ERROR);
        }
        LOGGER.info(APP_NAME + " successfully started.");
    }

    private static PrintStream getModelPrintStream() {
        try {
            return new PrintStream(new FileOutputStream("/home/divine/workspace/prolog/bco-rule-learner/model.pl", false));
        } catch (FileNotFoundException ex) {
            ExceptionPrinter.printHistory("Error while loading model file, use system out instead.", ex, LOGGER);
            return System.out;
        }
    }

    private static PrintStream getTransitionPrintStream() {
        try {
            return new PrintStream(new FileOutputStream("/home/divine/workspace/prolog/bco-rule-learner/transitions.pl", false));
        } catch (FileNotFoundException ex) {
            ExceptionPrinter.printHistory("Error while loading transitions file, use system out instead.", ex, LOGGER);
            return System.out;
        }
    }

    private void printStaticRelations(final PrintStream printStream) {
        try {
            // print unit templates
            printStream.println("/**\n" +
                    " * Unit Templates\n" +
                    " * --> syntax: unit(unit_type, [service_type]).\n" +
                    " */");
            for (UnitTemplate unitTemplate : Registries.getTemplateRegistry(true).getUnitTemplates()) {
                printStream.println("unit("
                        + unitTemplate.getType().name().toLowerCase() + ", ["
                        + StringProcessor.transformCollectionToString(
                        unitTemplate.getServiceDescriptionList(),
                        serviceDescription -> serviceDescription.getServiceType().name().toLowerCase(),
                        ", ",
                        (ServiceDescription sd) -> sd.getPattern() != ServicePattern.PROVIDER)
                        + "]).");
            }
            printStream.println();

            // print service type mapping
            printStream.println("/**\n" +
                    " * Service Templates\n" +
                    " * --> syntax: service(service_type, [service_state_values]).\n" +
                    " */");
            for (ServiceTemplate serviceTemplate : Registries.getTemplateRegistry(true).getServiceTemplates()) {
                try {
                    // print discrete service state values
                    printStream.println("service("
                            + serviceTemplate.getType().name().toLowerCase() + ", [" + StringProcessor.transformCollectionToString(
                            Services.getServiceStateEnumValues(serviceTemplate.getType())
                            , (ProtocolMessageEnum o) -> o.getValueDescriptor().getName().toLowerCase(),
                            ", ",
                            type -> type.getValueDescriptor().getName().equals("UNKNOWN"))
                            + "]).");
                } catch (CouldNotPerformException ex) {
                    try {
                        // print continuous service state values
                        printStream.println("service(" +
                                serviceTemplate.getType().name().toLowerCase() + ", [" + StringProcessor.transformCollectionToString(
                                Services.getServiceStateFieldDataTypes(serviceTemplate.getType()),
                                (String o) -> o.toLowerCase(),
                                ", "
                        ) + "]).");
                    } catch (CouldNotPerformException exx) {
                        try {
                            MultiException.checkAndThrow(() -> "Skip ServiceState[" + serviceTemplate.getType().name() + "]", MultiException.push(this, ex, MultiException.push(this, exx, null)));
                        } catch (CouldNotPerformException exxx) {
                            ExceptionPrinter.printHistory(exxx, LOGGER, LogLevel.WARN);
                        }
                    }
                }
            }
            printStream.println();

            // print locations
            printStream.println("/**\n" +
                    " * Locations\n" +
                    " * --> syntax: location(unit_id, unit_alias, location_type, [labels]).\n" +
                    " */");
            for (UnitConfig unitConfig : Registries.getUnitRegistry(true).getUnitConfigs(UnitType.LOCATION)) {
                printStream.println(unitConfig.getUnitType().name().toLowerCase() + "("
                        + "'" + unitConfig.getId() + "', "
                        + "'" + unitConfig.getAlias(0) + "', "
                        + "'" + unitConfig.getLocationConfig().getType().name().toLowerCase() + "', ["
                        + StringProcessor.transformCollectionToString(
                        unitConfig.getLabel().getEntryList(),
                        mapFieldEntry -> mapFieldEntry.getKey() + "='" + mapFieldEntry.getValue(0) + "'",
                        ", ")
                        + "]).");
            }
            printStream.println();

            // print connections
            printStream.println("/**\n" +
                    " * Connections\n" +
                    " * --> syntax: connection(unit_id, unit_alias, connection_type, [labels], [locations]).\n" +
                    " */");
            for (UnitConfig unitConfig : Registries.getUnitRegistry(true).getUnitConfigs(UnitType.CONNECTION)) {
                printStream.println(unitConfig.getUnitType().name().toLowerCase() + "("
                        + "'" + unitConfig.getId() + "', "
                        + "'" + unitConfig.getAlias(0) + "', "
                        + "'" + unitConfig.getConnectionConfig().getType().name().toLowerCase() + "', ["
                        + StringProcessor.transformCollectionToString(
                        unitConfig.getLabel().getEntryList(),
                        mapFieldEntry -> mapFieldEntry.getKey() + "='" + mapFieldEntry.getValue(0) + "'",
                        ", ")
                        + "], ["
                        + StringProcessor.transformCollectionToString(
                        unitConfig.getConnectionConfig().getTileIdList(),
                        tile_id -> {
                            try {
                                return "'" + Registries.getUnitRegistry().getUnitConfigById(tile_id, UnitType.LOCATION).getId() + "'";
                            } catch (CouldNotPerformException e) {
                                return "na";
                            }
                        },
                        ", ")
                        + "], move).");
            }
            printStream.println();
        } catch (CouldNotPerformException | InterruptedException ex) {
            ExceptionPrinter.printHistory("Could not print unit templates.", ex, LOGGER);
        }
    }
}
