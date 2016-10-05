<?php


/**
 * Description of Arqueos
 *
 * @author valle
 */
class Arqueos extends Registro{
    var $Fecha;
    var $Hora;
    var $TicketCom;
    var $TicketFinal;
    
    
    //put your code here
    public function tableName() {
        return "arqueocaja";
    }
    
    public function relations() {
        return array(
            "gastos"=>array(self::HAS_MANY, "Gastos", "IDArqueo"),
            "efectivo"=>array(self::HAS_MANY, "Efectivo", "IDArqueo", "select"=>"*, Can*moneda AS SubTotal", "group"=>"moneda", "order"=>"moneda DESC"),
            "cierre"=>array(self::BELONGS_TO, "CierresCaja", "IDCierre")
        );
    }
    
    public function getEfectivo(){
        return  Yii::app()->db->createCommand()
                 ->select('SUM(tr.subTotal) AS Total')
                 ->from('(SELECT Can*moneda AS subTotal FROM efectivo WHERE IDArqueo='.$this->ID.") AS tr")
                 ->queryScalar() - $this->Cambio;
    }
    
    public function getGastos(){
        return  Yii::app()->db->createCommand()
                 ->select('SUM(Importe) AS Total')
                 ->from("gastos")
                 ->where("IDArqueo=".$this->ID)
                 ->queryScalar();
    }
    
    public function getDesRetirada(){
       $efectivo = $this->getEfectivo();
       $des = array();
       $retirar = 0;
        foreach ($this->efectivo as $efc){
           if($retirar < $efectivo){
               if($efc->SubTotal+$retirar<=$efectivo) {
                    $retirar+=$efc->SubTotal;  $a=$efc->attributes;
                    $des[]=$a;          
                  }else{
                    $parcial =  $efectivo-$retirar;
                    $can = floor($parcial/$efc->Moneda);
                    if($can>0){
                      $a=$efc->attributes;  
                      $a["Can"]= $can;$retirar+=($can*$efc->Moneda);
                      $des[]=$a;          
                    }
                  }
                }
           }
           return $des;
    }
    
    public function getDesgloseCierre(){
       $arqueos = $this;
       $ex =  $arqueos->attributes;
       $ex["gastos"] = $arqueos->getGastos();
       $ex["Fecha"] = $arqueos->cierre->Fecha." - ".$arqueos->cierre->Hora;
       $ex["TotalTarjeta"]= $arqueos->cierre->getTotalTarjeta();
       $ex["TotalEfectivo"]= $arqueos->cierre->getTotalEfectivo();
       $ex["TotalCaja"] = $ex["TotalEfectivo"] + $ex["TotalTarjeta"];
       $ex["CajaReal"] = $arqueos->getEfectivo()+$ex["TotalTarjeta"]+$ex["gastos"];
       $ex["des_ventas"]= $arqueos->cierre->getDesgloseVentas();
       $ex["des_gastos"] = array();
       foreach($arqueos->gastos as $g)  $ex["des_gastos"][]= $g->attributes;
       return $ex;
    }
    
}
