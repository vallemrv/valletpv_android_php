<?php

/**
 * Description of MesasAbiertas
 *
 * @author elvalle
 */
class MesasAbiertas extends Registro{
    //put your code here
    public function tableName() {
        return 'mesasabiertas';
    }
    
    public function relations() {
        return array(
          'InfMesa'=>array(self::BELONGS_TO, 'InfMesas', 'UID'),  
          'Mesa'=>array(self::BELONGS_TO, 'Mesas', 'IDMesa')  
        );
    }
    
    
    public function update($attributes = null) {
        $sync = Sync::model()->find("Tabla='MesasAbiertas'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'MesasAbiertas';
        $sync->save();
        parent::update($attributes);
    }
    
    
    public function save($runValidation = true, $attributes = null) {
        $sync = Sync::model()->find("Tabla='MesasAbiertas'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'MesasAbiertas';
        $sync->save();
        parent::save($runValidation, $attributes);
    }
    
    public function delete() {
        $sync = Sync::model()->find("Tabla='MesasAbiertas'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'MesasAbiertas';
        $sync->save();
        parent::delete();
    }
    
    public function deleteAll($condition = '', $params = array()) {
        $sync = Sync::model()->find("Tabla='MesasAbiertas'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'MesasAbiertas';
        $sync->save();
        parent::deleteAll($condition, $params);
    }
    
}
