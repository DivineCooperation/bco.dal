/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.hal.device;

import de.citec.dal.data.Location;
import de.citec.dal.hal.service.ServiceFactory;
import de.citec.jul.rsb.ScopeProvider;

/**
 *
 * @author Divine <DivineThreepwood@gmail.com>
 */
public interface DeviceInterface extends ScopeProvider{

    public String getId();

    public String getName();

	public String getLabel();

    public Location getLocation();
    
    public ServiceFactory getDefaultServiceFactory();
}
