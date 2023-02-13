package Models;
import java.lang.*;
Public Class Vector {
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
    public Vector(Vector a){
        this.x = a.x;
        this.y = a.y;
    }

    public double dot(Vector a, Vector b){
        double hasil = a.x*b.x + a.y*b.y;
        return hasil;
    }
    public double magnitude(Vector a){
        double kuadrat = a.x*a.x+b.y*b.y;
        double hasil = Math.sqrt(kuadrat);
        return hasil;
    }
    public int sudut(Vector a,Vector b){
        double hitung = (magnitude(a)*magnitude(b))/dot(a,b);
        int hasil = Math.acos(hitung);
        return hasil
    }

}