package raf.rs.voiceservice.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import raf.rs.userservice.model.MyUser;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class VoiceChannelCache {
    private final Map<String, Set<MyUser>> channelUsers = new ConcurrentHashMap<>();

    public void addUserToChannel(String channelId, MyUser myUser) {
        log.info("Entering addUserToChannel with channelId: {}, myUser: {}", channelId, myUser);
        channelUsers.computeIfAbsent(channelId, k -> ConcurrentHashMap.newKeySet()).add(myUser);
        log.info("Exiting addUserToChannel: User added to channel");
    }

    public void removeUserFromChannel(String channelId, MyUser myUser) {
        log.info("Entering removeUserFromChannel with channelId: {}, myUser: {}", channelId, myUser);
        channelUsers.getOrDefault(channelId, ConcurrentHashMap.newKeySet()).remove(myUser);
        log.info("Exiting removeUserFromChannel: User removed from channel");
    }

    public Set<MyUser> getUsersInChannel(String channelId) {
        log.info("Entering getUsersInChannel with channelId: {}", channelId);
        Set<MyUser> users = channelUsers.getOrDefault(channelId, ConcurrentHashMap.newKeySet());
        log.info("Exiting getUsersInChannel with result: {}", users);
        return users;
    }
}