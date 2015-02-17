/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.hal.al;

import de.citec.dal.DALService;
import de.citec.dal.data.Location;
import de.citec.dal.exception.DALException;
import de.citec.dal.hal.device.homematic.HM_RotaryHandleSensorController;
import de.citec.dal.hal.unit.HandleSensorController;
import de.citec.dal.util.DALRegistry;
import de.citec.jps.core.JPService;
import de.citec.jps.properties.JPHardwareSimulationMode;
import de.citec.jul.exception.NotAvailableException;
import de.citec.jul.exception.VerificationFailedException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.LoggerFactory;
import rst.homeautomation.states.OpenClosedTiltedType;

/**
 *
 * @author thuxohl
 */
public class HandleSensorRemoteTest {

    private static final Location LOCATION = new Location("paradise");
    public static final String LABEL = "Handle_Sensor_Unit_Test";

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HandleSensorRemoteTest.class);

    private static HandleSensorRemote handleSensorRemote;
    private static DALService dalService;

    public HandleSensorRemoteTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        JPService.registerProperty(JPHardwareSimulationMode.class, true);
        dalService = new DALService(new TestConfiguration());
        dalService.activate();

        handleSensorRemote = new HandleSensorRemote();
        handleSensorRemote.init(LABEL, LOCATION);
        handleSensorRemote.activate();
    }

    @AfterClass
    public static void tearDownClass() {
        dalService.deactivate();
        try {
            handleSensorRemote.deactivate();
        } catch (InterruptedException ex) {
            logger.warn("Could not deactivate handle sensor remote: ", ex);
        }
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
        ((HandleSensorController) dalService.getRegistry().getUnit(LABEL, LOCATION, HandleSensorController.class)).updateHandle(state);
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

    public static class DeviceInitializerImpl implements de.citec.dal.util.DeviceInitializer {

        @Override
        public void initDevices(final DALRegistry registry) {

            try {
                registry.register(new HM_RotaryHandleSensorController("HM_RotaryHandleSensor_000", LABEL, LOCATION));
            } catch (de.citec.jul.exception.InstantiationException | VerificationFailedException | DALException ex) {
                logger.warn("Could not initialize unit test device!", ex);
            }
        }
    }
}