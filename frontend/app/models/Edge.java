package models;

public class Edge {
    public Long source;
    public Long target;


    public Edge(){
    	
    };
    public Edge(Long source, Long target) {
        this.source = source;
        this.target = target;
    }
}
