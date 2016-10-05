<?php


/**
 * Description of Usuarios
 *
 * @author valle
 */
class Usuarios extends Registro {
    //put your code here
    public function tableName() {
        return "usuarios";
    }
    
    public function relations() {
        return array (
            "horarios"=>array(self::HAS_MANY, "HorarioUsr", "IDUsr")
        );
    }
    
    static function getUsuariosMail(){
        $now = date("H:m");
        return Yii::app()->db->createCommand()
                 ->select('Nombre, Apellido, email')
                 ->from("usuarios AS usr")
                 ->join("horario_usr AS hr", "usr.ID=hr.IDUsr")
                 ->where("'$now' >= hr.Hora_ini AND '$now' <= hr.Hora_fin")
                 ->queryAll();
    }
}
