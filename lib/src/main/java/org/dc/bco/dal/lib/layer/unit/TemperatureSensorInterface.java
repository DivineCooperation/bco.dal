/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.dal.lib.layer.unit;

import org.dc.bco.dal.lib.layer.service.provider.TemperatureAlarmStateProvider;
import org.dc.bco.dal.lib.layer.service.provider.TemperatureProvider;

/**
 *
 * @author thuxohl
 */
public interface TemperatureSensorInterface extends TemperatureProvider, TemperatureAlarmStateProvider {
    
}