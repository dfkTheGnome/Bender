import javax.swing.text.html.HTMLDocument;
import java.sql.DriverManager;
import java.util.*;

public class Bender {
    int posY = 0;
    int posX = 0;
    int mPosY = 0;
    int mPosX = 0;
    char[][] mapa;
    List teleportatorY = new LinkedList();
    List teleportatorX = new LinkedList();
    public static void main(String[] args) {
        String mapa = "" +
                "#######\n" +
                "#$    #\n" +
                "# X   #\n" +
                "# T   #\n" +
                "#     #\n" +
                "#     #\n" +
                "#   T #\n" +
                "#     #\n" +
                "#     #\n" +
                "#     #\n" +
                "#     #\n" +
                "#T   T#\n" +
                "#     #\n" +
                "#######";
       Bender hola = new Bender(mapa);

      String road =  hola.run();
    }
    Bender(String mapa){
      this.mapa = mapBuilder(mapa);
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
            if (road.length() > 100000) return null;
        }

        return road;
    }


    char tryForward(int y , int x, int mira){

        switch (mira){

            case 0: y++; break;
            case 1: x++; break;
            case 2:y--; break;
            case 3: x--; break;
        }
        return mapa[y][x];
    }

    String forward(int mira, String r){

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
        System.out.println(r);
        return r;
    }

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
            System.out.println(minLength+" "+length);
            if (length == minLength){
                minCoords = equalsTeleports(posY,posX,minCoords[0], minCoords[1],intY,intX);
                System.out.println(minCoords[0]+" "+minCoords[1]);
            } else if ( length  < minLength && !(intY == posY && intX == posX)){

                minCoords[0] = intY;
                minCoords[1] = intX;
                minLength = length;
                if (minLength < 0 ) minLength *= -1;
            }
        }
        System.out.println("teletransportado de "+posY+" "+posX+" a: "+minCoords[0]+" "+minCoords[1]+" distancia: "+minLength);
        return minCoords;
    }

    int[] equalsTeleports(int centerY , int centerX , int y1 , int x1 , int y2 , int x2){
        int minY = Math.min(y1,y2);
        int maxY = Math.max(y1,y2);
        int minX = Math.min(x1,x2);
        int maxX = Math.max(x1,x2);

        if (minY < 0) minY = 0;
        if (minX < 0) minX = 0;
        if (maxY >= mapa.length) maxY = mapa.length -1;
        if (maxX >= mapa[0].length) maxX = mapa[0].length -1;
        if (minY > centerY) minY = centerY;
        if (maxY < centerY) maxY = centerY;
        if (minX > centerX) minX = centerX;
        if (maxX < centerX) maxX = centerX;

        int[] result = new int[2];
        System.out.println("hola");
        for (int i = minY; i <= maxY; i++) {
            for (int j = maxX; j >= minX; j--) {
                if (mapa[i][j] == 'T' && !(i == centerY && j == centerX)){
                    result[0] = i;
                    result[1] = j;
                    if (j == maxX || i == maxY) return result;
                }
            }
        }

        return result;
    }

    String bestRun(){
        return null;
    }
    char[][] mapBuilder(String m) {
        int contPosY = 0;
        int contPosX;
         //en estos bucles se difinira el tamaño de la primera dimension del array map
         // y el tamaño de cada uno de los arrays de las casillas de esta dimension
        int cont = 0;
        while (cont < m.length()){
            if (m.charAt(cont) == '\n') contPosY ++;
            cont++;
        }
        contPosY++;
        char[][] mapa = new char[contPosY][];

        cont = 0;
        for (int i = 0; i < contPosY; i++) {
            contPosX = 0;

            while (cont < m.length() && m.charAt(cont) != '\n') {
                contPosX++;
                cont++;
            }
            cont++;
            mapa[i] = new char[contPosX];
        }
        cont = 0;
        for (int i = 0; i < contPosY; i++) {
            for (int j = 0; j < mapa[i].length; j++) {
                if (m.charAt(cont) == '\n') cont++;

                if(m.charAt(cont) == 'X'){
                    posY = i;
                    posX = j;
                }

                if(m.charAt(cont) == '$'){
                    mPosY = i;
                    mPosX = j;
                }
                if(m.charAt(cont) == 'T'){
                    teleportatorY.add(i);
                    teleportatorX.add(j);
                }

                mapa[i][j] = m.charAt(cont);
                cont++;
            }
        }

        return mapa;
    }

}

