<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of GastosController
 *
 * @author valle
 */
class GastosController extends CController {
    //put your code here
    function  actionConsultaRapida(){
        $nom = $_POST["nombre"];
        $fdesde = Utilidades::RotarFecha($_POST["fdesde"]);
        $fhasta = Utilidades::RotarFecha($_POST["fhasta"]);
        $hdesde = $_POST["hdesde"];
        $hhasta = $_POST["hhasta"];
        $condicion = new CDbCriteria();
        $condicion->addSearchCondition("t.Descripcion", $nom);
        $condicion->addBetweenCondition("cierrecaja.Fecha", $fdesde, $fhasta);
        $where = "(gastos.Descripcion LIKE '%$nom%') AND (cierrecaja.Fecha BETWEEN '$fdesde' AND '$fhasta') AND ";
        if($hdesde<$hhasta){
           $condicion->addBetweenCondition("cierrecaja.Hora", $hdesde, $hhasta);
           $where = $where . "(cierrecaja.Hora BETWEEN '$hdesde' AND '$hhasta') "; 
        }else{
          $condicion->addCondition("((cierrecaja.Hora  BETWEEN  '$hdesde' AND '23:59') OR (cierrecaja.Hora BETWEEN '00:00' AND '$hhasta'))");
           $where = $where . "((cierrecaja.Hora BETWEEN '$hdesde' AND '23:59') OR (cierrecaja.Hora BETWEEN '00:00' AND '$hhasta') ) "; 
        }
        
        $condicion->select = "cierrecaja.Fecha, cierrecaja.Hora, t.* ";
        $condicion->join = "INNER JOIN arqueocaja ON t.IDArqueo=arqueocaja.ID INNER JOIN cierrecaja ON cierrecaja.ID=arqueocaja.IDCierre";
        $condicion->order = "cierrecaja.Fecha Desc";
        $lineas = Gastos::model()->findAll($condicion);
        $total = $a["Total"] = Yii::app()->db->createCommand()
                 ->select('SUM( Importe ) AS Total')
                 ->from('gastos')
                 ->join("arqueocaja", 'gastos.IDArqueo=arqueocaja.ID')
                 ->join('cierrecaja', 'cierrecaja.ID=arqueocaja.IDCierre')
                 ->where($where)
                 ->queryScalar();
        echo json_encode(array("lineas"=>$lineas, "total"=>$total));
        
        
    }
    
}
