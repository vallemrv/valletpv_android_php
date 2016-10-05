<?php


/**
 * Description of ImprimirTicket
 *
 * @author valle
 */
class ImprimirTicket {
  
                static function PreimprimirTicket ( $rutFicheros, $nombreImpresora,
			                                         $lineas, $now, $mesa, $numCopiasTicket, $totalFactura)
		{
                
                    $docPrint = new DocumentPrint($rutFicheros,$nombreImpresora);
                    $docPrint->IniciarDoc(); 
                    $docPrint->AddLinea();
                    $docPrint->AddLinea("PREIMPRESION DEL TICKET",Alineacion::centro,Tamaño::grande,true);
                    $docPrint->AddLinea();
                    $docPrint->AddLinea("Copia nº ". $numCopiasTicket);
                    $docPrint->AddLinea(sprintf("Mesa: %s ",$mesa));
                    $docPrint->AddLinea();
                    $docPrint->AddLinea("Can  Descripción            Precio   Total");
                    $docPrint->AddLinea("------------------------------------------");
		 
		            foreach($lineas as $articulo)
		            {
		                if($articulo["Precio"] > 0){
				        $precioImp = sprintf("%01.2f", $articulo["Precio"]);
			                $totalImp = sprintf("%01.2f", $articulo["TotalLinea"]);
				        $docPrint->AddLinea(sprintf("%4s %-23s %6s %6s",
			                   $articulo["Can"],$articulo["Nombre"], $precioImp,$totalImp));
				         }
		            }
		            
                            
		           $docPrint->AddLinea();
				
				    
 
				    $monedaformat = sprintf("%01.2f ",$totalFactura);
				    $docPrint->AddLinea(sprintf("Total Ticket :        %7s",$monedaformat),
			                 Alineacion::centro,Tamaño::grande,true);
		    	    
                                    
                                    $docPrint->AddLinea();$docPrint->AddLinea();
				    $docPrint->AddLinea($now,Alineacion::centro);
			            $docPrint->AddLinea();$docPrint->AddLinea();$docPrint->AddLinea();
					
			 
			   $docPrint->ImprimirDoc();
                           unset($docPrint);
		}

	        static	function ImprimeResumen($rutFicheros, $nombreImpresora, $resumen, $nomRes){
		      
			   $docPrint = new DocumentPrint($rutFicheros,$nombreImpresora) ;
                           $docPrint->IniciarDoc(); 
			   

		            $docPrint->ImprimirLogo();
                            $docPrint->AddLinea(sprintf("Resumen  %s",$nomRes));
		            $docPrint->AddLinea(sprintf("Fecha impresion: %s", date('d/m/Y')." - ".date("H:m")));
		            $docPrint->AddLinea("------------------------------------------");
		            $docPrint->AddLinea();
		            foreach ($resumen as $linea)
		            {
		                $docPrint->AddLinea($linea);
		            }
			    $docPrint->AddLinea();$docPrint->AddLinea();$docPrint->AddLinea();	
		            $docPrint->ImprimirDoc();
                            unset($docPrint);
		}
		
	        static	function ImprimirInforme($rutficheros,  $nombreImpresora, $resumen, $Titulo){
			
			  $docPrint = new DocumentPrint($rutFicheros,$nombreImpresora);
                          $docPrint->IniciarDoc(); 
			  $docPrint->ImprimirLogo();
                            

                            $docPrint->AddLinea(sprintf($Titulo));
                            $docPrint->AddLinea("------------------------------------------");
                            $docPrint->AddLinea("");
                            foreach ($resumen as $linea)
                            {
                                $docPrint->AddLinea($linea);
                            }
				
			    $docPrint->AddLinea();$docPrint->AddLinea();$docPrint->AddLinea();	
			    $docPrint->ImprimirDoc();
			    unset($docPrint);
		}
		
	        static	function AbrirCajon($rutFicheros, $nombreImpresora){
			$docPrint = new DocumentPrint($rutFicheros,$nombreImpresora);
                        $docPrint->IniciarDoc(); 
			$docPrint->AbrirCajon();
                        unset($docPrint); 
	         }
		
		
			
		
	        static	function Ticket ($rutFicheros, $nomImpresora, $ticket)
		{
			
                    $docPrint = new DocumentPrint($rutFicheros,$nomImpresora);
                    $docPrint->IniciarDoc(); 

                      $docPrint->ImprimirLogo();
                      $docPrint->AddLinea(sprintf("Camarero: %s",$ticket["Camarero"]));
                      $docPrint->AddLinea(sprintf("Mesa: %s ",$ticket["Mesa"]));
                      $docPrint->AddLinea();
                      $docPrint->AddLinea("Can  Descripción            Precio   Total");
                      $docPrint->AddLinea("------------------------------------------");

			$lineas =  $ticket["Lineas"];
		           
                        foreach ($lineas as $linea)
		        {
                          if($linea["Precio"]>0){
                             $precioImp = sprintf("%01.2f", $linea["Precio"]);
                             $totalImp = sprintf("%01.2f", $linea["TotalLinea"]);
                                     $docPrint->AddLinea(sprintf("%4s %-23s %6s %6s",
                                $linea["Can"], $linea["Nombre"],$precioImp,$totalImp));
                              }
		      }
		
		      $docPrint->AddLinea();
				
		        $monedaformat = sprintf("%01.2f",$ticket["Importe"]);
			$docPrint->AddLinea(sprintf("Total Factura :       %7s ", $monedaformat),Alineacion::izquierda,Tamaño::grande,true);
		    	        $monedaformat = sprintf("%01.2f",$ticket["Cambio"]);
			        $docPrint->AddLinea(
	                          sprintf("Entrega :        %7s ", $monedaformat),Alineacion::izquierda);
				$monedaformat = sprintf("%01.2f",$ticket["Importe"]-$ticket["Cambio"]);
				$docPrint->AddLinea(
	                          sprintf("Cambio  :        %7s ", str_replace("-", "", $monedaformat)),Alineacion::izquierda);
				     
				    $docPrint->AddLinea();
					
				    $docPrint->AddLinea(sprintf("%s", $ticket["Fecha"]),Alineacion::centro);
                                    $docPrint->AddLinea();
			           
                                    $docPrint->AddLinea("Factura simplificada",Alineacion::centro);
                                    $docPrint->AddLinea(sprintf("Num: %s", $ticket["ID"]),Alineacion::centro);
                                    $docPrint->AddLinea("Iva incluido",Alineacion::centro);
                                    $docPrint->AddLinea();
			            $docPrint->AddLinea("Gracias por su visita",Alineacion::centro);
                                    $docPrint->AddLinea();
			              

			   $docPrint->ImprimirDoc();
			   unset($docPrint);
		}
                
               
                
               static function HacerPedido($imfPedido, $pedido, $titulo=""){
                   $receptores = array();
                   $rutFicheros = Yii::app()->params["dirPrint"].  uniqid();
                   
                  foreach ($pedido as $linea){
                       $key= $linea["nomImp"].$linea["NR"];
                       if(!array_key_exists($key,$receptores)) {
                           $receptores[$key] =array("imp"=>$linea["nomImp"],"linea"=>array());
                       }
                       $receptores[$key]["linea"][]=$linea;
                   }
                   
                   
                  foreach ($receptores as $imp){
                        $nomImpresora = $imp["imp"];
                       
                        $docPrint = new DocumentPrint($rutFicheros,$nomImpresora);
                        $docPrint->IniciarDoc();
                        $docPrint->AddLinea($titulo. "  ".$imfPedido["Mesa"],Alineacion::centro,Tamaño::grande,true);
                        $docPrint->AddLinea();
				
		    	$docPrint->AddLinea("Por :".$imfPedido["Nombre"],Alineacion::centro);
                        $docPrint->AddLinea(" Hora ".$imfPedido["Hora"],Alineacion::centro);
                        $docPrint->AddLinea();$docPrint->AddLinea();
                        
                        foreach ($imp["linea"] as $l)
                            $docPrint->AddLinea(sprintf("%s %s",$l["Can"],$l["Nombre"]), Alineacion::centro,  Tamaño::grande);
                        
		    	$docPrint->AddLinea();
                        $docPrint->ImprimirDoc();
			unset($docPrint);
                   }
               }

}
