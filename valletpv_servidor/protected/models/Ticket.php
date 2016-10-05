<?php

/**
 * Description of HistorialNulo
 *
 * @author valle
 */
class Ticket extends Registro{
    //put your code here
    public function tableName() {
        return 'ticket';
    }
    
    public function relations() {
        return array(
          "Cam"=>array(self::BELONGS_TO,"Camareros","IDCam")  
        );
    }
}
