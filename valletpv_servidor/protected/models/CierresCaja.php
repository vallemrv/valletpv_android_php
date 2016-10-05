<?php


/**
 * Description of CierresCaja
 *
 * @author valle
 */
class CierresCaja extends Registro {
    //put your code here
    public function tableName() {
        return "cierrecaja";
    }
    
    public function getTotalEfectivo(){
        return  Yii::app()->db->createCommand()
                 ->select('SUM(Precio) AS Total')
                 ->from("ticketlineas AS tk")
                 ->join("lineaspedido AS lp", "tk.IDLinea=lp.ID")
                 ->join("ticket", "ticket.ID=tk.IDTicket")
                 ->where("ticket.ID >= ".$this->TicketCom. " AND ticket.ID <= ".$this->TicketFinal ." AND ticket.Entrega > 0")
                 ->queryScalar();
    }
    
    public function getTotalTarjeta(){
         return  Yii::app()->db->createCommand()
                 ->select('SUM(Precio) AS Total')
                 ->from("ticketlineas AS tk")
                 ->join("lineaspedido AS lp", "tk.IDLinea=lp.ID")
                 ->join("ticket", "ticket.ID=tk.IDTicket")
                 ->where("ticket.ID >= ".$this->TicketCom. " AND ticket.ID <= ".$this->TicketFinal ." AND ticket.Entrega = 0")
                 ->queryScalar();
    }
    
    public function getDesgloseVentas(){
         return  Yii::app()->db->createCommand()
                 ->select('COUNT(IDArt) as Can, Nombre, SUM(lp.Precio) AS Total')
                 ->from("ticketlineas AS tk")
                 ->join("lineaspedido AS lp", "tk.IDLinea=lp.ID")
                 ->join("ticket", "ticket.ID=tk.IDTicket")
                 ->group("lp.IDArt, lp.Nombre, lp.Precio")
                 ->order("lp.Nombre")
                 ->where("ticket.ID >= ".$this->TicketCom. " AND ticket.ID <= ".$this->TicketFinal)
                 ->queryAll();
        
    }
    
}
