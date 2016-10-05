<?php

/**
 * Clase para la gestion de articulos del tpv
 *
 * @author elvalle
 */

Yii::import('application.extensions.EWideImage.EWideImage');

class ArticulosController extends CController {
    
   
    public function  actionListado(){
        $offset = isset($_POST["offser"]) ? $_POST["offset"] : 0; 
        $t = isset($_POST["t"])?$_POST["t"]:1;
        $condicion = new CDbCriteria();
        $condicion->offset = $offset;
        $condicion->group="ID";
        $idSec = -1;
        
        if(isset($_POST["sec"])){
           $idSec = $_POST["sec"];
           $condicion->order="Orden DESC, Nombre";
           $condicion->join= "INNER JOIN teclaseccion ON t.ID=teclaseccion.IDTecla";
           $condicion->addCondition("IDSeccion=".$_POST["sec"]);
        }
        
        if(isset($_POST["str"])){
           $condicion->addSearchCondition ('Tag', $_POST["str"]);
           $condicion->order="Tag";
           $condicion->limit = 15;
        }
        
        $lstArt = array();
        foreach (Articulos::model("Articulos")->findAll($condicion) as $art){
            $a = $art->attributes;
            if ($t==1) $a["Precio"]=$art->P1;
            else $a["Precio"]=$art->P2;
            $a["RGB"]=count($art->seccion)>0 ? $art->seccion[0]->RGB : "255,255,0";
            $a["Color"]=count($art->seccion)>0 ? $art->seccion[0]->Color : "gray";
            $a["IDSec2"]= count($art->seccion)>1 ? $art->seccion[1]->ID : -1;
            $a["IDSeccion"]=  count($art->seccion)>0 ? $art->seccion[0]->ID : -1;
            $a["IDSecCom"]=  count($art->comanda)>0 ? $art->comanda[0]->IDSeccion : -1;
            $a["NomFamilia"]= $art->familia->Nombre;
            $lstArt[] = $a;
         }
         
        echo json_encode($lstArt);
    }
    
     public function  actionLsTodos(){
        
        $lstArt = array();
        $teclas = Articulos::model()->findAll();
        foreach ($teclas as $art){
            if($art->seccion){
              $a = $art->attributes;
              $a["RGB"]= count($art->seccion)>0 ? $art->seccion[0]->RGB : "255,255,0";
              $a["Precio"] = $art->P1;
              $a["IDSeccion"]= count($art->seccion)>0 ? $art->seccion[0]->ID : -1;
              $a["IDSec2"]= count($art->seccion)>1 ? $art->seccion[1]->ID : -1;
              $lstArt[] = $a;
            }
         }
         
        echo json_encode($lstArt);
    }
   
    
    public function  actionLsArtCom(){
        $condicion = new CDbCriteria();
        $condicion->order="teclascom.Orden DESC";
        $condicion->join= "INNER JOIN teclascom ON t.ID=teclascom.IDTecla";
        
        if(isset($_POST["sec"]))
           $condicion->addCondition("teclascom.IDSeccion=".$_POST["sec"]);
        
        if(isset($_POST["str"])){
           $condicion->addSearchCondition ('Tag', $_POST["str"]);
           $condicion->order="Tag";
           $condicion->limit = 15;
        }
        
        $lstArt = array();
        foreach (Articulos::model("Articulos")->findAll($condicion) as $art){
            $a = $art->attributes;
            $a["RGB"]= count($art->seccion)>0 ? $art->seccion[0]->RGB : "255,255,0";
            $a["Color"]= count($art->seccion)>0? $art->seccion[0]->Color : "gray";
            $a["IDSeccion"]= count($art->comanda)>0 ? $art->comanda[0]->IDSeccion : -1;
            $a["OrdCom"]= count($art->comanda) > 0 ? $art->comanda[0]->Orden : $art->Orden;
            $lstArt[] = $a;
         }
         
        echo json_encode($lstArt);
    }
    
    
    
    function  LsNoIncluidos(){
        $offset = isset($_POST["offser"]) ? $_POST["offset"] : 0; 
        $t = isset($_POST["t"])?$_POST["t"]:1;
        $condicion = new CDbCriteria();
        $condicion->offset = $offset;
        $condicion->group="ID";
        $idSec = -1;
        
        if(isset($_POST["sec"])){
           $idSec = $_POST["sec"];
           $condicion->order="Orden DESC, Nombre";
           $condicion->join= "INNER JOIN teclaseccion ON t.ID=teclaseccion.IDTecla";
           $condicion->addCondition("IDSeccion=".$_POST["sec"]);
        }
        
        if(isset($_POST["str"])){
           $condicion->addSearchCondition ('Tag', $_POST["str"]);
           $condicion->order="Tag";
           $condicion->limit = 15;
        }
        
        $lstArt = array();
        foreach (Articulos::model("Articulos")->findAll($condicion) as $art){
          if(count($art->comanda)<=0 ){  
                $a = $art->attributes;
                if ($t==1) $a["Precio"]=$art->P1;
                else $a["Precio"]=$art->P2;
                $a["RGB"]=count($art->seccion)>0 ? $art->seccion[0]->RGB : "255,255,0";
                $a["Color"]=count($art->seccion)>0 ? $art->seccion[0]->Color : "gray";
                $a["IDSec2"]= count($art->seccion)>1 ? $art->seccion[1]->ID : -1;
                $a["IDSeccion"]=  count($art->seccion)>0 ? $art->seccion[0]->ID : -1;
                $a["IDSecCom"]=  count($art->comanda)>0 ? $art->comanda[0]->IDSeccion : -1;
                $a["NomFamilia"]= $art->familia->Nombre;
                $lstArt[] = $a;
            }
         }
         
        echo json_encode($lstArt);
        
    }
    
    public function actionEditCom(){
        if($_POST["ids"]==-1){
            $art = TeclasCom::model("TeclasCom")->find("IDTecla=".$_POST["id"]);
            $art->delete();
        }else{
            $art = TeclasCom::model("TeclasCom")->find("IDTecla=".$_POST["id"]." AND IDSeccion=".$_POST["ids"]);

            if(!$art) $art = new TeclasCom();

            $art->IDSeccion = $_POST["ids"];
            $art->IDTecla =   $_POST["id"];
            $art->Orden = $_POST["Orden"];
            $art->save();
        }
        echo $this->LsNoIncluidos();
    }
    
    public function  actionTeclasCom(){
        $condicion = new CDbCriteria();
        $condicion->order="teclascom.Orden DESC, t.Nombre";
        $condicion->join= "INNER JOIN teclascom ON t.ID=teclascom.IDTecla";
        
        if(isset($_POST["sec"]))
           $condicion->addCondition("teclascom.IDSeccion=".$_POST["sec"]);
        
        if(isset($_POST["str"])){
           $condicion->addSearchCondition ('Tag', $_POST["str"]);
           $condicion->order="Tag";
           $condicion->limit = 10;
        }
        
        $lstArt = array();
        foreach (Articulos::model("Articulos")->findAll($condicion) as $art){
            $a = $art->attributes;
            $a["RGB"]=$art->seccion[0]->RGB;
            $a["Color"]=$art->seccion[0]->Color;
            $a["IDSeccion"]= count($art->comanda)>0 ? $art->comanda[0]->IDSeccion : -1;
            $a["OrdCom"]= count($art->comanda) > 0 ? $art->comanda[0]->Orden : $art->Orden;
            $lstArt[] = $a;
         }
         
        echo json_encode($lstArt);
    }
    

    
    public function actionAdd(){
        $this->actionEdit();
    }
    
    public function actionEdit(){
         $art = Articulos::model()->findByPk($_POST['ID']);
        if(!$art)  $art = new Articulos();
       
            $art->Nombre = $_POST["Nombre"];
            $art->P1 = str_replace( ",", ".",  $_POST["P1"]);
            $art->P2 = str_replace(",", ".",  $_POST["P2"]);
            $art->Orden = $_POST["Orden"];
            $art->IDFamilia = $_POST["IDFamilia"];
            $art->Tag=  $_POST["Tag"];
            $art->save();
            
            TeclaSeccion::model("TeclaSeccion")->deleteAll("IDTecla=".$art->ID);
            
                $teclasec = new TeclaSeccion();
                $teclasec->IDTecla = $art->ID;
                $teclasec->IDSeccion = $_POST["IDSeccion"];
                $teclasec->save();
           
            if($_POST["sec2"]!=="-1"){
                $teclasec = new TeclaSeccion();
                $teclasec->IDTecla = $art->ID;
                $teclasec->IDSeccion = $_POST["sec2"];
                $teclasec->save();
            }
          
        
           echo $this->actionListado();
        
    }
    
    public function actionEditTeclaCom(){
         if($_POST["ids"]==-1){
            $art = TeclasCom::model("TeclasCom")->find("IDTecla=".$_POST["id"]);
            $art->delete();
        }else{
            $art = TeclasCom::model("TeclasCom")->find("IDTecla=".$_POST["id"]." AND IDSeccion=".$_POST["ids"]);

            if(!$art) $art = new TeclasCom();

            $art->IDSeccion = $_POST["ids"];
            $art->IDTecla =   $_POST["id"];
            $art->Orden = $_POST["Orden"];
            $art->save();
        }
        echo $this->actionTeclasCom();
    }
    
    public function actionLsTapas(){
        $famTapas = Familias::model("Familias")->findAll("Tipo='Tapas'");
        $lstArt = array();
            
        foreach ($famTapas as $tapas) {
           foreach (Articulos::model("Articulos")->findAll("IDFamilia=".$tapas->ID) as $art){
             $a = $art->attributes;
             $a["regalo"]=true;
             $a["comb"]=NULL;
             $lstArt[] = $a;
           }
        }
         echo json_encode($lstArt);
    }
    
    public function actionChFoto(){
        
         $basepath = Yii::app()->getBasePath().'/../';
         $pathTemp = "gesficheros/files/";
         $pathTempTh = "gesficheros/files/thumbnail/";
         $pathPerfil ="images/fotos/";
           
           $art = Articulos::model("Articulos")->findByPk($_POST["id"]); 
           $nomFoto = $_POST["nomF"];
           $nomFotoID = GesImg::GetUidName($nomFoto);
           
           GesImg::removeImg($basepath.$pathPerfil.$art->Foto);
           GesImg::removeImg($basepath.$pathTempTh.$nomFoto);
           GesImg::Move($basepath.$pathTemp.$nomFoto, $basepath.$pathPerfil.$nomFotoID);
           
           $image = EWideImage::load($basepath.$pathPerfil.$nomFotoID);
           $image = $image->resize(100, 100 , 'outside');
           $image = $image->crop('center', 'center', 100, 100);
           $image->saveToFile($basepath.$pathPerfil.$nomFotoID) ;
           $art->Foto = $nomFotoID;
           $art->update();
           echo $nomFotoID;
     }
     
     public function actionImg(){
          if(isset($_GET["id"])){
            $id = $_GET["id"];
            $basepath = Yii::app()->getBasePath().'/../';
            $art = Articulos::model("Articulos")->findByPk($id);
            if($art->Foto){
                $image = EWideImage::load($basepath.'/images/fotos/'.$art->Foto);
                $image->output('jpg', 90);
            }else{
                $im = EWideImage::createTrueColorImage(100, 100);
                $c = $im->getCanvas();
                $rgb = explode(",",$art->seccion->RGB);
                $c->fill(0, 0, $im->allocateColor($rgb[0], $rgb[1], $rgb[2]));
	        $im->output('jpg', 60);
            }
         }
     }
     
     public function actionRm(){
         $id = $_POST["id"];
         $art = Articulos::model("Articulos")->findByPk($id);
         if($art) $art->delete ();
         echo 'success';
     }
}
