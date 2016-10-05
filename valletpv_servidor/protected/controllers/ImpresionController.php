<?php
error_reporting(E_ALL);
ini_set('display_errors', '1');

/**
 * Description of ImpresionController
 *
 * @author valle
 */
class ImpresionController extends CController {
    
   
    
    public function actionPreImprimir(){
        $idm = $_POST["idm"];
        $mesa = MesasAbiertas::model("MesasAbiertas")->find("IDMesa=$idm");
         if($mesa){
            
            $inf = $mesa->InfMesa;
            $inf->NumCopias = $inf->NumCopias+1;
            $inf->update();
            
            $uid = $mesa->UID;
            $pedidos = Yii::app()->db->createCommand()
                ->select('IDArt, Precio, Estado, COUNT(IDArt) as Can, (Precio * COUNT(IDArt)) as TotalLinea,
                          CASE lineaspedido.IDArt
                                WHEN  0 THEN lineaspedido.Nombre 
                                ELSE teclas.Nombre
                            END as Nombre')
                ->from('lineaspedido')
                ->leftJoin("teclas", 'lineaspedido.IDArt=teclas.ID')
                ->where("(Estado='P' OR Estado='N') AND UID='$uid'")
                ->group("IDArt, Precio, Nombre, Estado")
                ->order("Estado, IDArt")    
                ->queryAll();
            
            $total = Yii::app()->db->createCommand()
                ->select('SUM(Precio) as ticket')
                ->from('lineaspedido')
                ->where("(Estado='P' OR Estado='N') AND UID='$uid'")
                ->queryScalar();
                

            $rutFicheros = Yii::app()->params["dirPrint"].  uniqid();
            $nombreImpresora = Receptores::model("Receptores")->find("Nombre='Ticket'")->nomImp;
            ImprimirTicket::PreimprimirTicket($rutFicheros, $nombreImpresora, $pedidos, date('d/m/Y')." - ".date('H:m'), $mesa->Mesa->Nombre, 1, $total);
                        
          }
    }
    
    function actionTicket(){
        $id = $_POST["id"];
        $idc = isset($_POST["idc"]) ? $_POST["idc"] : 'rowid';
        $ticket = Ticket::model("Ticket")->findByPk($id);
        $cam = $ticket->Cam;
        
        if($ticket && $cam){
            
            $ls = Yii::app()->db->createCommand()
                ->select('COUNT(lineaspedido.IDArt) As Can, lineaspedido.IDArt,  lineaspedido.Precio, SUM( Precio ) AS TotalLinea,
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
                

            $res = array("Importe"=>$total, "Camarero"=>$cam->Nombre, "Mesa"=>$ticket->Mesa, "Lineas"=>$ls,
                            "Cambio"=>$ticket->Entrega, "Fecha"=>$ticket->Fecha." - ".$ticket->Hora, "ID"=>$ticket->ID);
            $rutFicheros = Yii::app()->params["dirPrint"].uniqid().$cam->Nombre;
            $nombreImpresora = Receptores::model("Receptores")->find("Nombre='Ticket'")->nomImp;
            ImprimirTicket::Ticket($rutFicheros, $nombreImpresora, $res);
            echo 'success';
          }
    }
    
    function actionAbrirCajon(){
          $rutFicheros = Yii::app()->params["dirPrint"].  uniqid();
          $nombreImpresora = Receptores::model("Receptores")->find("Nombre='Ticket'")->nomImp;
          ImprimirTicket::AbrirCajon($rutFicheros, $nombreImpresora);
          echo 'success';
    }
    
     
    function actionReenviarLinea(){
         
         $idp = $_POST["idp"];
         $id = $_POST["id"];
         $nombre = $_POST["Nombre"];
         
         $infPedido = Pedidos::model("Pedidos")->findByPk($idp);
         $pedido = Yii::app()->db->createCommand()
                ->select('count(l.IDArt) as Can, l.Nombre, l.IDArt, IDPedido, rec.Nombre as NR, nomImp ')
                ->from('lineaspedido as l')
                ->join('teclas as art', 'l.IDArt=art.ID ')
                ->join('familias as fam', 'art.IDFamilia=fam.ID')
                ->join("receptores as rec", 'fam.IDReceptor=rec.ID')
                ->where( "IDPedido=$idp AND l.IDArt=$id AND l.Nombre='$nombre'")
                ->order('Nombre, nomImp')
                ->group('l.IDArt, l.Nombre, l.Precio') 
                ->queryAll();
       
         ImprimirTicket::HacerPedido($infPedido->GetInf(), $pedido, "URGENTEEEE!");
         echo 'success';
            
    }
}
