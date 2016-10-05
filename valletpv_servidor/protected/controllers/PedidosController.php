<?php

/**
 * Description of PedidosController
 *
 * @author elvalle
 */

class PedidosController extends CController {
    
    public function actionGetPendientes(){
        $idz = $_POST["idz"];
        //$idz = 4;
        $mz = "(SELECT mesaszona.IDZona, mesasabiertas.UID FROM mesasabiertas LEFT JOIN mesaszona ON mesaszona.IDMesa=mesasabiertas.IDMesa) as mz";
        $m = "(SELECT mesas.Nombre AS nomMesa, mesasabiertas.UID FROM mesasabiertas LEFT JOIN mesas ON mesas.ID=mesasabiertas.IDMesa) as m";
        $s = "(SELECT IDLinea FROM servidos)";
        $pedidos = Yii::app()->db->createCommand()
                ->select('m.nomMesa, mz.IDZona, l.ID, l.Precio, count(l.IDArt) as Can, l.Nombre, l.IDArt, IDPedido ')
                ->from('lineaspedido as l')
                ->join($mz, "mz.UID=l.UID")
                ->join($m, "m.UID=l.UID")
                ->where( "(Estado='P' OR Estado='R')  AND mz.IDZona=$idz AND l.ID NOT IN $s")
                ->order("l.ID DESC")
                ->group('l.IDArt, l.Nombre, l.Precio, l.IDPedido, l.UID')
                ->queryAll();
         echo json_encode($pedidos);
       }
       
       
     public function actionRcPendientes(){
        $receptor = isset($_POST["receptor"])?$_POST["receptor"]:"barra";
        $reg = isset($_POST["reg"])?"l.ID >". $_POST["reg"]. " AND ":"";
        $s = "(SELECT IDLinea FROM servidos)";
        $mz = "(SELECT mesaszona.IDZona, zonas.Nombre AS nomZona, mesasabiertas.UID FROM mesasabiertas LEFT JOIN mesaszona ON mesaszona.IDMesa=mesasabiertas.IDMesa LEFT JOIN zonas ON zonas.ID=mesaszona.IDZona ) as mz";
        $pedidos = Yii::app()->db->createCommand()
                ->select('l.ID, l.Precio, mz.nomZona, l.Nombre, l.IDArt,  p.ID AS IDPedido')
                ->group("l.ID")
                ->from('lineaspedido as l')
                ->Join($mz, "mz.UID=l.UID")
                ->join("pedidos AS p", "p.UID=l.UID")
                ->join('teclas as art', 'l.IDArt=art.ID ')
                ->join('familias as fam', 'art.IDFamilia=fam.ID')
                ->join("receptores as rec", 'fam.IDReceptor=rec.ID')
                ->where( "$reg (Estado='P' OR Estado='R')  AND rec.Nombre='$receptor' AND l.ID NOT IN $s")
                ->queryAll();
         echo json_encode($pedidos);
       }
       
    public function actionHayPedidos(){
        $receptor = isset($_POST["receptor"])?$_POST["receptor"]:"barra";
        $s = "(SELECT IDLinea FROM servidos)";
        $m = "(SELECT mesas.Nombre AS nomMesa, mesasabiertas.UID FROM mesasabiertas LEFT JOIN mesas ON mesas.ID=mesasabiertas.IDMesa) as m";
     
        $count = Yii::app()->db->createCommand()
                ->select('COUNT(l.ID) as Total')
                ->from('lineaspedido as l')
                ->join($m, "l.UID=m.UID")
                ->join('teclas as art', 'l.IDArt=art.ID ')
                ->join('familias as fam', 'art.IDFamilia=fam.ID')
                ->join("receptores as rec", 'fam.IDReceptor=rec.ID')
                ->where( "(Estado='P' OR Estado='R')  AND rec.Nombre='$receptor' AND l.ID NOT IN $s")
                ->queryScalar();
         echo $count;
       }
       
       
    
     public function actionServido(){
          $art = json_decode($_POST["art"]);
          $condicion = new CDbCriteria();
          $condicion->addCondition("IDArt=".$art->IDArt);
          $condicion->addCondition("Nombre='".$art->Nombre."'");
          $condicion->addCondition("Precio=".$art->Precio);
          $condicion->addCondition("IDPedido=".$art->IDPedido);
          $lineas = LineasPedido::model()->findAll($condicion);
          foreach ($lineas as $linea){
              $serv = new Servidos();
              $serv->IDLinea = $linea->ID;
              $serv->save();
          }
          
          $this->actionGetPendientes();
        }   
   
     public function actionRcServido(){
          $l = json_decode($_POST["lineas"]);
          foreach ($l as $art){
                $condicion = new CDbCriteria();
                $condicion->addCondition("IDArt=".$art->IDArt);
                $condicion->addCondition("Nombre='".$art->Nombre."'");
                $condicion->addCondition("Precio=".$art->Precio);
                $condicion->addCondition("IDPedido=".$art->IDPedido);
                $lineas = LineasPedido::model()->findAll($condicion);
                foreach ($lineas as $linea){
                    $serv = new Servidos();
                    $serv->IDLinea = $linea->ID;
                    $serv->save();
                }
          }          
          //$this->actionRcPendientes();
        } 
        
        public function actionMServido(){
          $l = json_decode($_POST["lineas"]);
          foreach ($l as $art){
            $serv = new Servidos();
            $serv->IDLinea = $art->ID;
            $serv->save();
          }          
          //$this->actionRcPendientes();
        }   
 }

  