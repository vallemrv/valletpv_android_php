<?php


/**
 * Description of HistorialController
 *
 * @author valle
 */
class HistorialController extends CController{
    //put your code here
    public function actionLsInfMesasAbiertas(){
         $condicion = new CDbCriteria();
         if(isset($_POST["idz"])){
             $idz = $_POST["idz"];
             $condicion->join = "INNER JOIN mesaszona ON mesaszona.IDMesa=t.IDMesa";
             $condicion->addCondition("mesaszona.IDZona=$idz");
         }
         $mesas = MesasAbiertas::model("MesasAbiertas")->findAll($condicion);
         $lsMesas = array();
         foreach ($mesas as $m){
                $uid = $m->UID;
                $atr = $m->attributes;
                $atr["num"] = $m->InfMesa->NumCopias; 
                $atr["Hora"] = $m->InfMesa->Hora;
                $atr["NomCam"] = $m->InfMesa->Cam->Nombre;
                $atr["NomMesa"] = $m->Mesa->Nombre;
                $atr["Total"] =   Yii::app()->db->createCommand()
                        ->select('SUM(Precio) as ticket')
                        ->from('lineaspedido')
                        ->where("(Estado='P' OR Estado='N') AND UID='$uid'")
                        ->queryScalar();
                $atr["RGB"]= $m->Mesa->zona[0]->RGB;
                $lsMesas[] = $atr;
         }
         echo json_encode($lsMesas);
    }
    
    public function actionLsInfMesas(){
         $condicion = new CDbCriteria();
         $condicion->limit = 50;
         $condicion->order=("t.Fecha DESC, t.Hora DESC");
         $condicion->addCondition("t.UID not in (SELECT UID FROM mesasabiertas)");
         $mesas =  InfMesas::model()->findAll($condicion);
         $lsMesas = array();
         foreach ($mesas as $m){
                $uid = $m->UID;
                $atr = $m->attributes;
                list($id,$resto) =  explode("-", $uid);
                $mesa = Mesas::model()->findByPk($id);
                if(!$mesa) $mesa = new Mesas();
                $atr["NomMesa"] = $mesa->Nombre;
                $atr["NomCam"] = $m->Cam->Nombre;
                $atr["Total"] =   Yii::app()->db->createCommand()
                        ->select('SUM(Precio) as ticket')
                        ->from('lineaspedido')
                        ->where("Estado='C' AND UID='$uid'")
                        ->queryScalar();
                $lsMesas[] = $atr;
         }
         echo json_encode($lsMesas);
    }
    
    
    public function actionLsNulos(){
         $condicion = new CDbCriteria();
         $condicion->limit = 50;
         $condicion->order=("t.Fecha DESC, t.Hora DESC");
         $condicion->group="t.UID";
         $condicion->join = "INNER JOIN lineaspedido ON t.UID=lineaspedido.UID";
         $condicion->addCondition("lineaspedido.ID in (SELECT IDLPedido FROM historialnulos)");
         
         $mesas =  InfMesas::model()->findAll($condicion);
         $lsMesas = array();
         foreach ($mesas as $m){
                $uid = $m->UID;
                $atr = $m->attributes;
                list($id,$resto) =  explode("-", $uid);
                $mesa = Mesas::model()->findByPk($id);
                if(!$mesa) $mesa = new Mesas();
                $atr["NomMesa"] = $mesa->Nombre;
                $atr["NomCam"] = $m->Cam->Nombre;
                $atr["Total"] =   Yii::app()->db->createCommand()
                        ->select('SUM(Precio) as ticket')
                        ->from('lineaspedido')
                        ->where("Estado='C' AND UID='$uid'")
                        ->queryScalar();
                $lsMesas[] = $atr;
         }
         echo json_encode($lsMesas);
    }
    
  
    public function actionInfMesa(){
        $uid = $_POST["uid"];
        $obj = array();
        $pedidos = Pedidos::model()->findAll("UID='$uid'");
        foreach ($pedidos as $p){
            $pedido = $p->GetInf(false);
            $idp = $p->ID;
            $pedido["lp"] =  Yii::app()->db->createCommand()
                ->select('l.ID, count(l.IDArt) as Can, l.Nombre, l.IDArt, IDPedido ')
                ->from('lineaspedido as l')
                ->where( "(Estado='P' OR Estado='R')  AND UID='$uid' AND IDPedido=$idp")
                ->order("l.ID DESC")
                ->group('l.IDArt, l.Nombre, l.Precio, l.IDPedido')    
                ->queryAll();
            
            $pedido["ln"] =  Yii::app()->db->createCommand()
                ->select('l.ID, count(l.IDArt) as Can, l.Nombre, l.IDArt, IDPedido, h.Motivo, c.Nombre AS NomCam ')
                ->from('lineaspedido as l')
                ->join("historialnulos as h", "l.ID=h.IDLPedido")
                ->join("camareros as c", "h.IDCam=c.ID")
                ->where( "(Estado='A')  AND UID='$uid' AND IDPedido=$idp")
                ->order("l.ID DESC")
                ->group('l.IDArt, l.Nombre, l.Precio, l.IDPedido')    
                ->queryAll();
            
            $pedido["lc"] =  Yii::app()->db->createCommand()
                ->select('l.ID, count(l.IDArt) as Can, l.Nombre, l.IDArt, IDPedido ')
                ->from('lineaspedido as l')
                ->where( "(Estado='C')  AND UID='$uid' AND IDPedido=$idp")
                ->order("l.ID DESC")
                ->group('l.IDArt, l.Nombre, l.Precio, l.IDPedido')    
                ->queryAll();
         $obj[]=$pedido;   
        }
        echo json_encode($obj);
    }


}
