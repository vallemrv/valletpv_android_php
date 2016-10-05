<?php


/**
 * Description of GesImpresion
 *
 * @author valle
 */
class GesImpresion {
    //put your code here
    static function ImprimirTicket($id, $idc){
        $ticket = Ticket::model("Ticket")->findByPk($id);
        $cam = Camareros::model("Camareros")->findByPk($idc);
        
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
            $rutFicheros = Yii::app()->params["dirPrint"].$cam->Nombre;
            $nombreImpresora = Receptores::model("Receptores")->find("Nombre='Ticket'")->nomImp;
            ImprimirTicket::AbrirCajon($rutFicheros.  uniqid(), $nombreImpresora);
            ImprimirTicket::Ticket($rutFicheros, $nombreImpresora, $res);
          }
    
        
    }
    
    static function ImprimirCambio($empresa, $importe){
                  
                $rutFicheros = Yii::app()->params["dirPrint"].  uniqid();

                $nomImpresora = Receptores::model("Receptores")->find("Nombre='Ticket'")->nomImp;

                $docPrint = new DocumentPrint($rutFicheros,$nomImpresora);
                $docPrint->IniciarDoc();
                $docPrint->AddLinea("Cierre de Caja",Alineacion::centro,Tamaño::grande,true);
                $docPrint->AddLinea();

                $docPrint->AddLinea("Empresa : ".$empresa,Alineacion::centro);
                $docPrint->AddLinea("Hora y fecha de cierre ".date("d/m/Y")."-".date("H:m"),Alineacion::centro);
                $docPrint->AddLinea();$docPrint->AddLinea();
                $docPrint->AddLinea(sprintf("%s %01.2f","Cambio: ", $importe), Alineacion::centro,  Tamaño::grande);


                $docPrint->AddLinea();$docPrint->AddLinea();$docPrint->AddLinea(); 
                $docPrint->ImprimirDoc();
                unset($docPrint);
                   
    }
    
    static function ImprimirRetirada($empresa, $desglose){
                $rutFicheros = Yii::app()->params["dirPrint"].  uniqid();

                $nomImpresora = Receptores::model("Receptores")->find("Nombre='Ticket'")->nomImp;

                $docPrint = new DocumentPrint($rutFicheros,$nomImpresora);
                $docPrint->IniciarDoc();
                $docPrint->AddLinea("Cierre de Caja",Alineacion::centro,Tamaño::grande,true);
                $docPrint->AddLinea();

                $docPrint->AddLinea("Empresa : ".$empresa,Alineacion::centro);
                $docPrint->AddLinea("Hora y fecha de cierre ".date("d/m/Y")."-".date("H:m"),Alineacion::centro);
                $docPrint->AddLinea();$docPrint->AddLinea();
                foreach ($desglose as $des)
                  $docPrint->AddLinea(sprintf("Retirar %6s %-10s de %6s ",$des["Can"], ($des["Moneda"]>2 ? "Billetes" : "Monedas"),
                          sprintf ("%01.2f",$des["Moneda"])), Alineacion::centro,  Tamaño::pequeña);
                 

                $docPrint->AddLinea();$docPrint->AddLinea();$docPrint->AddLinea(); 
                $docPrint->ImprimirDoc();
                unset($docPrint);
    }
    
    
}
