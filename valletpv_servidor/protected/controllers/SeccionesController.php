<?php

/**
 * Clase para la gestion de secciones del tpv
 *
 * @author elvalle
 */
class SeccionesController  extends CController{
    //put your code here
    
    public function actionListado(){
        $lstSecciones = array();
        $condicion = new CDbCriteria();
        $condicion->order="Orden DESC";
        
        $secciones = Secciones::model("Secciones")->findAll($condicion);
        foreach ($secciones as $sec)
             $lstSecciones[] = $sec->attributes;
        
        echo json_encode($lstSecciones);
    }
    
    
    public function actionLsSeccionesCom(){
        $lstSecciones = array();
        
        $secciones = SeccionesCom::model("SeccionesCom")->findAll();
        foreach ($secciones as $sec)
             $lstSecciones[] = $sec->attributes;
        
        echo json_encode($lstSecciones);
    }
    
    public function actionAdd(){
      try{
          $reg = Secciones::model()->findByPk($_POST["ID"]);
          $orden = $_POST["Orden"];
          $id = $_POST["IDColor"];
          if(!$reg){
              $reg = new Secciones();
              if($id <= -1) $color = Colores::model("Colores")->find();
              else  $color = Colores::model("Colores")->findByPk($id);
              $reg->Color = $color->Color;
              $reg->RGB = $color->RGB;
          }
           if($id > -1){
             $color = Colores::model("Colores")->findByPk($id);
             $reg->Color = $color->Color;
             $reg->RGB = $color->RGB; 
           }
          $reg->Nombre = $_POST["Nombre"];
          $reg->Orden = $orden;
          $reg->save();
          echo $reg->ID;
        }catch (Exception $e){
           echo $e;
        }
    }
    
    public function actionRm(){
      try{
          Secciones::model()->deleteByPk($_POST["ID"]);
          echo 'succes';
        }catch (Exception $e){
           echo $e;
        }
    }
}
