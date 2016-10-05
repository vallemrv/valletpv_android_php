<?php


/**
 * Description of Pedidos
 *
 * @author elvalle
 */
class Pedidos extends Registro {
   
    
    //put your code here
    public function tableName() {
        return 'pedidos';
    }
    
    public function relations() {
        return array(
            "cam"=>array(self::BELONGS_TO, "Camareros", "IDCam")
        );
    }
    
    public function GetInf($abierta=true){
        $mesa = new Mesas();
        if($abierta){
           $m = MesasAbiertas::model()->find("UID='".$this->UID."'");
           $mesa = $m->Mesa;
        }else{
          list($id,$uid) =  explode("-", $this->UID);
          $mesa = Mesas::model()->findByPk($id);
        }
         
        $camarero  = Camareros::model("Camareros")->findByPk($this->IDCam);
        return array("Nombre"=>$camarero->Nombre." ".$camarero->Apellidos,"Hora"=>$this->Hora,"Mesa"=>$mesa->Nombre);
    }
    
   
    
   
}
