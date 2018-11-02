package models;

import java.util.*;

public class Author {

    private Long id;
    private String name;

    private List<String> followers;
    private List<String> followees;

    public Author() {
        followers = new ArrayList<String>();
        followees = new ArrayList<String>();
    }

    public Long getId() {
       return id;
   }

    public void setId(Long id) {
       this.id = id;
   }

    public String getName() {
       return name;
   }

    public void setName(String name) {
       this.name = name;
   }

    public int getFollowerCount() {
        return followers.size();
    }

    public int getFolloweeCount() {
        return followees.size();
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowees() {
        return followees;
    }

    public void addFollower(String name) {
        followers.add(name);
    }

    public void removeFollower(String name) {
        followers.remove(name);
    }

    public void addFollowee(String name) {
        followees.add(name);
    }

    public void removeFollowee(String name) {
        followees.remove(name);
    }

}