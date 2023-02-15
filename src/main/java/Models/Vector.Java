package Models;

public class Vector {
    private double x;
    private double y;

    public Vector(){
        this.x = 0;
        this.y = 0;
    }
    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector(Position a){
        this.x = a.x;
        this.y = a.y;
    }

    public Vector(Vector a){
        this.x = a.x;
        this.y = a.y;
    }

    public double magnitude(){
        double kuadrat = this.x*this.x+this.y*this.y;
        double hasil = Math.sqrt(kuadrat);
        return hasil;
    }

    public static Vector min(Vector a, Vector b){
        Vector tmp = new Vector(a.x-b.x,a.y-b.y);
        return tmp;
    }

    public static double dot(Vector a, Vector b){
        double hasil = a.x*b.x + a.y*b.y;
        return hasil;
    }

    public static double sudut(Vector a,Vector b){
        double hitung = dot(a,b)/(a.magnitude()*b.magnitude());
        double hasil = Math.acos(hitung);
        return hasil;
    }

    public static double sudut(Position a, Position b, Position c){
        Vector av = new Vector(a);
        Vector bv = new Vector(b);
        Vector cv = new Vector(c);
        return sudut(min(bv,av),min(cv,bv));
    }
}