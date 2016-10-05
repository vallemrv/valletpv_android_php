<?php


/**
 * Description of ComandasController
 *
 * @author valle
 */
class ComandasController extends CController {
    
    public function  actionLs(){
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
    
    //put your code here
    function  actionDescargarTeclados(){
        
        $secciones = SeccionesCom::model()->findAll();
        $teclado = array();
        foreach ($secciones as $sec){
        $lstArt = array();
          foreach ($sec->articulos as $art){
              $a = $art->attributes;
              $a["Precio"]=$art->P1;
              $a["RGB"]= count($art->seccion)>0 ? $art->seccion[0]->RGB : "255,255,0";
              $a["Color"]= count($art->seccion)>0? $art->seccion[0]->Color : "gray";
              $a["OrdCom"] = $art->comanda[0]->Orden;
              $a["IDSeccion"] = $art->comanda[0]->IDSeccion;
              $a["Sub"] = array();
              
              foreach (SubTeclas::model("SubTeclas")->findAll("IDTecla=".$art->ID) as $sub)  $a["Sub"][] = $sub->attributes;
              
              $lstArt[] = $a;
            
            }
           $teclado[$sec->Nombre]=$lstArt; 
            
        }
        
        echo json_encode($teclado);  
    }
    
    public function actionLsAll(){
        $secciones = SeccionesCom::model()->findAll();
        $lstArt = array();
        foreach ($secciones as $sec){
          foreach ($sec->articulos as $art){
              $a = $art->attributes;
              $a["Precio"]=$art->P1;
              $a["RGB"]= count($art->seccion)>0 ? $art->seccion[0]->RGB : "255,255,0";
              $a["Color"]= count($art->seccion)>0? $art->seccion[0]->Color : "gray";
              $a["IDSeccion"]=  count($art->seccion)>0? $art->seccion[0]->ID : -1;
              $a["OrdCom"] = $art->comanda[0]->Orden;
              $a["Nombre_sec"] = $sec->Nombre;
              $lstArt[] = $a;
            }
          }
        
        echo json_encode($lstArt);  
    }
    
    public function actionRmSubTecla(){
        $sub = SubTeclas::model("SubTeclas")->deleteByPk($_POST["ID"]);
        $this->actionLsSubTeclas();
    }
    
    public function actionLsSubTeclas(){
       if(isset($_POST["IDTecla"]))  $sug = SubTeclas::model("SubTeclas")->findAll("IDTecla=".$_POST["IDTecla"]);
       else $sug = SubTeclas::model("SubTeclas")->findAll();
        $a = array();
        foreach ($sug as $s){
            $a[]=$s->attributes;
        }
        echo json_encode($a);
    }
    
    public function actionAddSubTeclas(){
        $sug = SubTeclas::model()->findByPk($_POST["ID"]);
        if(!$sug) $sug = new SubTeclas();
        $sug->IDTecla = $_POST["IDTecla"];
        $sug->Nombre = $_POST["Nombre"];
        $sug->Incremento = isset($_POST["Incremento"]) ? $_POST["Incremento"] : 0.00;
        $sug->save();
        $this->actionLsSubTeclas();
    }
    
    
    function actionPedir(){
        $idm = $_POST["idm"];
        $idc = $_POST["idc"];
        $lineas = json_decode($_POST["pedido"]);
        
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
            
        
        //Abrimos la mesa si no esta abierta..
        $mesa = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=$idm");
        if(!$mesa){
            $infMesa = new InfMesas();
            $infMesa->IDCam = $idc;
            $infMesa->Hora = date('H:i');
            $infMesa->Fecha = date('Y/m/d');
            $infMesa->UID = uniqid($idm."-");
            $infMesa->save();
            
            $mesa = new MesasAbiertas();
            $mesa->IDMesa = $idm;
            $mesa->UID = $infMesa->UID;
            $mesa->save();
            
        }
        
        $pedido = new Pedidos();
        $pedido->UID = $mesa->UID;
        $pedido->Hora = date('H:i');
        $pedido->IDCam = $idc;
        $pedido->save();
        
        foreach ($lineas as $pd){
            
            $can = $pd->Can; 
           
            for($i = 0; $i < $can; $i++){
               $linea = new LineasPedido();
               $linea->UID = $mesa->UID;
               $linea->IDArt = $pd->ID;
               $linea->IDPedido = $pedido->ID;
               $linea->Precio = $pd->Precio;
               $linea->Nombre = $pd->Nombre;
               $linea->Estado = $pd->Precio == 0 ? "R" : "P";
               $linea->save();
             }
          
          
        }
        $this->ImprimirPedido($pedido->ID);
    }
    
    private function ImprimirPedido($idp){
       
         $infPedido = Pedidos::model("Pedidos")->findByPk($idp);
         $pedido = Yii::app()->db->createCommand()
                ->select('count(l.IDArt) as Can, l.Nombre, l.IDArt, IDPedido, rec.Nombre as NR, nomImp ')
                ->from('lineaspedido as l')
                ->join('teclas as art', 'l.IDArt=art.ID ')
                ->join('familias as fam', 'art.IDFamilia=fam.ID')
                ->join("receptores as rec", 'fam.IDReceptor=rec.ID')
                ->where( "IDPedido=$idp")
                ->order('Nombre, nomImp')
                ->group('l.IDArt, l.Nombre, l.Precio, l.IDPedido') 
                ->queryAll();
          
        ImprimirTicket::HacerPedido($infPedido->GetInf(), $pedido);
    }
    
    function actionLsPedidos(){
        
        $idm = $_POST["idm"];
        $mesa = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=$idm");
        if($mesa){
            $uid = $mesa->UID;
            $pedido = Yii::app()->db->createCommand()
                ->select('l.ID, count(l.IDArt) as Can, l.Nombre, l.IDArt, IDPedido ')
                ->from('lineaspedido as l')
                ->where( "(Estado='P' OR Estado='R')  AND UID='$uid'")
                ->order("l.ID DESC")
                ->group('l.IDArt, l.Nombre, l.Precio, l.IDPedido')    
                ->queryAll();
          
            echo json_encode($pedido);
         }
    }
        
     public function  actionLsNoIncluidos(){
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
    
    
}
