<?php
/**
 * Description of LinesaPedido
 *
 * @author elvalle
 */
class LineasPedido extends Registro {
    //put your code here
    public $Can;
    public $Nombre;
    public function precio(){
        return $this->Can * $this->Articulo->getPrecio($this->Tarifa);
    }
    
    public function tableName() {
        return 'lineaspedido';
    }
    
    public function relations() {
        return array(
           'Articulo'=>array(self::BELONGS_TO, 'Articulos', 'IDArt') ,
           "combo"=>array(self::MANY_MANY, "Articulos", 'articulos(IDComb, IDRefresco)')
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
