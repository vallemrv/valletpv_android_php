<?php

/**
 * Description of Gastos
 *
 * @author valle
 */
class Gastos extends Registro{
    //put your code here
    Var  $Hora;
    var  $Fecha;
    var  $Descripcion;
    var  $Importe;


    public function tableName() {
        return "gastos";
    }
}
