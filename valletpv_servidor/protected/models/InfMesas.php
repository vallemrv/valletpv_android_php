<?php

/**
 * Description of InfMesas
 *
 * @author elvalle
 */
class InfMesas extends Registro {
    //put your code here
    public function tableName() {
        return 'infmesa';
    }
    
    public function relations() {
       return array("Cam"=>array(self::BELONGS_TO,"Camareros","IDCam"));
    }
    
    public function update($attributes = null) {
        $sync = Sync::model()->find("Tabla='MesasAbiertas'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'MesasAbiertas';
        $sync->save();
        parent::update($attributes);
    }
    
}
