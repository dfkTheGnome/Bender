import java.util.*;

public class Bender {
    //coordenadas robot
    private int posY = 0;
    private int posX = 0;

    //coordenadas meta
    private int mPosY = 0;
    private int mPosX = 0;

    private char[][] mapa;
    private HashMap camino = new HashMap<String,String>();
    private HashMap pasosRepetidos = new HashMap<String,Integer>();

    //posicion de los trasportadores
    private List teleportatorY = new LinkedList();
    private List teleportatorX = new LinkedList();


    Bender(String mapa){
        this.mapa = mapBuilder(mapa);
    }

    char[][] mapBuilder(String stMapa) {

        int contPosY = 0;
        int contPosX;

        int cont = 0;
        int telepCont = 0;

        //en estos bucles se definira el tamaño de la primera dimension del array map
        // y el tamaño de cada uno de los arrays de las casillas de esta dimension
        while (cont < stMapa.length()){

            if (stMapa.charAt(cont) == '\n') contPosY ++;

            cont++;
        }
        contPosY++;

        char[][] mapa = new char[contPosY][];


        cont = 0;
        for (int i = 0; i < contPosY; i++) {
            contPosX = 0;

            //las filas del eje x pueden variar de tamaño
            while (cont < stMapa.length() && stMapa.charAt(cont) != '\n') {
                contPosX++;
                cont++;
            }
            cont++;
            mapa[i] = new char[contPosX];
        }



        cont = 0;
        for (int i = 0; i < contPosY; i++) {
            for (int j = 0; j < mapa[i].length; j++) {
                if (stMapa.charAt(cont) == '\n') cont++;

                if(stMapa.charAt(cont) == 'X'){
                    posY = i;
                    posX = j;
                }

                if(stMapa.charAt(cont) == '$'){
                    mPosY = i;
                    mPosX = j;
                }
                if(stMapa.charAt(cont) == 'T'){
                    teleportatorY.add(i);
                    teleportatorX.add(j);
                    telepCont++;
                }

                mapa[i][j] = stMapa.charAt(cont);
                cont++;
            }
        }

        // control de valided del mapa
        if (mPosY == 0) {
            System.out.println("Este mapa no tiene meta, no se puede resolver");
            return null;
        }
        if (telepCont == 1){
            System.out.println("¡ERROR! Este mapa solo tiene un transportador");
        }

        return mapa;
    }


    String run(){
        String road = "";
        Integer mira = 0;
        int[] nextT;
        int order = 1;
        int count;
        //este bucle inicia el camino de Bender, no se detendrá hasta que llegue a la mete
        //cuyas coordenadas estan almacenadas en mPosY y en mPosX

        while (!(posX == mPosX && posY == mPosY)) {
            count = 0;
            while (tryForward(posY,posX,mira) == '#') {
                mira++;
                if (mira > 3) mira = 0;
                if (mira < 0) mira = 3;
                if (count == 0){
                    if (order == 1) mira = 0;
                    else mira = 2;
                }
                count++;
            }

            road += forward(mira,road);
            if (mapa[posY][posX] == 'T'){
                nextT = teleport(posY,posX);
                posY = nextT[0];
                posX = nextT[1];
            }
            if (mapa[posY][posX] == 'I') {
                if (order == 1) order = -1;
                else order = 1;
            }
            if (pasosRepetidos.containsValue(5)) return null;
        }

        return road;
    }


    // Esta funcion es utilizada para saber que valor tiene la casilla en la direccion donde esta mirando el robot
    private char tryForward(int y, int x, int mira){

        switch (mira){

            case 0: y++; break;
            case 1: x++; break;
            case 2:y--; break;
            case 3: x--; break;
        }
        return mapa[y][x];
    }


    //forward es una funcion similar a tryForward solo que en este caso avanzará
    private String forward(int mira, String r){

        switch (mira) {

            case 0:
                posY++;
                r = "S";
                break;
            case 1:
                posX++;
                r = "E";
                break;
            case 2:
                posY--;
                r = "N";
                break;
            case 3:
                posX--;
                r = "W";
                break;
        }

        if (camino.containsKey(posY+""+posX) && camino.get(posY+""+posX).equals(r)){
            pasosRepetidos.put(posY+""+posX, (int) pasosRepetidos.get(posY+""+posX) + 1);
        }else{
            camino.put(posY+""+posX,r);
            pasosRepetidos.put(posY+""+posX,0);
        }

        return r;
    }


    // la funcion determinara la posicion del transportador al cual el robot se transportara
    // en funcion del teletransportador en el que ha entrado
    int[] teleport (int y, int x){
        Iterator teleY = teleportatorY.iterator();
        Iterator teleX = teleportatorX.iterator();
        int intX;
        int intY;

        int[] minCoords = new int[2];
        int length;
        int tempLY;
        int tempLX;
        int minLength = 100;
      /*  while (teleX.hasNext()){
            System.out.println(teleY.next()+" "+teleX.next());
        }*/
        while (teleY.hasNext()) {

            intY = (int)teleY.next();
            intX = (int)teleX.next();
            tempLY = y - intY;
            tempLX = x - intX;
            if(tempLY < 0) tempLY *= -1;
            if(tempLX < 0) tempLX *= -1;
            length = tempLY + tempLX;
            if (length < 0 ) length *= -1;
            //    System.out.println(minLength+" "+length);
            if (length == minLength){
                minCoords = equalsTeleports(posY,posX,minCoords[0], minCoords[1],intY,intX);
                //  System.out.println(minCoords[0]+" "+minCoords[1]);
            } else if ( length  < minLength && !(intY == posY && intX == posX)){

                minCoords[0] = intY;
                minCoords[1] = intX;
                minLength = length;
                if (minLength < 0 ) minLength *= -1;
            }
        }
        //  System.out.println("teletransportado de "+posY+" "+posX+" a: "+minCoords[0]+" "+minCoords[1]+" distancia: "+minLength);
        return minCoords;
    }


    //esta funcion se ejecutara cuando haya dos o mas transportadores a la misma distancia
    int[] equalsTeleports(int centerY , int centerX , int y1 , int x1 , int y2 , int x2){
        int[] result = new int[2];

        int minY = Math.min(y1,y2);
        int maxY = Math.max(y1,y2);
        int minX = Math.min(x1,x2);
        int maxX = Math.max(x1,x2);

        //estas condiciones son para asegurarnos de que el teletransportador inicial
        //esta dentro del area del mapa delimitada por los teletransportadores objetivos

        if (minY > centerY) minY = centerY;
        if (maxY < centerY) maxY = centerY;
        if (minX > centerX) minX = centerX;
        if (maxX < centerX) maxX = centerX;

        //en estos bucles recorreremos el borde del area
        for (int i = minY; i <= maxY; i++) {
            for (int j = maxX; j >= minX; j--) {
                if (mapa[i][j] == 'T' && !(i == centerY && j == centerX)){
                    result[0] = i;
                    result[1] = j;
                    if (j == maxX || i == maxY) return result;
                }
            }
        }

        if(result[0] != 0 ) return result;

        //el anterior for no recorre la parte a la izquierda del borde superior
        //ya que empieza desde centerX
        for (int j = minX; j < centerX; j++) {
            if(mapa[minY][j] == 'T'){
                result[0] = minY;
                result[1] = j;
                return result;
            }
        }

        return result;
    }

    String bestRun(){
        return null;
    }

}
