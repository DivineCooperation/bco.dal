/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.dal.lib.binding;

/**
 *
 * @author mpohling
 */
public class AbstractDALBinding implements Binding {

    @Override
    public Class<? extends Binding> getId() {
        return getClass();
    }    
}