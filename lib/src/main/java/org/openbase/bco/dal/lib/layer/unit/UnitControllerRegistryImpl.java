package org.openbase.bco.dal.lib.layer.unit;

/*
 * #%L
 * BCO DAL Library
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
import com.google.protobuf.GeneratedMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.InvalidStateException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.provider.DataProvider;
import org.openbase.jul.storage.registry.ControllerRegistryImpl;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 *
 * @param <CONTROLLER> the type of unit controller.
 */
public class UnitControllerRegistryImpl<CONTROLLER extends UnitController<?,?>> extends ControllerRegistryImpl<String, CONTROLLER> implements UnitControllerRegistry<CONTROLLER> {

    private final Map<String, CONTROLLER> scopeControllerMap;

    public UnitControllerRegistryImpl() throws InstantiationException {
        this.scopeControllerMap = new HashMap<>();
        addObserver(new UnitControllerSynchronizer());
    }

    public UnitControllerRegistryImpl(final HashMap<String, CONTROLLER> entryMap) throws InstantiationException {
        super(entryMap);
        this.scopeControllerMap = new HashMap<>();
        addObserver(new UnitControllerSynchronizer());
    }

    /**
     * {@inheritDoc}
     *
     * @throws NotAvailableException {@inheritDoc}
     */
    @Override
    public CONTROLLER getUnitByScope(final String scope) throws NotAvailableException {
        final CONTROLLER controller = scopeControllerMap.get(scope);
        if (controller == null) {
            throw new NotAvailableException("UnitController", new InvalidStateException("No unit controller for given scope registered!"));
        }
        return controller;
    }

    /**
     * Class to synchronize the scope controller map with the unit controller registry.
     */
    private class UnitControllerSynchronizer implements Observer<DataProvider<Map<String, CONTROLLER>>, Map<String, CONTROLLER>> {

        @Override
        public void update(final DataProvider<Map<String, CONTROLLER>> source, final Map<String, CONTROLLER> data) throws Exception {

            final Collection<CONTROLLER> unitControllerCollection = new ArrayList<>(data.values());
            // add new entries to the scope controller map
            for (final CONTROLLER controller : unitControllerCollection) {
                scopeControllerMap.put(ScopeGenerator.generateStringRep(controller.getScope()), controller);
            }

            // remove controller which are no longer provided by the registry
            for (final CONTROLLER controller : new ArrayList<>(scopeControllerMap.values())) {
                if (unitControllerCollection.contains(controller)) {
                    continue;
                }
                scopeControllerMap.remove(ScopeGenerator.generateStringRep(controller.getScope()));
            }
        }
    }
}
