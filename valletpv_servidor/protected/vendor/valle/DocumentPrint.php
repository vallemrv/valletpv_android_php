<?php
/**
 * Description of DocumentPrint
 *
 * @author valle
 */

abstract class Alineacion
{
    const centro = 0;
    const derecha = 1;
    const izquierda = 2;

}

abstract class Tamaño
{
    const pequeña = 0;
    const normal = 1;
    const grande = 2;

}

class DocumentPrint {
 	
          private $agregarLineas = "";
	  private $tipoEuro = "";	
	  private $saltoDeLinea = "";
	  private $iniciarImp = "";	
	  private $resaltado = "";
	  private $cortarPapel = "";
          private $centrado ="";
          private $derecha = "";	
	  private $izquierda ="";	
	  private $pequeña = "";
	  private $normal = "";	
	  private $grande = "";	
	  private $grandeNegrita = "";
	  private $abrirCajon = "";
	  private $impLogo = ""; 
          private $saltoLineaFinal = "";                  
          private $fichero = null;
          private $nomImp= "";
          private $path = "";
          
          
          public function DocumentPrint($path, $nomImp){
             $this->agregarLineas=chr(27). chr(100). chr(6);
             $this->tipoEuro=chr(27).chr(116).chr(16);
             $this->saltoDeLinea=chr(27).chr(100).chr(1);
             $this->iniciarImp=chr(27).chr(64);
             $this->resaltado=chr(27).chr(33).chr(8);
             $this->cortarPapel=chr(29).chr(86).chr(1);
             $this->centrado=chr(27).chr(97).chr(1);
             $this->derecha=chr(27).chr(97).chr(0);
             $this->izquierda=chr(27).chr(97).chr(2);
             $this->pegueña=chr(27).chr(77).chr(48);
             $this->normal=chr(27).chr(77).chr(49);
             $this->grande=chr(27).chr(33).chr(16);
             $this->grandeNegrita=chr(27).chr(33).chr(24);
             $this->abrirCajon=chr(27).chr(112).chr(48);
             $this->impLogo=chr(28).chr(112).chr(1).chr(48);
             $this->saltoLineaFinal =  chr(27). chr(74). chr(255);
                       
                         
             $this->path = $path;
             $this->nomImp = $nomImp;
             if(($this->fichero = fopen($path, "a"))===FALSE){
                   die('No se puedo Imprimir, Verifique su conexion con el Terminal');
              }
          }
	  
	   public function __destruct()
	   {
		 fclose($this->fichero);
	   	 if(file_exists($this->path )) unlink($this->path);
	   }
	   
	   public function IniciarDoc(){
                 fwrite($this->fichero, $this->iniciarImp);
           }
		
           function AddBytes($b){
		    fwrite($this->fichero, $b);
	   }
		
          function AddALineamiento( $aling){
			switch($aling){
			    case Alineacion::centro:	
				  $this->AddBytes($this->centrado);
				break;
				case Alineacion::izquierda:	
				  $this->AddBytes($this->izquierda);
				break;
				case Alineacion::derecha:	
				   $this->AddBytes($this->derecha);
				break;
			}
		}
		
             function AddNegrita(){
			$this->AddBytes($this->resaltado);
             }
		
	    function AddTamaño($tamaño){
		     switch($tamaño){
			    case Tamaño::normal:	
				  $this->AddBytes($this->normal);
				break;
				case Tamaño::pequeña:	
				  $this->AddBytes($this->pequeña);
				break;
				case Tamaño::grande:	
				   $this->AddBytes($this->grande);
				break;
			}	
		}
		
             function ImprimirLogo(){
	           $this->AddBytes($this->impLogo);
              }
		
              function AbrirCajon(){
		   $this->AddBytes($this->abrirCajon);
                   $this->AddBytes($this->iniciarImp);
                   $salida = shell_exec('lpr -P '.$this->nomImp.' '.$this->path); //lpr->puerto impresora, imprimir archivo PRN  
             }
		
	     function AddLinea($str=null,$aling=null, $t=null, $negrita=null){
                
                 if($aling!==null) $this->AddALineamiento($aling);
                 
                 if($negrita!==null && ($t==Tamaño::grande))$this->AddBytes($this->grandeNegrita);
			else  if ($negrita !== null && ($t!=Tamaño::grande)) AddNegrita();
		 if($t !== null) $this->AddTamaño($t);
                 if($str!==null){
                    $this->AddBytes($this->tipoEuro); 
                    $this->AddBytes(mb_convert_encoding($str,"latin1","UTF-8"));
                  }  
                 $this->AddBytes($this->saltoDeLinea);
                 $this->AddBytes($this->iniciarImp);
             }
		
		
	      function ImprimirDoc(){
			$this->AddBytes($this->saltoLineaFinal);
			$this->AddBytes($this->cortarPapel);
                        $salida = shell_exec('lpr -P '.$this->nomImp.' '.$this->path); //lpr->puerto impresora, imprimir archivo PRN  
               }
		
	}

