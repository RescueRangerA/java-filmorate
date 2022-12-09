package ru.yandex.practicum.filmorate.storage.userfriend;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.util.Graph;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserFriendStorage implements UserFriendStorage {
    private final Graph<Long, UserFriend> storage = new Graph<>();

    @Override
    public List<UserFriend> findAll() {
        return storage.getEdges();
    }

    @Override
    public Iterable<User> findFriendsOfUser(User user) {
        return storage.getEdgesOfVertex(user.getId()).values().stream().map(UserFriend::getToUser).collect(Collectors.toList());
    }

    @Override
    public UserFriend save(UserFriend entity) throws FriendOfHisOwnException {
        Long fromUserId = entity.getFromUser().getId();
        Long toUserId = entity.getToUser().getId();

        if (Objects.equals(fromUserId, toUserId)) {
            throw new FriendOfHisOwnException(entity);
        }

        storage.addVertex(fromUserId);
        storage.addVertex(toUserId);

        UserFriend userFriend = storage.getEdge(fromUserId, toUserId);
        if ( userFriend != null ) {
            return userFriend;
        }

        UserFriend maybeExistingUserFriendEntity = storage.getEdge(toUserId, fromUserId);

        if (maybeExistingUserFriendEntity != null) {
            storage.addEdge(fromUserId, toUserId, entity);
            storage.addEdge(toUserId, fromUserId, maybeExistingUserFriendEntity);
        } else {
            storage.addEdge(fromUserId, toUserId, entity);
        }

        return entity;
    }

    @Override
    public void delete(UserFriend entity) {
        storage.removeEdge(entity.getFromUser().getId(), entity.getToUser().getId());
    }

    @Override
    public Iterable<User> findFriendsInCommonOf2Users(User userA, User userB) {
        Set<User> friendsA = storage.getEdgesOfVertex(userA.getId()).values().stream().map(UserFriend::getToUser).collect(Collectors.toSet());
        Set<User> friendsB = storage.getEdgesOfVertex(userB.getId()).values().stream().map(UserFriend::getToUser).collect(Collectors.toSet());

        Set<User> result = new HashSet<>();

        for (User friendOfA: friendsA){
            for (User friendOfB: friendsB){
                if ( Objects.equals(friendOfA.getId(), friendOfB.getId()) ) {
                    result.add(friendOfA);
                }
            }
        }

        return new LinkedList<>(result);
    }
}
