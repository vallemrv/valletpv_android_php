<?php

/**
 * Description of CuentaController
 *
 * @author valle
 */


class CuentaController extends CController {
   
    
    public function actionJuntarMesas(){
        $mesaP = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=".$_POST['idp']);
        $mesaS = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=".$_POST['ids']);
        if($mesaS){
            $uid = $mesaS->UID;
            Pedidos::model("Pedidos")->updateAll(array("UID"=>$mesaP->UID),"UID='$uid'");
            LineasPedido::model("LineasPedido")->updateAll(array("UID"=>$mesaP->UID),"UID='$uid'");
            $mesaS->InfMesa->delete();
          }else{
            $mesa = new MesasAbiertas();
            $mesa->IDMesa = $_POST['ids'];
            $mesa->UID = $mesaP->UID;
            $mesa->save();
        }
        echo 'success';
    }
    
    public function actionCambiarMesas(){
        $mesaP = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=".$_POST['idp']);
        $mesaS = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=".$_POST['ids']);
        if($mesaS){
            $uid = $mesaS->UID;
            $mesaS->UID = $mesaP->UID;
            $mesaS->update();
            $mesaP->UID = $uid; $mesaP->update();
        }else{
             $mesaP->IDMesa = $_POST['ids']; $mesaP->update();
        }
        echo 'success';
    }
    
    
    public function actionMvLinea(){
        $idm = $_POST["idm"];
        $idLinea = $_POST["idLinea"];
        $linea = LineasPedido::model()->findByPk($idLinea);
        if($linea){
             $pedido = Pedidos::model()->findByPk($linea->IDPedido);
             $idc = $pedido->IDCam;
             $uid = $linea->UID;
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
                
                $linea->UID = $mesa->UID;
                $linea->IDPedido = $pedido->ID;
                $linea->save();
                
               $numart = LineasPedido::model("LineasPedido")->count("(Estado='P' OR Estado='N')  AND  UID='".$uid."'");
               if($numart<=0)  MesasAbiertas::model("MesasAbiertas")->deleteAll("UID='$uid'");
          
           }
           
        echo 'success';
    }
    
       
    public function actionAdd(){
        $idm = $_POST["idm"];
        $idc = $_POST["idc"];
        $lineas = json_decode($_POST["pedido"]);
        
       
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
               $linea->IDArt = $pd->IDArt;
               $linea->IDPedido = $pedido->ID;
               $linea->Precio = $pd->Precio;
               $linea->Nombre = $pd->Nombre;
               $linea->Estado = $pd->Precio == 0 ? "R" : "P";
               $linea->save();
             }
         }
         echo 'success';
    }
    
    public function actionLsAparcadas(){
       $mesas = MesasAbiertas::model("MesasAbiertas")->findAll();
       $condicion = new CDbCriteria();
       $condicion->select = ("t.*, mesasabiertas.IDMesa");
       $condicion->join = ("INNER JOIN mesasabiertas ON mesasabiertas.UID=t.UID");
       foreach ($mesas as $m){
           $uid = $m->UID;
           $condicion->addCondition("t.UID='$uid'", "OR");
       }
       $condicion->addCondition("Estado='P'");
       $res = new LineasPedido();
       echo json_encode($res->queryAllCDb($condicion));
    }
    
    public function actionAparcar(){
       $idm= $_POST["idm"];
       $mesa = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=$idm");
         if($mesa){
             $uid = $mesa->UID;
             LineasPedido::model("LineasPedido")->updateAll(array("Estado"=>'P'), "UID='$uid' AND Estado='N'");
             echo 'success';
         }
    }   
    
    public function actionCobrar(){
        $idm = $_POST["idm"];
        $idc = $_POST["idc"];
        $entrega = $_POST["entrega"];
        $art = json_decode($_POST["art"]);
       
        //Abrimos la mesa si no esta abierta..
        $mesa = MesasAbiertas::model("MesasAbiertas")->find("IDMesa='$idm'");
        if($mesa){
            $uid = $mesa->UID;
            $ticket = new Ticket();
            $ticket->Hora = date('H:i');
            $ticket->Fecha = date('Y/m/d');
            $ticket->IDCam = $idc;
            $ticket->UID = $uid;
            $ticket->Entrega = $entrega;
            $ticket->Mesa = $mesa->Mesa->Nombre;
            $ticket->save();
            
            foreach ($art as $l){
               
                $condicion = new CDbCriteria();
                $condicion->addCondition("UID='$uid'");
                $condicion->addCondition("(Estado='P' OR Estado='N')");
                $condicion->addCondition("IDArt=".$l->IDArt);
                $condicion->addCondition("Precio=".$l->Precio);
                $condicion->limit = $l->Can;
              
                $reg = LineasPedido::model("LineasPedido")->findAll($condicion);
                
                
                foreach ($reg as $r){
                    $ticketlinea= new TicketLineas();
                    $ticketlinea->IDTicket = $ticket->ID;
                    $ticketlinea->IDLinea = $r->ID;
                    $ticketlinea->save();$r->Estado = 'C'; $r->update();
                }
            }
            
            
            $numart = LineasPedido::model("LineasPedido")->count("(Estado='P' OR Estado='N')  AND  UID='".$uid."'");
            if($numart<=0)  MesasAbiertas::model("MesasAbiertas")->deleteAll("UID='$uid'");
            
            GesImpresion::ImprimirTicket($ticket->ID, $idc);
            echo 'success';
          
        }
        
        
    }
    
    public function actionRm(){
         $idm = $_POST["idm"];
         $motivo = $_POST["motivo"];
         $idc = $_POST["idc"];
         $mesa = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=$idm");
         if($mesa){
            $uid = $mesa->UID;
            $condicion = new CDbCriteria();
            $condicion->addCondition("UID='$uid'");
            $condicion->addCondition("(Estado='R' OR Estado='P' OR Estado='N')");
            $reg = LineasPedido::model("LineasPedido")->findAll($condicion);
            foreach ($reg as $r){
                    $historial = new HistorialNulos();
                    $historial->IDLPedido = $r->ID;
                    $historial->IDCam = $idc;
                    $historial->Motivo = $motivo;
                    $historial->Hora =  date('H:i');
                    $historial->save();
                    $r->Estado = 'A'; $r->update();
                }
                
            MesasAbiertas::model("MesasAbiertas")->deleteAll("UID='$uid'");   
            echo 'success';
         }
    }
    
    public function actionRmLinea(){
       $idm = $_POST["idm"];
       $p = $_POST["Precio"];
       $idArt = $_POST["idArt"];
       $can = $_POST["can"];
       $idc = $_POST["idc"];
       $motivo = $_POST["motivo"];
       $s = $_POST["Estado"];
       $n = $_POST["Nombre"];
       
       $mesa = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=$idm");
         if($mesa){
            $uid = $mesa->UID;
            $condicion = new CDbCriteria();
            $condicion->addCondition("UID='$uid'");
            $condicion->addCondition("IDArt=$idArt");
            $condicion->addCondition("Estado='$s'");
            $condicion->addCondition("Precio=$p");
            $condicion->addCondition("Nombre='$n'");
            $condicion->limit = $can;
            $reg = LineasPedido::model("LineasPedido")->findAll($condicion);
            foreach ($reg as $r){
                if($motivo!=="null"){
                    $historial = new HistorialNulos();
                    $historial->IDLPedido = $r->ID;
                    $historial->IDCam = $idc;
                    $historial->Motivo = $motivo;
                    $historial->Hora =  date('H:i');
                    $historial->save();
                    $r->Estado = 'A'; $r->update();
                }else $r->delete ();
            }
            
            $numart = LineasPedido::model("LineasPedido")->count("(Estado='P' OR Estado='N')  AND  UID='".$uid."'");
            if($numart<=0)  MesasAbiertas::model("MesasAbiertas")->deleteAll("UID='$uid'");
            echo "success";
         }
           
    }
    
    
     
    public function actionLsTicket(){
        $offset = isset($_POST["offset"]) ? $_POST["offset"] : 0;
       
            $ls = Yii::app()->db->createCommand()
                 ->select('ticket.ID, Fecha, Hora, Entrega, Mesa, SUM( Precio ) AS Total')
                 ->from('ticket')
                 ->join("ticketlineas", 'ticket.ID=ticketlineas.IDTicket')
                 ->join('lineaspedido', 'lineaspedido.ID=ticketlineas.IDLinea')
                 ->order("ticket.ID DESC")   
                 ->group ("ticket.ID")
                 ->offset ($offset)   
                 ->limit (20)
                 ->queryAll();
            
                echo json_encode($ls);
          
       }
       
    public function actionLsLineas(){
         $id = $_POST["id"]; 
         
            $ls = Yii::app()->db->createCommand()
                 ->select('COUNT(lineaspedido.IDArt) As Can, lineaspedido.IDArt,  lineaspedido.Precio, SUM( Precio ) AS Total,
                            CASE lineaspedido.IDArt
                                WHEN  0 THEN lineaspedido.Nombre 
                                ELSE teclas.Nombre
                            END as Nombre')
                 ->from('lineaspedido')
                 ->leftJoin("teclas", 'lineaspedido.IDArt=teclas.ID')
                 ->join("ticketlineas", 'lineaspedido.ID=ticketlineas.IDLinea')
                 ->join('ticket', 'ticket.ID=ticketlineas.IDTicket')
                 ->where("ticket.ID=$id")
                 ->group ("lineaspedido.IDArt, Nombre, lineaspedido.Precio")
                 ->queryAll();
         
            $total = Yii::app()->db->createCommand()
                 ->select('SUM( Precio ) AS Total')
                 ->from('ticket')
                 ->join("ticketlineas", 'ticket.ID=ticketlineas.IDTicket')
                 ->join('lineaspedido', 'lineaspedido.ID=ticketlineas.IDLinea')
                 ->where("ticket.ID=$id")
                 ->queryScalar();
            
        echo json_encode(array("lineas"=>$ls,"total"=>$total, "IDTicket"=>$id));
          
       }
       
    public function actionTicket(){
        $this->getTicket(-1) ;   
    }
       
    function getTicket($IDPedido){
        $idm = $_POST["idm"];
        $mesa = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=$idm");
         if($mesa){
            $uid = $mesa->UID;
            
            $pedidos = Yii::app()->db->createCommand()
                ->select('Precio, Estado, IDArt, COUNT(IDArt) as Can, (Precio * COUNT(IDArt)) as Total,Nombre')
                ->from('lineaspedido')
                ->where("(Estado='P' OR Estado='N') AND UID='$uid'")
                ->group("IDArt, Precio, Nombre, Estado")
                ->order("Estado, IDArt")    
                ->queryAll();
            
            $total = Yii::app()->db->createCommand()
                        ->select('SUM(Precio) as ticket')
                        ->from('lineaspedido')
                        ->where("(Estado='P' OR Estado='N') AND UID='$uid'")
                        ->queryScalar();
                
                        $res = array("lineas"=>$pedidos, "total"=>($total ? $total : "0.00"), "pedido"=>$IDPedido);
           
             echo json_encode($res);
         }
     }  
}
