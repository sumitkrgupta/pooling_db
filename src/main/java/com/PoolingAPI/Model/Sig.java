package com.PoolingAPI.Model;

import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import java.io.Serializable;

@Component
public class Sig implements Serializable {

    private Integer Id;

    private String Token;

    private String Text;

    private boolean IsRouteOfAdmin;

    public Sig() {
        super();
    }

    public Sig(Integer id, String token, String text, boolean isRouteOfAdmin) {
        Id = id;
        Token = token;
        Text = text;
        IsRouteOfAdmin = isRouteOfAdmin;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public boolean getIsRouteOfAdmin() {
        return IsRouteOfAdmin;
    }

    public void setRouteOfAdmin(boolean routeOfAdmin) {
        IsRouteOfAdmin = routeOfAdmin;
    }

    @Override
    public String toString() {
        return "Sig{" +
                "Id=" + Id +
                ", Token='" + Token + '\'' +
                ", Text='" + Text + '\'' +
                ", IsRouteOfAdmin=" + IsRouteOfAdmin +
                '}';
    }
}
