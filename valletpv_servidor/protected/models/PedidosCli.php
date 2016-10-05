<?php
/**
 * Description of PedidosCam
 *
 * @author elvalle
 */
class PedidosCli extends Registro {
    //put your code here
    public function tableName() {
        return 'clipedidos';
    }
    
    public function relations() {
        return array(
            'cliente'=>array(self::HAS_MANY, "Clientes", "IDCliente")
        );
    }
}
