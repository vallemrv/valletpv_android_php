<?php

/**
 * Description of RefrescoController
 *
 * @author valle
 */
class SyncController extends CController {
    //put your code here
    public function actionGetUpdate(){
        $hora = isset($_POST["hora"]) ? $_POST["hora"] : "";
        if($hora=="") $res = array("hora"=>date("Y/m/d - H:i:s"),"Tablas"=>array(array("Tabla"=>"Camareros"),
                                array("Tabla"=>"Zonas"), array("Tabla"=>"Secciones"),  array("Tabla"=>"MesasAbiertas"),
                                                               array("Tabla"=>"SubTeclas"), array("Tabla"=>"TeclasCom")));

        else{
          $sync = new Sync();
          $condicion = new CDbCriteria();
          $condicion->addCondition("Modificado>='$hora'");
          $condicion->group = "Tabla";
          $condicion->order = "Modificado DESC";
          $res = array("hora"=>date("Y/m/d - H:i:s"),"Tablas"=>$sync->queryAllCDb($condicion));
        }
        echo json_encode($res);
        exit();
    }
}
