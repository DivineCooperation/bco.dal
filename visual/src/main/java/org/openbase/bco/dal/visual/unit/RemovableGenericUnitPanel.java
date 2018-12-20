package org.openbase.bco.dal.visual.unit;

/*
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.openbase.bco.dal.remote.layer.unit.AbstractUnitRemote;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.pattern.ObservableImpl;
import org.openbase.jul.pattern.Observer;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

/**
 *
 * * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RemovableGenericUnitPanel extends GenericUnitPanel<AbstractUnitRemote> {

    private final ObservableImpl<Object, String> removedObservable;
    private String mapId;
    private UnitType filteredUnitType = UnitType.UNKNOWN;

    /**
     * Creates new form TestPanel
     */
    public RemovableGenericUnitPanel() {
        removedObservable = new ObservableImpl<>();
        initComponents();
    }

    public void init(String mapId, boolean unitTypeFilter) {
        this.mapId = mapId;

        if (unitTypeFilter) {
            List<UnitType> unitTypeList = new ArrayList<>();
            unitTypeList.addAll(Arrays.asList(UnitType.values()));
            Collections.sort(unitTypeList, (UnitType o1, UnitType o2) -> o1.name().compareTo(o2.name()));
            unitTypeComboBox.setModel(new DefaultComboBoxModel(unitTypeList.toArray()));
            unitTypeComboBox.setSelectedItem(filteredUnitType);
        } else {
            unitTypeComboBox.setVisible(unitTypeFilter);
        }
    }

    public String getMapId() {
        return mapId;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        genericUnitPanel = new org.openbase.bco.dal.visual.unit.GenericUnitPanel();
        removeButton = new javax.swing.JButton();
        unitTypeComboBox = new javax.swing.JComboBox();

        removeButton.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
        removeButton.setText("X");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        unitTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        unitTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unitTypeComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(unitTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeButton)
                .addGap(6, 6, 6))
            .addComponent(genericUnitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeButton)
                    .addComponent(unitTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 21, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genericUnitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        try {
            removedObservable.notifyObservers(mapId);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory(ex, logger, LogLevel.WARN);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void unitTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unitTypeComboBoxActionPerformed
        filteredUnitType = (UnitType) unitTypeComboBox.getSelectedItem();
    }//GEN-LAST:event_unitTypeComboBoxActionPerformed

    public void addObserver(Observer<Object, String> removedObserver) {
        removedObservable.addObserver(removedObserver);
    }

    public void removeObserver(Observer<Object, String> removedObserver) {
        removedObservable.removeObserver(removedObserver);
    }

    public GenericUnitPanel getGenericUnitPanel() {
        return genericUnitPanel;
    }

    @Override
    public void updateUnitConfig(UnitConfig unitConfig, ServiceType serviceType) throws CouldNotPerformException, InterruptedException {
        genericUnitPanel.updateUnitConfig(unitConfig, serviceType);
    }

    @Override
    public void updateUnitConfig(UnitConfig unitConfig, ServiceType serviceType, Object serviceAttribute) throws CouldNotPerformException, InterruptedException {
        genericUnitPanel.updateUnitConfig(unitConfig, serviceType, serviceAttribute);
    }

    public UnitType getSelectedUnitType() {
        return filteredUnitType;
    }

    public void selectType(UnitType type) {
        unitTypeComboBox.setSelectedItem(type);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openbase.bco.dal.visual.unit.GenericUnitPanel genericUnitPanel;
    private javax.swing.JButton removeButton;
    private javax.swing.JComboBox unitTypeComboBox;
    // End of variables declaration//GEN-END:variables
}
