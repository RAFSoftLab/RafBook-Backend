package raf.rs.voiceservice.cache;

import org.springframework.stereotype.Component;
import raf.rs.userservice.model.MyUser;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VoiceChannelCache {
    private final Map<Long, Set<MyUser>> channelUsers = new ConcurrentHashMap<>();

    public void addUserToChannel(Long channelId, MyUser myUser) {
        channelUsers.computeIfAbsent(channelId, k -> ConcurrentHashMap.newKeySet()).add(myUser);
    }

    public void removeUserFromChannel(Long channelId, MyUser myUser) {
        channelUsers.getOrDefault(channelId, ConcurrentHashMap.newKeySet()).remove(myUser);
    }

    public Set<MyUser> getUsersInChannel(Long channelId) {
        return channelUsers.getOrDefault(channelId, ConcurrentHashMap.newKeySet());
    }
}