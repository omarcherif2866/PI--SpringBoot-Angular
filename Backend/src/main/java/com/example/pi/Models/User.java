package com.example.pi.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "users")
public class User implements  UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Getter
    public String nom;
    @Getter
    public String prenom;
    public String username;
    public String email;
    public String password;
    public String tel;
    public String image;
    @Enumerated(EnumType.STRING)
    public ERole role ;
    private String profilePicture;
    private boolean isActive;
    public boolean activated = true ;
    public String verificationCode ;
    public String verificationCodeCheck;
    private boolean activation2fa;
    private String twofactorcode;
    public boolean telVerif;
    private String telVerifcode;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "user_activite",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "activite_id")
    )
    Set<Activite> ListActivites;



    @ManyToMany
    @JoinTable(
            name = "user_club",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "club_id")
    )
    Set<Club> listClubs;

    @ManyToMany
    @JoinTable(
            name = "user_club",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "evenement_id")
    )
    Set<Evenement> ListEvenements;


    @OneToMany (mappedBy = "userId")
    Set<Covoiturage> ListCovoiturages ;



    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}