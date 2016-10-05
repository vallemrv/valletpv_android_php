<?php

/**
 * Description of VentasController
 *
 * @author valle
 */
class VentasController extends CController{
    
    public function actionIndex(){
        echo 'success';
    }
    //put your code here
    public function actionVentasArt(){
        $nom = $_POST["nombre"];
        $art =$_POST["art"];
        $fdesde = Utilidades::RotarFecha($_POST["fdesde"]);
        $fhasta = Utilidades::RotarFecha($_POST["fhasta"]);
        $hdesde = $_POST["hdesde"];
        $hhasta = $_POST["hhasta"];
        $condicion = new CDbCriteria();
        $where = "";
        if($art!=""){
            $num = count($art);
            for($i=0;$i<$num;$i++){
                $op = $i<$num-1 ? "OR" : "AND";
                $condicion->addCondition ("IDArt=".$art[$i], "OR");
                $where = $where. "IDArt=".$art[$i] . ($i<$num-1 ? " OR ": "");
            }
        }else{
            $where = "Nombre LIKE '%$nom%'";
            $condicion->addSearchCondition("Nombre", $nom);
        }
        $condicion->addBetweenCondition("Fecha", $fdesde, $fhasta);
        $condicion->addBetweenCondition("Hora", $hdesde, $hhasta);
        $condicion->group=("IDArt, Estado, Nombre");
        $condicion->select =("COUNT(IDArt) as Can, Nombre, Estado");
        $condicion->join = "INNER JOIN infmesa ON t.UID=infmesa.UID";
        $condicion->order = "t.Estado, t.Nombre";
        $lineas = LineasPedido::model()->findAll($condicion);
        $exit = array();
        
        foreach ($lineas as $linea){
            $a = array();
            $a["Can"] = $linea->Can;
            $a["Nombre"] = $linea->Nombre;
            $a["Estado"] = $linea->Estado;
            $a["Motivo"] = array();
            if($linea->Estado=="A"){
                $a["Motivo"] =  Yii::app()->db->createCommand()
                ->select('COUNT(Motivo) as Can, Motivo')
                ->from('historialnulos as n')
                ->join("lineaspedido as l", "l.ID=n.IDLPedido")
                ->join("infmesa as inf", "l.UID=inf.UID")
                ->where( "(Estado='A') AND ($where) AND (Nombre = '$linea->Nombre') AND (Fecha BETWEEN '$fdesde' AND '$fhasta') AND (n.Hora BETWEEN '$hdesde' AND '$hhasta')")
                ->group('n.Motivo')    
                ->queryAll();
            }
            $exit[] = $a;
        }
        echo json_encode($exit);
    }
    
    public function actionVentasArq(){
        $fdesde = Utilidades::RotarFecha($_POST["fdesde"]);
        $fhasta = Utilidades::RotarFecha($_POST["fhasta"]);
        $hdesde = $_POST["hdesde"];
        $hhasta = $_POST["hhasta"];
        $condicion = new CDbCriteria();
        $where = "";
        
        $condicion->addBetweenCondition("cierrecaja.Fecha", $fdesde, $fhasta);
        if($hdesde<$hhasta){
           $condicion->addBetweenCondition("cierrecaja.Hora", $hdesde, $hhasta);
        }else{
          $condicion->addCondition("((cierrecaja.Hora  BETWEEN  '$hdesde' AND '23:59') OR (cierrecaja.Hora BETWEEN '00:00' AND '$hhasta'))");
        }
        $condicion->select =("t.ID, cierrecaja.Fecha, cierrecaja.Hora, cierrecaja.TicketCom, cierrecaja.TicketFinal,  t.Descuadre, t.Cambio");
        $condicion->join = "INNER JOIN cierrecaja ON t.IDCierre=cierrecaja.ID";
        $condicion->order = "cierrecaja.Fecha DESC, cierrecaja.Hora DESC";
        $lineas = Arqueos::model()->findAll($condicion);
        $exit = array();
        
        foreach ($lineas as $linea){
            $a = array();
            $a["ID"] = $linea->ID;
            $a["Fecha"] = Utilidades::RotarFecha($linea->Fecha);
            $a["Hora"] = $linea->Hora;
            $a["Descuadre"] = $linea->Descuadre;
            $a["Total"] = Yii::app()->db->createCommand()
                 ->select('SUM( Precio ) AS Total')
                 ->from('ticket')
                 ->join("ticketlineas", 'ticket.ID=ticketlineas.IDTicket')
                 ->join('lineaspedido', 'lineaspedido.ID=ticketlineas.IDLinea')
                 ->where("ticket.ID >= ".$linea->TicketCom. " AND ticket.ID <= ".$linea->TicketFinal )
                 ->queryScalar();
            $exit[] = $a;
        }
        echo json_encode($exit);
    }
  
}
