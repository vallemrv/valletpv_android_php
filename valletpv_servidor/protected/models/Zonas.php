<?php

/**
 * Description of Zonas
 *
 * @author elvalle
 */
class Zonas extends Registro{
   
    //put your code here
    public function tableName() {
        return 'zonas';
    }
    
    public function relations() {
        return array(
           "mesas" => array(self::MANY_MANY, 'Mesas', 'mesaszona(IDZona, IDMesa)','order'=>'mesas.Orden DESC, mesas.ID',)
            );
    }
    
    public function update($attributes = null) {
        $sync = Sync::model()->find("Tabla='Zonas'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'Zonas';
        $sync->save();
        parent::update($attributes);
    }
    
    
    public function save($runValidation = true, $attributes = null) {
        $sync = Sync::model()->find("Tabla='Zonas'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'Zonas';
        $sync->save();
        parent::save($runValidation, $attributes);
    }
    
    public function delete() {
        $sync = Sync::model()->find("Tabla='Zonas'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'Zonas';
        $sync->save();
        parent::delete();
    }
    
    public function deleteAll($condition = '', $params = array()) {
        $sync = Sync::model()->find("Tabla='Zonas'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'Zonas';
        $sync->save();
        parent::deleteAll($condition, $params);
    }
    
    public function deleteByPk($pk, $condition = '', $params = array()) {
        $sync = Sync::model()->find("Tabla='Zonas'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'Zonas';
        $sync->save();
        parent::deleteByPk($pk, $condition, $params);
    }
   
}
