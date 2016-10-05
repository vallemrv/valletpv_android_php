<?php

/**
 * Description of SeccionesCom
 *
 * @author valle
 */
class SeccionesCom  extends Registro{
    //put your code here
    public function tableName() {
        return "secciones_com";
    }
    
    public function relations() {
        return array(
          "articulos"=>array(self::MANY_MANY,"Articulos","teclascom(IDTecla, IDSeccion)", "order"=>"articulos_articulos.Orden DESC")  
        );
        
    }
}
