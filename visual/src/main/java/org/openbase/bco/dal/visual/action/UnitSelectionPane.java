package org.openbase.bco.dal.visual.action;

/*-
 * #%L
 * BCO DAL Visualisation
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.bco.registry.remote.login.BCOLogin;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.MultiException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import org.openbase.jul.extension.rst.processing.MultiLanguageTextProcessor;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.visual.javafx.control.AbstractFXController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.service.ServiceConfigType.ServiceConfig;
import org.openbase.type.domotic.service.ServiceDescriptionType.ServiceDescription;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.state.EnablingStateType.EnablingState;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UnitSelectionPane extends AbstractFXController {
    public final static LocationUnitConfigHolder ALL_LOCATION = new LocationUnitConfigHolder(null);
    public final static UnitConfigHolder ALL_UNIT = new UnitConfigHolder(null);
    public final static ServiceTemplateHolder ALL_Service = new ServiceTemplateHolder(null);
    private static final Logger LOGGER = LoggerFactory.getLogger(UnitSelectionPane.class);
    private final ReentrantReadWriteLock updateComponentLock;
    private SimpleStringProperty unitIdProperty;
    private LocationUnitConfigHolder selectedLocationConfigHolder;
    private UnitConfigHolder selectedUnitConfigHolder;
    @FXML
    private ComboBox<LocationUnitConfigHolder> locationComboBox;
    @FXML
    private ComboBox<UnitTemplateHolder> unitTemplateComboBox;
    @FXML
    private ComboBox<ServiceTemplateHolder> serviceTemplateComboBox;
    @FXML
    private ComboBox<UnitConfigHolder> unitComboBox;


    public UnitSelectionPane() {
        this.unitIdProperty = new SimpleStringProperty();
        this.updateComponentLock = new ReentrantReadWriteLock();
        final Observer dataObserver = (source, data) -> update();

        try {
            Registries.waitForData();
            BCOLogin.loginBCOUser();
            Registries.getTemplateRegistry(false).addDataObserver(dataObserver);
            Registries.getUnitRegistry(false).addDataObserver(dataObserver);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Could not register dynamic content observer!", ex, System.out);
        } catch (InterruptedException ex) {
            // skip observer registration on shutdown
        }
    }

    @Override
    public void initContent() throws InitializationException {


        locationComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (!locationComboBox.isDisabled()) {
                updateDynamicContent();
            }
        }));

        unitTemplateComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (!unitTemplateComboBox.isDisabled()) {
                updateDynamicContent();
            }
        }));

        serviceTemplateComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (!serviceTemplateComboBox.isDisabled()) {
                updateDynamicContent();
            }
        }));

        unitComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (!unitComboBox.isDisabled()) {
                if(newValue == null) {
                    LOGGER.warn("ignore null value update!");
                    return;
                }
                LOGGER.info("trigger update!");
                unitIdProperty.set(newValue.getConfig().getId());
            }
        }));

//        unitComboBox.getItems().filtered(unitConfigHolder -> {
//            if ((!locationComboBox.getSelectionModel().isEmpty()
//                    && locationComboBox.getSelectionModel().getSelectedIndex() != 0
//                    && !locationComboBox.getSelectionModel().getSelectedItem().getConfig().getId().equals(unitConfigHolder.getConfig().getPlacementConfig().getLocationId()))) {
//                return false;
//            }
//            if ((!unitTemplateComboBox.getSelectionModel().isEmpty()
//                    && unitTemplateComboBox.getSelectionModel().getSelectedIndex() != 0
//                    && !unitTemplateComboBox.getSelectionModel().getSelectedItem().getTemplate().getType().equals(unitConfigHolder.getConfig().getUnitType()))) {
//                return false;
//            }
//            if (!serviceTemplateComboBox.getSelectionModel().isEmpty()
//                    && serviceTemplateComboBox.getSelectionModel().getSelectedIndex() != 0) {
//                boolean found = false;
//                final ServiceType selectedServicType = serviceTemplateComboBox.getSelectionModel().getSelectedItem().getTemplate().getType();
//                for (ServiceConfig serviceConfig : unitConfigHolder.getConfig().getServiceConfigList()) {
//                    if (serviceConfig.getServiceDescription().getServiceType() == selectedServicType) {
//                        found = true;
//                        break;
//                    }
//                }
//                if (!found) {
//                    return false;
//                }
//            }
//            return true;
//        });
    }

    @Override
    public void updateDynamicContent() {
        if (!Registries.isDataAvailable()) {
            locationComboBox.setDisable(true);
            unitTemplateComboBox.setDisable(true);
            serviceTemplateComboBox.setDisable(true);
            unitComboBox.setDisable(true);
            return;
        }

        MultiException.ExceptionStack exceptionStack = null;

        updateComponentLock.writeLock().lock();
        try {
            LOGGER.debug("Update selectorPanel!");
            try {
                selectedLocationConfigHolder = locationComboBox.getSelectionModel().getSelectedItem();
                if (selectedLocationConfigHolder == null) {
                    selectedLocationConfigHolder = ALL_LOCATION;
                }
            } catch (Exception ex) {
                selectedLocationConfigHolder = ALL_LOCATION;
                ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.ERROR);
            }

            // store selection to recover state after update
            try {
                selectedUnitConfigHolder = unitComboBox.getSelectionModel().getSelectedItem();
            } catch (Exception ex) {
                selectedUnitConfigHolder = null;
                ExceptionPrinter.printHistory(ex, LOGGER);
            }

            // update unit types
            try {
                ObservableList<UnitTemplateHolder> unitTemplateHolderList = FXCollections.observableArrayList();

                // apply service type filter if needed
                if (serviceTemplateComboBox.getSelectionModel().getSelectedItem() != null && !serviceTemplateComboBox.getSelectionModel().getSelectedItem().isNotSpecified()) {
                    unitTemplateHolderList.add(new UnitTemplateHolder(null));
                    final ServiceType serviceTypeFilter = serviceTemplateComboBox.getSelectionModel().getSelectedItem().getType();
                    for (final UnitTemplate unitTemplate : Registries.getTemplateRegistry().getUnitTemplates()) {
                        for (final ServiceDescription serviceDescription : unitTemplate.getServiceDescriptionList()) {
                            if (serviceDescription.getServiceType() == serviceTypeFilter) {
                                unitTemplateHolderList.add(new UnitTemplateHolder(unitTemplate));
                                break;
                            }
                        }
                    }
                } else {
                    for (UnitTemplate template : Registries.getTemplateRegistry().getUnitTemplates()) {
                        unitTemplateHolderList.add(new UnitTemplateHolder(template));
                    }
                }

                //Collections.sort(unitTemplateHolderList);

                unitTemplateComboBox.setDisable(!false);
                UnitTemplateHolder selectedItem = unitTemplateComboBox.getSelectionModel().getSelectedItem();
                unitTemplateComboBox.setItems(new SortedList<>(unitTemplateHolderList));
                unitTemplateComboBox.getSelectionModel().select(selectedItem);
                unitTemplateComboBox.setDisable(unitTemplateComboBox.getItems().size() <= 1);
            } catch (Exception ex) {
                unitTemplateComboBox.setDisable(!false);
                ExceptionPrinter.printHistory(ex, LOGGER);
            }

            // update service types
            try {

                // precompute location supported services
                final Set<ServiceType> locationSupportedServiceConfigList;
                if (!selectedLocationConfigHolder.isNotSpecified()) {
                    locationSupportedServiceConfigList = Registries.getUnitRegistry().getServiceTypesByLocation(selectedLocationConfigHolder.getConfig().getId());
                } else {
                    locationSupportedServiceConfigList = null;
                }

                // precompute unit supported services
                final Set<ServiceType> unitTypeSupportedServiceConfigList;
                if (unitTemplateComboBox.getSelectionModel().getSelectedItem() != null && !((UnitTemplateHolder) unitTemplateComboBox.getSelectionModel().getSelectedItem()).isNotSpecified()) {
                    unitTypeSupportedServiceConfigList = new TreeSet();
                    for (final ServiceDescription serviceDescription : Registries.getTemplateRegistry().getUnitTemplateByType(unitTemplateComboBox.getSelectionModel().getSelectedItem().getType()).getServiceDescriptionList()) {
                        unitTypeSupportedServiceConfigList.add(serviceDescription.getServiceType());
                    }
                } else {
                    unitTypeSupportedServiceConfigList = null;
                }

                ObservableList<ServiceTemplateHolder> serviceTemplateHolderList = FXCollections.observableArrayList();
                serviceTemplateHolderList.add(new ServiceTemplateHolder(null));
                for (ServiceTemplate template : Registries.getTemplateRegistry().getServiceTemplates()) {

                    // apply location type filter if needed
                    if (locationSupportedServiceConfigList != null && !locationSupportedServiceConfigList.contains(template.getType())) {
                        continue;
                    }

                    // apply unit type filter if needed
                    if (unitTypeSupportedServiceConfigList != null && !unitTypeSupportedServiceConfigList.contains(template.getType())) {
                        continue;
                    }

                    serviceTemplateHolderList.add(new ServiceTemplateHolder(template));
                }

                serviceTemplateComboBox.setDisable(!false);
                ServiceTemplateHolder selectedItem = serviceTemplateComboBox.getSelectionModel().getSelectedItem();
                serviceTemplateComboBox.setItems(new SortedList<>(serviceTemplateHolderList));
                serviceTemplateComboBox.getSelectionModel().select(selectedItem);
                serviceTemplateComboBox.setDisable(serviceTemplateComboBox.getItems().size() <= 1);
            } catch (Exception ex) {
                locationComboBox.setDisable(!false);
                ExceptionPrinter.printHistory(ex, LOGGER);
            }

            // update location types
            try {
                ObservableList<LocationUnitConfigHolder> locationConfigHolderList = FXCollections.observableArrayList();
                locationConfigHolderList.add(ALL_LOCATION);
                for (UnitConfig locationUnitConfig : Registries.getUnitRegistry().getUnitConfigs(UnitType.LOCATION)) {
                    locationConfigHolderList.add(new LocationUnitConfigHolder(locationUnitConfig));
                }

                locationComboBox.setDisable(!false);
                locationComboBox.setItems(new SortedList<>(locationConfigHolderList));
                locationComboBox.getSelectionModel().select(selectedLocationConfigHolder);
                locationComboBox.setDisable(locationComboBox.getItems().size() <= 1);

            } catch (CouldNotPerformException ex) {
                locationComboBox.setDisable(!false);
                ExceptionPrinter.printHistory(ex, LOGGER);
            }

            try {
                ObservableList<UnitConfigHolder> unitConfigHolderList = FXCollections.observableArrayList();
                UnitType selectedUnitType = unitTemplateComboBox.getSelectionModel().getSelectedItem().getType();
                ServiceType selectedServiceType = serviceTemplateComboBox.getSelectionModel().getSelectedItem().getType();

                // generate unit config list
                List<UnitConfig> selectedUnitConfigs = new ArrayList<>();
                if (selectedLocationConfigHolder != null && !selectedLocationConfigHolder.isNotSpecified()) {
                    selectedUnitConfigs.addAll(Registries.getUnitRegistry().getUnitConfigsByLocation(selectedLocationConfigHolder.getConfig().getId()));
                } else {
                    selectedUnitConfigs.addAll(Registries.getUnitRegistry().getUnitConfigs());
                }

                for (UnitConfig unitConfig : selectedUnitConfigs) {

                    // filter disabled units
                    if (unitConfig.getEnablingState().getValue() != EnablingState.State.ENABLED) {
                        continue;
                    }

                    if (unitConfig.getPlacementConfig().getLocationId().isEmpty()) {
                        LOGGER.warn("Could not load location unit of " + unitConfig.getLabel() + " because its location is not configured!");
                        continue;
                    }

                    // filter units by selections
                    if (selectedUnitType != UnitType.UNKNOWN) {
                        if (unitConfig.getUnitType() != selectedUnitType) {
                            continue;
                        }
                    }

                    // filter service by selections
                    if (selectedServiceType != ServiceType.UNKNOWN) {
                        boolean found = false;
                        for (final ServiceConfig serviceConfig : unitConfig.getServiceConfigList()) {
                            if (serviceConfig.getServiceDescription().getServiceType() == selectedServiceType) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            continue;
                        }
                    }

                    // generate config holder for unit
                    unitConfigHolderList.add(new UnitConfigHolder(unitConfig));
                }

                // sort units
                // setup model
                unitComboBox.setItems(new SortedList<>(unitConfigHolderList));
                if (selectedUnitConfigHolder != null) {
                    unitComboBox.getSelectionModel().select(selectedUnitConfigHolder);
                }
                unitComboBox.setDisable(unitConfigHolderList.size() <= 0);

                if (selectedUnitType == UnitType.LOCATION) {
                    locationComboBox.getSelectionModel().select(0);
                    locationComboBox.setDisable(!false);
                }

                // auto select
                if(locationComboBox.getSelectionModel().getSelectedIndex() == -1) {
                    locationComboBox.getSelectionModel().select(0);
                }
                if(unitTemplateComboBox.getSelectionModel().getSelectedIndex() == -1) {
                    unitTemplateComboBox.getSelectionModel().select(0);
                }
                if(serviceTemplateComboBox.getSelectionModel().getSelectedIndex() == -1) {
                    serviceTemplateComboBox.getSelectionModel().select(0);
                }
                if(unitComboBox.getSelectionModel().getSelectedIndex() == -1) {
                    unitComboBox.getSelectionModel().select(0);
                }
            } catch (CouldNotPerformException ex) {
                unitComboBox.setDisable(!false);
                throw ex;
            }
//            updateRemotePanel();
            MultiException.checkAndThrow(() -> "Could not acquire all information!", exceptionStack);
        } catch (CouldNotPerformException | NullPointerException ex) {

//        } catch (InterruptedException ex) {
//            ExceptionPrinter.printHistory(new CouldNotPerformException("Component update interrupted.", ex), LOGGER, LogLevel.WARN);
//            Thread.currentThread().interrupt();
        } finally {
            updateComponentLock.writeLock().unlock();
        }
    }

    public ReadOnlyStringProperty unitIdProperty() {
        return unitIdProperty;
    }

    public static class LocationUnitConfigHolder implements Comparable<LocationUnitConfigHolder> {

        private final UnitConfig locationUnitConfig;

        public LocationUnitConfigHolder(UnitConfig locationUnitConfig) {
            this.locationUnitConfig = locationUnitConfig;
        }

        @Override
        public String toString() {
            if (isNotSpecified()) {
                return "All";
            }
            return LabelProcessor.getBestMatch(locationUnitConfig.getLabel(), "?");
        }

        public boolean isNotSpecified() {
            return locationUnitConfig == null;
        }

        public UnitConfig getConfig() {
            return locationUnitConfig;
        }

        @Override
        public int compareTo(LocationUnitConfigHolder o) {
            if (o == null) {
                return -1;
            }

            // make sure "all" is on top.
            if (isNotSpecified()) {
                return -1;
            } else if (o.isNotSpecified()) {
                return +1;
            }

            return toString().compareTo(o.toString());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            LocationUnitConfigHolder instance = (LocationUnitConfigHolder) obj;

            // handle ALL entry
            if (locationUnitConfig == null && instance.locationUnitConfig == null) {
                return true;
            }
            if (locationUnitConfig == null || instance.locationUnitConfig == null) {
                return super.equals(obj);
            }

            return new EqualsBuilder()
                    .append(locationUnitConfig.getId(), instance.locationUnitConfig.getId())
                    .isEquals();
        }

        @Override
        public int hashCode() {

            // filter all entry location
            if (locationUnitConfig == null) {
                return super.hashCode();
            }

            return new HashCodeBuilder(17, 37).
                    append(locationUnitConfig.getId()).
                    toHashCode();
        }
    }

    public static class UnitTemplateHolder implements Comparable<UnitTemplateHolder> {

        private final UnitTemplate template;

        public UnitTemplateHolder(final UnitTemplate template) {
            this.template = template;
        }

        @Override
        public String toString() {
            if (getType().equals(UnitType.UNKNOWN)) {
                return "All";
            }
            return LabelProcessor.getBestMatch(template.getLabel(), "?");
        }

        public boolean isNotSpecified() {
            return getType().equals(UnitType.UNKNOWN);
        }

        public UnitTemplate getTemplate() {
            return template;
        }

        public UnitType getType() {
            if (template == null) {
                return UnitType.UNKNOWN;
            }
            return template.getType();
        }

        @Override
        public int compareTo(final UnitTemplateHolder o) {
            if (o == null) {
                return -1;
            }

            // make sure "all" is on top.
            if (getType().equals(UnitType.UNKNOWN)) {
                return -1;
            } else if (o.getType().equals(UnitType.UNKNOWN)) {
                return +1;
            }

            return toString().compareTo(o.toString());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            UnitTemplateHolder instance = (UnitTemplateHolder) obj;
            return new EqualsBuilder()
                    .append(getType(), instance.getType())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).
                    append(getType()).
                    toHashCode();
        }
    }

    public static class ServiceTemplateHolder implements Comparable<ServiceTemplateHolder> {

        private final ServiceTemplate template;

        public ServiceTemplateHolder(final ServiceTemplate template) {
            this.template = template;
        }

        @Override
        public String toString() {
            if (getType().equals(ServiceType.UNKNOWN)) {
                return "All";
            }
            return LabelProcessor.getBestMatch(template.getLabel(), "?");
        }

        public boolean isNotSpecified() {
            return getType().equals(ServiceType.UNKNOWN);
        }

        public ServiceType getType() {
            if (template == null) {
                return ServiceType.UNKNOWN;
            }
            return template.getType();
        }

        public ServiceTemplate getTemplate() {
            return template;
        }

        @Override
        public int compareTo(final ServiceTemplateHolder o) {
            if (o == null) {
                return -1;
            }

            // make sure "all" is on top.
            if (getType().equals(ServiceType.UNKNOWN)) {
                return -1;
            } else if (o.getType().equals(ServiceType.UNKNOWN)) {
                return +1;
            }

            return toString().compareTo(o.toString());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            ServiceTemplateHolder instance = (ServiceTemplateHolder) obj;
            return new EqualsBuilder()
                    .append(getType(), instance.getType())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).
                    append(getType()).
                    toHashCode();
        }
    }

    public static class UnitConfigHolder implements Comparable<UnitConfigHolder> {

        private final UnitConfig config;

        public UnitConfigHolder(final UnitConfig unitConfig) {
            this.config = unitConfig;
        }

        @Override
        public String toString() {
            if (isNotSpecified()) {
                return "Not Available";
            }

            String unitType = null;
            try {
                unitType = LabelProcessor.getBestMatch(Registries.getTemplateRegistry(false).getUnitTemplateByType(config.getUnitType()).getLabel(), "?");
            } catch (CouldNotPerformException | InterruptedException ex) {
                unitType = "?";
            }

            String label = LabelProcessor.getBestMatch(config.getLabel(), "?");

            String locationLabel = null;
            try {
                locationLabel = LabelProcessor.getBestMatch(Registries.getUnitRegistry(false).getUnitConfigById(config.getPlacementConfig().getLocationId(), UnitType.LOCATION).getLabel(), "?");
            } catch (CouldNotPerformException | InterruptedException ex) {
                unitType = "?";
            }

            final String description = MultiLanguageTextProcessor.getBestMatch(config.getDescription(), "");
            return unitType
                    + " = " + label + ""
                    + " @ " + locationLabel
                    + (description.isEmpty() ? "" : " (" + description + ")");
        }

        public boolean isNotSpecified() {
            return config == null;
        }

        public UnitConfig getConfig() {
            return config;
        }

        @Override
        public int compareTo(final UnitConfigHolder o) {
            if (o == null) {
                return -1;
            }
            return toString().compareTo(o.toString());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            UnitConfigHolder instance = (UnitConfigHolder) obj;
            return new EqualsBuilder()
                    .append(config.getId(), instance.config.getId())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).
                    append(config.getId()).
                    toHashCode();
        }
    }
}
