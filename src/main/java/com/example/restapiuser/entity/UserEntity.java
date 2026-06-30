package com.example.restapiuser.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="TUSER")
public class UserEntity {
    @Id
    @Column(name="USERID", length=50, nullable = false )
    private String userid;

    @Column(name="PASSWD", length=100, nullable = false )
    private String passwd;

    @Column(name="USERNAME", length=100, nullable = false )
    private String username;

    @Column(name="EMAIL", length=200  )
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name="ROLE", length=20)
    private Role role = Role.USER;

    @Column(name="ENABLED")
    private Boolean enabled = true;

    @Column(name="INDATE", nullable = false, updatable = false )
    private LocalDateTime indate;

    // 기본생성자
    public UserEntity() {}

    // 생성자
    public UserEntity(String userid, String passwd,
                      String username, String email) {
        this(userid, passwd, username, email, Role.USER);
    }

    public UserEntity(String userid, String passwd,
                      String username, String email, Role role) {
        this.userid = userid;
        this.passwd = passwd;
        this.username = username;
        this.email = email;
        this.role = role;
        this.enabled = true;
    }

    @PrePersist
    public void prePersist() {
        if (this.indate == null) {
            this.indate = LocalDateTime.now();
        }
        if (this.role == null) {
            this.role = Role.USER;
        }
        this.enabled = true;
    }

    // Getter
    public String getUserid() {
        return userid;
    }
    public String getPasswd() {
        return passwd;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public Role getRole() {
        return role == null ? Role.USER : role;
    }
    public boolean isEnabled() {
        return enabled == null || enabled;
    }
    public LocalDateTime getIndate() {
        return indate;
    }

    //Setter
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public void setIndate(LocalDateTime indate) {
        this.indate = indate;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", enabled=" + enabled +
                ", indate=" + indate +
                '}';
    }
}
