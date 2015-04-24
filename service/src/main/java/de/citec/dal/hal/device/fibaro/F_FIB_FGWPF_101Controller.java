/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.dal.hal.device.fibaro;

import de.citec.dal.bindings.openhab.AbstractOpenHABDeviceController;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.CouldNotTransformException;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.homeautomation.device.fibaro.F_FIB_FGWPF_101Type;
import de.citec.jul.exception.InstantiationException;
import rst.homeautomation.device.DeviceConfigType;

/**
 *
 * @author thuxohl
 */
public class F_FIB_FGWPF_101Controller extends AbstractOpenHABDeviceController<F_FIB_FGWPF_101Type.F_FIB_FGWPF_101, F_FIB_FGWPF_101Type.F_FIB_FGWPF_101.Builder> {

	static {
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(F_FIB_FGWPF_101Type.F_FIB_FGWPF_101.getDefaultInstance()));
	}

	public F_FIB_FGWPF_101Controller(final DeviceConfigType.DeviceConfig config) throws InstantiationException, CouldNotTransformException {
		super(config, F_FIB_FGWPF_101Type.F_FIB_FGWPF_101.newBuilder());
		try {
			registerUnits(config.getUnitConfigList());
		} catch (CouldNotPerformException ex) {
			throw new InstantiationException(this, ex);
		}
	}
}
