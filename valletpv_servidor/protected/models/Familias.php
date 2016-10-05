<?php
/**
 * Representa un registro de la tabla familias
 * @author vallesoft.es
 */
class Familias extends  Registro {
    public function tableName() {
        return 'familias';
    }
    
     public function relations()
    {
        return array(
            'Receptor'=>array(self::BELONGS_TO, 'Receptores', 'IDReceptor'),
        );
    }
}

?>
