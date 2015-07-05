/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.visual.util;

import com.google.protobuf.GeneratedMessage;
import de.citec.dal.remote.unit.DALRemoteService;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InitializationException;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.NotAvailableException;
import de.citec.jul.pattern.Observable;
import de.citec.jul.pattern.Observer;
import de.citec.jul.processing.StringProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.unit.UnitConfigType;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.homeautomation.unit.UnitTemplateType;
import rst.rsb.ScopeType.Scope;

/**
 *
 * @author mpohling
 */
public abstract class RSBRemoteView extends javax.swing.JPanel implements Observer<GeneratedMessage> {
//public abstract class RSBRemoteView<M extends GeneratedMessage, R extends DALRemoteService<M>> extends javax.swing.JPanel implements Observer<M> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private DALRemoteService remoteService;

    /**
     * Creates new form RSBViewService
     */
    public RSBRemoteView() {
        this.initComponents();
    }

    private synchronized void setRemoteService(final DALRemoteService remoteService) {

        if (this.remoteService != null) {
            this.remoteService.shutdown();
        }

        this.remoteService = remoteService;
        remoteService.addObserver(this);
    }

    public synchronized void shutdown() {
        if (remoteService == null) {
            return;
        }

        remoteService.shutdown();
    }

    @Override
    public void update(Observable<GeneratedMessage> source, GeneratedMessage data) {
        updateDynamicComponents(data);
    }

    public DALRemoteService getRemoteService() throws NotAvailableException {
        if (remoteService == null) {
            throw new NotAvailableException("remoteService");
        }
        return remoteService;
    }
//    public M getData() throws CouldNotPerformException {
//        return getRemoteService().getData();
//    }
    public void setUnitRemote(final UnitTemplateType.UnitTemplate.UnitType unitType, final Scope scope) throws CouldNotPerformException, InterruptedException {
        logger.info("Setup unit remote: " + unitType + ".");
        try {
            Class<? extends DALRemoteService> remoteClass = loadUnitRemoteClass(unitType);
            DALRemoteService unitRemote = instantiatUnitRemote(remoteClass);
            initUnitRemote(unitRemote, scope);
            unitRemote.activate();
            setRemoteService(unitRemote);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not setup unit remote config!", ex);
        }
    }

    public void setUnitRemote(final UnitConfigType.UnitConfig unitConfig) throws CouldNotPerformException, InterruptedException {
        logger.info("Setup unit remote: " + unitConfig.getId());
        try {
            Class<? extends DALRemoteService> remoteClass = loadUnitRemoteClass(unitConfig.getTemplate().getType());
            DALRemoteService unitRemote = instantiatUnitRemote(remoteClass);
            initUnitRemote(unitRemote, unitConfig);
            unitRemote.activate();
            setRemoteService(unitRemote);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not setup unit remote config!", ex);
        }
    }

    private Class<? extends DALRemoteService> loadUnitRemoteClass(UnitTemplateType.UnitTemplate.UnitType unitType) throws CouldNotPerformException {
        String remoteClassName = DALRemoteService.class.getPackage().getName() + "." + StringProcessor.transformUpperCaseToCamelCase(unitType.name()) + "Remote";
        try {
            return (Class<? extends DALRemoteService>) getClass().getClassLoader().loadClass(remoteClassName);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not detect remote class for UnitType[" + unitType.name() + "]", ex);
        }
    }

    private DALRemoteService instantiatUnitRemote(Class<? extends DALRemoteService> remoteClass) throws InstantiationException {
        try {
            return remoteClass.newInstance();
        } catch (Exception ex) {
            throw new InstantiationException("Could not instantiate unit remote out of RemoteClass[" + remoteClass.getSimpleName() + "]!", ex);
        }
    }

    private void initUnitRemote(DALRemoteService unitRemote, UnitConfig config) throws CouldNotPerformException {
        try {
            unitRemote.init(config);
        } catch (InitializationException ex) {
            throw new CouldNotPerformException("Could not init " + unitRemote + "!", ex);
        }
    }

    private void initUnitRemote(DALRemoteService unitRemote, Scope scope) throws CouldNotPerformException {
        try {
            unitRemote.init(scope);
        } catch (InitializationException ex) {
            throw new CouldNotPerformException("Could not init " + unitRemote + "!", ex);
        }
    }

    protected abstract void updateDynamicComponents(GeneratedMessage data);

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
