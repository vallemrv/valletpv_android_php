<?php


/**
 * Description of Articulos
 *
 * @author elvalle
 */

class Articulos  extends Registro{
    //put your code here
    public function tableName() {
        return 'teclas';
    }
    
    public function relations() {
        return array(
            "familia"=>array(self::BELONGS_TO, "Familias", "IDFamilia"),
            "seccion"=>array(self::MANY_MANY, 'Secciones', 'teclaseccion(IDSeccion, IDTecla)'),
            "comanda"=>array(self::HAS_MANY, 'TeclasCom', "IDTecla"),
            "sugs"=>array(self::HAS_MANY, 'Sugerencias', "IDTecla")
         );
    }
    
    public function update($attributes = null) {
        $sync = Sync::model()->find("Tabla='Secciones'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'Secciones';
        $sync->save();
        parent::update($attributes);
    }
    
    
    public function save($runValidation = true, $attributes = null) {
        $sync = Sync::model()->find("Tabla='Secciones'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'Secciones';
        $sync->save();
        parent::save($runValidation, $attributes);
    }
    
    public function delete() {
        $sync = Sync::model()->find("Tabla='Secciones'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'Secciones';
        $sync->save();
        parent::delete();
    }
    
    public function deleteAll($condition = '', $params = array()) {
        $sync = Sync::model()->find("Tabla='Secciones'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'Secciones';
        $sync->save();
        parent::deleteAll($condition, $params);
    }
    
    public function deleteByPk($pk, $condition = '', $params = array()) {
        $sync = Sync::model()->find("Tabla='Secciones'");
        if(!$sync) $sync = new Sync();
        $sync->Modificado = date("Y/m/d - H:i:s");
        $sync->Tabla = 'Secciones';
        $sync->save();
        
        parent::deleteByPk($pk, $condition, $params);
    }
   
}
