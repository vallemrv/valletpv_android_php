<?php
/**
 * Representa un registro de la tabla receptores
 * @author vallesoft.es
 */
class Receptores  extends Registro{
    
    public function tableName() {
        return 'receptores';
    }
    
    ///Relaciones entre tablas
     public function relations() {
         return array(
              "familias" => array(self::HAS_MANY, 'Familias', 'IDReceptor'),
         );
     }
    
    
}

?>
