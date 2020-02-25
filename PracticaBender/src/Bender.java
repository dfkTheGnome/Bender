
public class Bender {
	int posY = 0;
	int posX = 0;
	int mPosY = 0;
	int mPosX = 0;
	char[][] mapa;
	Bender(String mapa){
	      this.mapa = mapBuilder(mapa);
	    }
	
	String run(){
	        String road = "";
	        Integer mira = 0;
	      
	        //este bucle inicia el camino de Bender, no se detendrá hasta que llegue a la mete
	        //cuyas coordenadas estan almacenadas en mPosY y en mPosX

	        while (!(posX == mPosX && posY == mPosY)) {
	    
	            while (tryForward(posY,posX,mira) == '#') {
	                mira++;
	                if (mira > 3) mira = 0;
	               
	            }

	            road += forward(mira,road);
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
	        return r;
	    }
	String bestRun(){
	        return null;
	    }
	
	char[][] mapBuilder(String m){
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
              
               mapa[i][j] = m.charAt(cont);
               cont++;
           }
       }

       return mapa;
   }
}
