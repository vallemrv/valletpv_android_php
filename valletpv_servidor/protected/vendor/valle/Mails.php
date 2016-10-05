<?php

/**
 * Description of Mails
 *
 * @author valle
 */
class Mails {
    
    
    
     public static function sendCierreCaja($para, $desglose){
        // Varios destinatarios
        $de = 'info@elbrasilia.com';
          
        // subject
        
        $titulo = 'Cierre '. Mails::getDiaSemana() ." - ". date("d/m/Y");

        // message
        $mensaje = sprintf('
        <html lang="es">
        <head>
          <title>Cierre de '.Yii::app()->params["empresa"].'</title>
        </head>
        <body>
          <h4>Cierre de caja en '.Yii::app()->params["empresa"].'</h4>
          <p> Total efectivo: %01.2f € </p>
          <p> Total tarjeta: %01.2f € </p>
          <p> Pagos por caja: %01.2f € </p>
          <p> Total caja del dia: %01.2f € </p>
          <p> Caja real: %01.2f € </p>
          <p> Descuadre: %01.2f € </p>
          <h4>Desglose de gastos </h4> ', $desglose["TotalEfectivo"],$desglose["TotalTarjeta"], $desglose["gastos"],
                                     $desglose["TotalCaja"], $desglose["CajaReal"],$desglose["Descuadre"]);
        
          foreach ($desglose["des_gastos"] as $gasto) $mensaje .= sprintf ("<p> %-50s  %6s € </p>",$gasto["Descripcion"], sprintf ("%01.2f", $gasto["Importe"]) );
          
          $mensaje.="<h4>Desglose de ventas </h4>";
          
          foreach ($desglose["des_ventas"] as $venta) $mensaje .= sprintf ("<p> %6s  %-100s  %7s € </p>", $venta["Can"], $venta["Nombre"], sprintf ("%01.2f", $venta["Total"]) );
         
          $mensaje.='</body> </html> ';

        // Para enviar un correo HTML mail, la cabecera Content-type debe fijarse
        $cabeceras  = 'MIME-Version: 1.0' . "\r\n";
        $cabeceras .= 'Content-type: text/html; charset=utf-8' . "\r\n";

        // Cabeceras adicionales
        $cabeceras .= 'To: '.$para["Nombre"].' '.$para["Apellido"].' <'.$para["email"].'>' . "\r\n";
        $cabeceras .= 'From: ' . Mails::getDiaSemana() ." - ". Mails::getFranja(). '  <info@elbrasilia.com>' . "\r\n";

        // Mail it
        mail($para["email"], $titulo, $mensaje, $cabeceras);
        
       }
    
       static function getFranja(){
           $hora = date("H:m");
           if(($hora < "06:00") || ($hora >= "21:30" && $hora <= "23:59")) return "noche";
           if(($hora >= "19:00" && $hora <= "21:29")) return "tarde";
           if(($hora >= "15:00" && $hora <= "18:59")) return "medio dia";
           if(($hora >= "06:01" && $hora <= "14:59")) return "mañana";
           return "";
       }
       
       static function  getDiaSemana(){
           $hora = date("H:m");
           $d = date("w");
           $d = $hora < "06:00" ? $d-1 : $d;
           switch ($d){
               case 0:
                   return "Domingo";
                   break;
                 case 1:
                   return "Lunes";
                   break;
                 case 2:
                   return "Martes";
                   break;
                 case 3:
                   return "Miercoles";
                   break;
                 case 4:
                   return "Jueves";
                   break;
                 case 5:
                   return "Viernes";
                   break;
                 case 6:
                 case -1:
                   return "Sabado";
                  break;
              default :
                   return "Administrador";
                   break;
           }
       }
 }
