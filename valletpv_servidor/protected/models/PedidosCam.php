<?php
/**
 * Description of PedidosCam
 *
 * @author elvalle
 */
class PedidosCam extends Registro {
    //put your code here
    public function tableName() {
        return 'campedidos';
    }
    
    public function relations() {
        return array(
            'camarero'=>array(self::BELONGS_TO, "Camareros", "IDCam")
        );
    }
}
