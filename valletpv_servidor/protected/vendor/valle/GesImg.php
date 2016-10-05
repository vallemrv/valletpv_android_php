<?php

/**
 * Gesitona la subida de imagenes, el tamaño, la forma de visualizar, recorte, borrado, etc
 * @author elvalle
 */
class GesImg {
    
    
    public static function removeImg($filePath){
        if (file_exists($filePath)) 
	{
           unlink($filePath); return true;
        }
        return false;
    }
    
    public static function crearDirectorios($dirBase){
        mkdir($dirBase,0777);
        mkdir($dirBase.'/fotos',0777);
        mkdir($dirBase.'/videos',0777);
    }
    
   public static function uploadImg($output_dir){
        if(isset($_FILES["myfile"]))
        {
                $ret = array();

                $error =$_FILES["myfile"]["error"];
                //You need to handle  both cases
                //If Any browser does not support serializing of multiple files using FormData() 
                if(!is_array($_FILES["myfile"]["name"])) //single file
                {
                        $fileName = $_FILES["myfile"]["name"];
                        move_uploaded_file($_FILES["myfile"]["tmp_name"],$output_dir.$fileName);
                        $ret[]= $fileName;
                }
                else  //Multiple files, file[]
                {
                  $fileCount = count($_FILES["myfile"]["name"]);
                  for($i=0; $i < $fileCount; $i++)
                  {
                        $fileName = $_FILES["myfile"]["name"][$i];
                        move_uploaded_file($_FILES["myfile"]["tmp_name"][$i],$output_dir.$fileName);
                        $ret[]= $fileName;
                  }

                }
            return $ret;
         }
    }
     
    public static function Move($fileSource, $fileDest){
        if(copy($fileSource, $fileDest)){
            GesImg::removeImg($fileSource);
        }
    }
    
    public static function GetUidName ($nomFile){
            $pathinfo = pathinfo($nomFile);
            return uniqid().($pathinfo["extension"]? '.'.$pathinfo["extension"]:'');
    }
    
    public static function Preview($filePath){
        $ruta_imagen = $filePath;

        $miniatura_ancho_maximo = 200;
        $miniatura_alto_maximo = 200;
        $info_imagen = getimagesize($ruta_imagen);
        $imagen_ancho = $info_imagen[0];
        $imagen_alto = $info_imagen[1];
        $imagen_tipo = $info_imagen['mime'];
        
        $lienzo = imagecreatetruecolor( $miniatura_ancho_maximo, $miniatura_alto_maximo );
        
        switch ( $imagen_tipo ){
            case "image/jpg":
            case "image/jpeg":
              $imagen = imagecreatefromjpeg( $ruta_imagen );
              break;
            case "image/png":
              $imagen = imagecreatefrompng( $ruta_imagen );
              break;
            case "image/gif":
              $imagen = imagecreatefromgif( $ruta_imagen );
              break;
        }
        
        $proporcion_imagen = $imagen_ancho / $imagen_alto;
        $proporcion_miniatura = $miniatura_ancho_maximo / $miniatura_alto_maximo;
        
        if ( $proporcion_imagen > $proporcion_miniatura ){
            $miniatura_ancho = $miniatura_ancho_maximo;
            $miniatura_alto = $miniatura_ancho_maximo / $proporcion_imagen;
          } else if ( $proporcion_imagen < $proporcion_miniatura ){
            $miniatura_ancho = $miniatura_alto_maximo * $proporcion_imagen;
            $miniatura_alto = $miniatura_alto_maximo;
          } else {
            $miniatura_ancho = $miniatura_ancho_maximo;
            $miniatura_alto = $miniatura_alto_maximo;
          }
          
        $lienzo = imagecreatetruecolor( $miniatura_ancho, $miniatura_alto );
        imagecopyresampled($lienzo, $imagen, 0, 0, 0, 0, $miniatura_ancho, $miniatura_alto, $imagen_ancho, $imagen_alto);
        header('Content-type: ' . $imagen_tipo);
        imagejpeg($lienzo);  
        imagedestroy($lienzo);
        imagedestroy($imagen);
    }
    
     public static function Resize($path, $filename, $ancho=900, $alto=600){
        $ruta_imagen = $path.$filename;

        $miniatura_ancho_maximo = $ancho;
        $miniatura_alto_maximo = $alto;
        $info_imagen = getimagesize($ruta_imagen);
        $imagen_ancho = $info_imagen[0];
        $imagen_alto = $info_imagen[1];
        $imagen_tipo = $info_imagen['mime'];
        
        $lienzo = imagecreatetruecolor( $miniatura_ancho_maximo, $miniatura_alto_maximo );
        
        switch ( $imagen_tipo ){
            case "image/jpg":
            case "image/jpeg":
              $imagen = imagecreatefromjpeg( $ruta_imagen );
              break;
            case "image/png":
              $imagen = imagecreatefrompng( $ruta_imagen );
              break;
            case "image/gif":
              $imagen = imagecreatefromgif( $ruta_imagen );
              break;
        }
        
        $proporcion_imagen = $imagen_ancho / $imagen_alto;
        $proporcion_miniatura = $miniatura_ancho_maximo / $miniatura_alto_maximo;
        
        if ( $proporcion_imagen > $proporcion_miniatura ){
            $miniatura_ancho = $miniatura_ancho_maximo;
            $miniatura_alto = $miniatura_ancho_maximo / $proporcion_imagen;
          } else if ( $proporcion_imagen < $proporcion_miniatura ){
            $miniatura_ancho = $miniatura_alto_maximo * $proporcion_imagen;
            $miniatura_alto = $miniatura_alto_maximo;
          } else {
            $miniatura_ancho = $miniatura_ancho_maximo;
            $miniatura_alto = $miniatura_alto_maximo;
          }
          
        $lienzo = imagecreatetruecolor( $miniatura_ancho, $miniatura_alto );
        imagecopyresampled($lienzo, $imagen, 0, 0, 0, 0, $miniatura_ancho, $miniatura_alto, $imagen_ancho, $imagen_alto);
        $pathinfo = pathinfo($filename);
        $nom = $pathinfo["filename"]   .'.jpg'; 
        imagejpeg($lienzo, $path.$nom);    
        imagedestroy($lienzo);
        imagedestroy($imagen);
        return $nom;
    }
}
