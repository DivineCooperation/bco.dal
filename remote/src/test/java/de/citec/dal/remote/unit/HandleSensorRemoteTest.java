/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.remote.unit;

import de.citec.dal.registry.MockRegistry;
import de.citec.dal.DALService;
import de.citec.dal.data.Location;
import de.citec.dal.hal.unit.HandleSensorController;
import de.citec.jps.core.JPService;
import de.citec.jps.properties.JPHardwareSimulationMode;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InitializationException;
import de.citec.jul.exception.InvalidStateException;
import de.citec.jul.exception.NotAvailableException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.LoggerFactory;
import rst.homeautomation.state.OpenClosedTiltedType;

/**
 *
 * @author thuxohl
 */
public class HandleSensorRemoteTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HandleSensorRemoteTest.class);

    private static HandleSensorRemote handleSensorRemote;
    private static DALService dalService;
    private static MockRegistry registry;
    private static Location location;
    public static String label;

    public HandleSensorRemoteTest() {
    }

    @BeforeClass
    public static void setUpClass() throws InitializationException, InvalidStateException, de.citec.jul.exception.InstantiationException, CouldNotPerformException {
        JPService.registerProperty(JPHardwareSimulationMode.class, true);
        registry = new MockRegistry();
        
        dalService = new DALService();
        dalService.activate();

        location = new Location(registry.getLocation());
        String label = MockRegistry.HANDLE_SENSOR_LABEL;

        handleSensorRemote = new HandleSensorRemote();
        handleSensorRemote.init(label, location);
        handleSensorRemote.activate();
    }

    @AfterClass
    public static void tearDownClass() throws CouldNotPerformException, InterruptedException {
        dalService.shutdown();
        try {
            handleSensorRemote.deactivate();
        } catch (InterruptedException ex) {
            logger.warn("Could not deactivate handle sensor remote: ", ex);
        }
        registry.shutdown();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of notifyUpdated method, of class HandleSensorRemote.
     */
    @Ignore
    public void testNotifyUpdated() {
    }

    /**
     * Test of getRotaryHandleState method, of class HandleSensorRemote.
     *
     * @throws java.lang.Exception
     */
    @Test(timeout = 3000)
    public void testGetRotaryHandleState() throws Exception {
        System.out.println("getRotaryHandleState");
        OpenClosedTiltedType.OpenClosedTilted.OpenClosedTiltedState state = OpenClosedTiltedType.OpenClosedTilted.OpenClosedTiltedState.TILTED;
        ((HandleSensorController) dalService.getUnitRegistry().getUnit(label, location, HandleSensorController.class)).updateHandle(state);
        while (true) {
            try {
                if (handleSensorRemote.getHandle().equals(state)) {
                    break;
                }
            } catch (NotAvailableException ex) {
                logger.debug("Not ready yet");
            }
            Thread.yield();
        }
        assertTrue("The getter for the handle state returns the wrong value!", handleSensorRemote.getHandle().equals(state));
    }
}
