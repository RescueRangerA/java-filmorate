package ru.yandex.practicum.filmorate.storage.userfriend;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.util.Graph;

import java.util.*;

@Component
public class InMemoryUserFriendStorage implements UserFriendStorage {
    private final Graph<Long, UserFriend> storage = new Graph<>();

    @Override
    public List<UserFriend> findAll() {
        return storage.getEdges();
    }

    @Override
    public Iterable<Long> findFriendsOfUser(User user) {
        return new LinkedList<>(storage.getEdgesOfVertex(user.getId()).keySet());
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
            entity.setStatus(UserFriend.Status.CONFIRMED);
            maybeExistingUserFriendEntity.setStatus(UserFriend.Status.CONFIRMED);

            storage.addEdge(fromUserId, toUserId, entity);
            storage.addEdge(toUserId, fromUserId, maybeExistingUserFriendEntity);
        } else {
            entity.setStatus(UserFriend.Status.PENDING);
            storage.addEdge(fromUserId, toUserId, entity);
        }

        return entity;
    }

    @Override
    public void delete(UserFriend entity) {
        storage.removeEdge(entity.getFromUser().getId(), entity.getToUser().getId());
    }

    @Override
    public Iterable<Long> findFriendsInCommonOf2Users(User userA, User userB) {
        Set<Long> friendsA = storage.getEdgesOfVertex(userA.getId()).keySet();
        Set<Long> friendsB = storage.getEdgesOfVertex(userB.getId()).keySet();
        friendsA.retainAll(friendsB);

        return new LinkedList<>(friendsA);
    }
}
