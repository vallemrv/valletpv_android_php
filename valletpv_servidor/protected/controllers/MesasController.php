<?php

/**
 * Servicio web para la gestion de mesas del tpv
 * podemos dar altas, bajas, estado, etc...
 * @author vallesoft.es
 */
class MesasController extends CController{
    
    /**
     * Funcion solo para saber si hay respuesta del servidor
     * @return TEXT success
     **/
    /// llamada  (urlserver)/mesas/
    public function actionIndex(){
        echo 'success';
    }
    
    public function actionLsZonas(){
        $lsZonas = array();
        foreach(Zonas::model("Zonas")->findAll() as $obj)
            $lsZonas[] = $obj->getAttributes ();
        echo json_encode($lsZonas);
    }
    
    public function actionLsMesasAbiertas(){
         $condicion = new CDbCriteria();
         if(isset($_POST["idz"])){
             $idz = $_POST["idz"];
             $condicion->join = "mesaszona ON mesaszona.IDMesa=t.IDMesa";
             $condicion->addCondition("mesaszona.IDZona=$idz");
         }
         $mesas = MesasAbiertas::model("MesasAbiertas")->findAll($condicion);
         $lsMesas = array();
         foreach ($mesas as $m){
                $atr = $m->attributes;
                $atr["num"] = $m->InfMesa->NumCopias; 
                $atr["Hora"] = $m->InfMesa->Hora;
                $art["NomCam"] = $m->InfMesa->Cam->Nombre;
                $art["NomMesa"] = $m->Mesa->Nombre;
                $art["RGB"]= $m->Mesa->zona[0]->RGB;
                $lsMesas[] = $atr;
         }
         echo json_encode($lsMesas);
    }
    
     
    public function actionLsTodasLasMesas(){
            $lsMesas = array();
            $mesas = Mesas::model()->findAll();
            foreach ($mesas as $m){
                if($m->zona){
                    $mesa = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=".$m->ID);
                    $atr = $m->attributes;
                    if($mesa){ 
                      $atr["num"] = $mesa->InfMesa->NumCopias; 
                      $atr["abierta"] = true;
                    }else{
                      $atr["num"] = 0;  
                      $atr["abierta"] = false;
                    }
                    $atr["RGB"] =$m->zona[0]->RGB; 
                    $atr["IDZona"] = $m->zona[0]->ID;
                    $atr["Tarifa"] = $m->zona[0]->Tarifa;
                    $lsMesas[] = $atr;
                }
           }
           echo json_encode($lsMesas);
    }
    
    public function actionLsMesas(){
        $lsMesas = array();
        $zona = Zonas::model("Zonas")->findByPk($_POST["id"]);
        
        if($zona){
           
            $id = isset($_POST["idm"]) ? $_POST["idm"] : -1 ;
            
            foreach ($zona->mesas as $m){
              if($m->ID!=$id){  
               
                $mesa = MesasAbiertas::model()->find("IDMesa=".$m->ID);
                $atr = $m->attributes;
                $atr["c"] =  $zona->Color;
                $atr["t"] =  $zona->Tarifa;
                if($mesa){ 
                  $atr["RGB"] = $mesa->InfMesa->NumCopias > 0 ? "255,0,0" : $zona->RGB;
                  $atr["abierta"] = true;
                }else{
                  $atr["abierta"] = false;
                  $atr["RGB"] =$zona->RGB; 
                }
                $atr["Color"] =$zona->Color;
                $atr["IDZona"] = $zona->ID;
                $lsMesas[] = $atr;
              }
           }
           echo json_encode($lsMesas);
        }
    }
    
    public function actionLs(){
        $mesas = Mesas::model("Mesas")->findAll();
        if(count($mesa)>0){
            foreach($mesas->queryAll() as $m){
                $a = $m->attributes;
                $a["Color"] = $m->zona->Color;
            }
          echo json_encode($a);
        }
    }


    public function actionAddZona(){
      try{
          $reg = Zonas::model("Zonas")->findByPk($_POST["ID"]);
          $id = $_POST["IDColor"];
          if(!$reg){
              $reg = new Zonas();
              if($id<=-1) $color = Colores::model("Colores")->find();
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
          
          $reg->save();
          echo $reg->ID;
        }catch (Exception $e){
           echo $e;
        }
    }
   
   
    public function actionAdd(){
      try{
            $idz = $_POST["IDZona"];
            $orden = $_POST["Orden"];
            $reg = Mesas::model("Mesas")->findByPk($_POST["ID"]);
            if(!$reg) $reg = new Mesas();
            $reg->Nombre = $_POST["Nombre"];
            $reg->Orden = $orden;
            $reg->save();
            if($idz>=0){
               MesasZona::model("MesasZona")->deleteAll ("IDMesa=".$_POST["ID"]);
               $z = new MesasZona();
               $z->IDZona = $idz;
               $z->IDMesa = $reg->ID;
               $z->save();
            }
            echo $reg->ID;
           }catch (Exception $e){
              echo $e;
           }
    }
    
     public function actionRmZona(){
       try{
            $reg = Zonas::model("Zonas")->deleteByPk($_POST["ID"]);
            echo 'success';
         }  catch (Exception $e){
             echo $e;
         }
    }
    
    public function actionRm(){
       try{
            $reg = Mesas::model("Mesas")->deleteByPk($_POST["ID"]);
            echo 'success';
         }  catch (Exception $e){
             echo $e;
         }
    }
    
    
    
   
}


