package Models;

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

    public Vector cross(Vector a,Vector b){
        
    }

}