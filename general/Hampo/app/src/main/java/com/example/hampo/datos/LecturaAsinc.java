package com.example.hampo.datos;

import com.example.hampo.modelo.Lectura;

public interface LecturaAsinc {

    interface EscuchadorElemento{
        void onRespuesta(Lectura lectura);
    }
    void ultimaLectura(String id, String id_jaula, LecturaAsinc.EscuchadorElemento escuchador);

}
