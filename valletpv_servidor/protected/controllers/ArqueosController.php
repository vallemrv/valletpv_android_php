<?php

/**
 * Description of ArqueosController
 *
 * @author valle
 */
error_reporting(E_ALL);
ini_set('display_errors', '1');

class ArqueosController extends CController{
    
  
     //put your code here

    public function actionGetCambio(){
        $condicion = new CDbCriteria();
        $condicion->order ="ID DESC";
        $arqueo = Arqueos::model("Arqueos")->find($condicion);
        if($arqueo){
            echo $arqueo->Cambio;
        }else{
            echo "0.00";
        }
    }
    
    public function actionArquear(){
        
        $efectivo = $_POST["efectivo"];
        $cambio = $_POST["cambio"];
        $gastos = $_POST["gastos"];
        $ef = json_decode($_POST["des_efectivo"]);
        $gas = json_decode($_POST["des_gastos"]);
        
        ignore_user_abort(true);
        set_time_limit(0);
        ob_start();
         // do initial processing here
        echo 'success';           // send the response
        header('Connection: close');
        header('Content-Length: '.ob_get_length());
        ob_end_flush();
        ob_flush();
        flush();
        
        $condicion = new CDbCriteria();
        $condicion->order= "ID DESC";
        $cierre = CierresCaja::model("CierresCaja")->find($condicion);
        if(!$cierre){
            $c = new CierresCaja();
            $c->TicketCom = Ticket::model("Ticket")->find()->ID;
            $c->TicketFinal = Ticket::model("Ticket")->find($condicion)->ID;
            
        } else {
            $c = new CierresCaja();
            $c->TicketCom = $cierre->TicketFinal+1;
            $c->TicketFinal = Ticket::model("Ticket")->find($condicion)->ID;
        }
        $c->Hora = date("H:m");
        $c->Fecha = date("Y/m/d");
        $c->save();
        $total = Yii::app()->db->createCommand()
                 ->select('SUM( Precio ) AS Total')
                 ->from('ticket')
                 ->join("ticketlineas", 'ticket.ID=ticketlineas.IDTicket')
                 ->join('lineaspedido', 'lineaspedido.ID=ticketlineas.IDLinea')
                 ->where("ticket.ID >= ".$c->TicketCom. " AND ticket.ID <= ".$c->TicketFinal ." AND ticket.Entrega > 0")
                 ->queryScalar();
         
         $arqueo = new Arqueos();
         $arqueo->IDCierre = $c->ID;
         $arqueo->Cambio = $cambio;
         $arqueo->Descuadre = (($efectivo+$gastos)-$cambio)-$total;
         $arqueo->save();
         foreach ($ef as $f){
             $lef = new Efectivo();
             $lef->IDArqueo = $arqueo->ID;
             $lef->Can = $f->Can;
             $lef->Moneda = $f->Moneda;
             $lef->save();
         }
         foreach ($gas as $g){
             $lg = new Gastos();
             $lg->IDArqueo = $arqueo->ID;
             $lg->Descripcion = $g->Des;
             $lg->Importe = $g->Importe;
             $lg->save();
         }
         
          
         GesImpresion::ImprimirCambio(Yii::app()->params["empresa"], $cambio);
         GesImpresion::ImprimirRetirada(Yii::app()->params["empresa"], $arqueo->getDesRetirada());
         foreach (Usuarios::getUsuariosMail() as $usr)
                   Mails::sendCierreCaja($usr, $arqueo->getDesgloseCierre());
         
    }
    
    
    /*public function actionRF(){
        $condicion = new CDbCriteria();
        $condicion->addCondition("ID > 1497");
        $cierres = CierresCaja::model("CierresCaja")->findAll($condicion);
        foreach ($cierres as $c){
           $fecha = split("/", $c->Fecha);
           $src = $fecha[0]."/".$fecha[2]."/".$fecha[1];
           $c->Fecha = $src;
           $c->update();
        }
      var_dump($cierres);
    }*/
}
