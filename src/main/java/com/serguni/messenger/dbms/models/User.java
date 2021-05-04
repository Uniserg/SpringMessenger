package com.serguni.messenger.dbms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    @Id
    @SequenceGenerator(
            name = "users_generator",
            sequenceName = "users_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "users_generator")
    private long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "about_me")
    private String aboutMe;

    private byte[] avatar;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Session> sessions;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "usr_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_usr_id")
    )
    private Set<User> friends;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false)
    @PrimaryKeyJoinColumn
    private Configuration configuration;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<WatchedChat> watchedChats;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<TemporaryKey> temporaryKeys;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<BlockedUser> blockedUsers;

    @JsonIgnore
    @OneToMany(mappedBy = "toUser",fetch = FetchType.EAGER)
    private Set<FriendRequest> incomingFriendRequests;

    @JsonIgnore
    @OneToMany(mappedBy = "fromUser",fetch = FetchType.EAGER)
    private Set<FriendRequest> outgoingFriendRequests;


    @Column(name = "last_online")
    private Date lastOnline;


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        User user = (User) o;
//        return id == user.id && nickname.equals(user.nickname) && email.equals(user.email) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(aboutMe, user.aboutMe) && Arrays.equals(avatar, user.avatar);
//    }

//    public int hashCodeDto() {
//        int result = Objects.hash(id, nickname, email, firstName, lastName, aboutMe);
//        result = 31 * result + Arrays.hashCode(avatar);
//        return result;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && nickname.equals(user.nickname) && email.equals(user.email) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(aboutMe, user.aboutMe) && Arrays.equals(avatar, user.avatar);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, nickname, email, firstName, lastName, aboutMe);
        result = 31 * result + Arrays.hashCode(avatar);
        return result;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public void setSessions(Set<Session> sessions) {
        this.sessions = sessions;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Set<WatchedChat> getWatchedChats() {
        return watchedChats;
    }

    public void setWatchedChats(Set<WatchedChat> watchedChats) {
        this.watchedChats = watchedChats;
    }

    public Set<TemporaryKey> getTemporaryKeys() {
        return temporaryKeys;
    }

    public void setTemporaryKeys(Set<TemporaryKey> temporaryKeys) {
        this.temporaryKeys = temporaryKeys;
    }

    public Set<BlockedUser> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<BlockedUser> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public Set<FriendRequest> getIncomingFriendRequests() {
        return incomingFriendRequests;
    }

    public void setIncomingFriendRequests(Set<FriendRequest> incomingFriendRequests) {
        this.incomingFriendRequests = incomingFriendRequests;
    }

    public Set<FriendRequest> getOutgoingFriendRequests() {
        return outgoingFriendRequests;
    }

    public void setOutgoingFriendRequests(Set<FriendRequest> outgoingFriendRequests) {
        this.outgoingFriendRequests = outgoingFriendRequests;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                '}';
    }
}
