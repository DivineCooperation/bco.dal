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

import com.google.protobuf.Message;
import org.openbase.bco.dal.lib.layer.service.Services;
import org.openbase.bco.dal.lib.layer.unit.Unit;
import org.openbase.bco.dal.remote.layer.unit.CustomUnitPool;
import org.openbase.bco.dal.remote.layer.unit.Units;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.iface.DefaultInitializable;
import org.openbase.jul.pattern.Filter;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.processing.StringProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.service.ServiceCommunicationTypeType.ServiceCommunicationType.CommunicationType;
import rst.domotic.service.ServiceDescriptionType.ServiceDescription;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

public class UnitStatePrinter implements DefaultInitializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitStatePrinter.class);

    private final CustomUnitPool customUnitPool;
    private final Observer<Unit, Message> unitStateObserver;
    private final PrintStream printStream;

    public UnitStatePrinter(final PrintStream printStream, final Filter<UnitConfig>... filters) throws InstantiationException {
        try {
            this.printStream = printStream;
            this.customUnitPool = new CustomUnitPool(filters);
            this.unitStateObserver = (source, data) -> print(source, data);
            this.printStaticRelations();
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    @Override
    public void init() throws InitializationException, InterruptedException {
        try {
            customUnitPool.init();
            printStream.println("### Service State Updates: unit(unit_type , unit_id, service_state[service_value]). ###");

            customUnitPool.addObserver(unitStateObserver);
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    private void printStaticRelations() {
        try {
            // print unit templates
            printStream.println("### Unit Templates: unit_type(service_type). ###");
            for (UnitTemplate unitTemplate : Registries.getTemplateRegistry(true).getUnitTemplates()) {
                printStream.println(unitTemplate.getType().name().toLowerCase() + "("+ StringProcessor.transformCollectionToString(unitTemplate.getServiceDescriptionList(), ", ", serviceDescription -> serviceDescription.getServiceType().name().toLowerCase())+").");
            }
            printStream.println();

            // print service type mapping
            printStream.println("### Service Templates: service_type\" + \"(service_state). ###");
            printStream.println("service_type" + "(service_state).");
            for (ServiceTemplate sericeTemplate : Registries.getTemplateRegistry(true).getServiceTemplates()) {
                printStream.println(sericeTemplate.getType().name().toLowerCase() + "(" + sericeTemplate.getCommunicationType().name().toLowerCase()+ ")");
            }
            printStream.println();
        } catch (CouldNotPerformException | InterruptedException ex) {
            ExceptionPrinter.printHistory("Could not print unit templates.", ex, LOGGER);
        }
    }

    private void print(Unit unit, Message data) {
        try {
            for (ServiceDescription serviceDescription : unit.getUnitTemplate().getServiceDescriptionList()) {
                print(unit, serviceDescription.getServiceType(), data);
            }
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not print " + unit, ex, LOGGER);
        }
    }

    private void print(Unit unit, ServiceType serviceType, Message data) {
        try {
            final List<String> states = Services.generateServiceProviderStringRepresentation(data, serviceType);
            if (!states.isEmpty()) {
                printStream.println("===========================================================================================================");
            }
            for (String extractServiceState : states) {
                printStream.println("unit(" + unit.getUnitType().name().toLowerCase() + ", " + unit.getId() + ", " + extractServiceState + ").");
            }
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not print " + serviceType.name() + " of " + unit, ex, LOGGER);
        }
    }
}
